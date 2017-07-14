package com.twinsoft.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;
import javax.persistence.TransactionRequiredException;
import javax.validation.Valid;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.twinsoft.domain.Hotel;
import com.twinsoft.domain.HotelRating;
import com.twinsoft.domain.HotelRoomType;
import com.twinsoft.domain.RoomType;
import com.twinsoft.service.HotelRoomTypeService;
import com.twinsoft.service.HotelService;
import com.twinsoft.service.ManageHotelService;
import com.twinsoft.util.event.EventType;
import com.twinsoft.util.event.HotelEventMessage;
import com.twinsoft.util.exception.DeleteEntityException;
import com.twinsoft.util.exception.PersistEntityException;
import com.twinsoft.util.exception.ResourceNotFoundException;
import com.twinsoft.util.exception.UpdateEntityException;

import lombok.extern.slf4j.Slf4j;
/**
 * Rest controller for exposing Hotel endpoints.
 *
 * @author Miodrag Pavkovic
 */
@Slf4j
@RestController
@RequestMapping("api/hotels")
public class HotelController {
	private static final String RESOURCE_NOT_FOUND_MESSAGE = null;
	/** The hotel service */
	private final HotelService hotelService;
	
	private final ManageHotelService manageHotelService;
	
	private final HotelRoomTypeService roomService;
	 
	 /** The RabbitMQ template */
	private final RabbitTemplate rabbitTemplate;
	
	@Value("${hotelserver.amqp.exchange}")
	private String exchange;

	/** The contract createed routing key */
	@Value("${hotelserver.amqp.hotel-routing-key}")
    private String hotelRequestRoutingKey;



	@Inject
	public HotelController(final HotelService hotelService, final ManageHotelService manageHotelService,  final HotelRoomTypeService roomService, final RabbitTemplate rabbitTemplate) {
		this.hotelService = hotelService;
		this.manageHotelService = manageHotelService;
		this.roomService = roomService;
		this.rabbitTemplate = rabbitTemplate;
	}	

	/**
	 * Rest endpoint for retrieving all hotels.
	 *
	 * @param pageable
	 * @return ResponseEntity<List<Hotel>>
	 */
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Hotel>> findAll() {
		return new ResponseEntity<>(hotelService.findAll(), HttpStatus.OK);
	}
	
	/**
	 * Rest endpoint for retrieving  hotel by id.
	 *
	 * @return ResponseEntity<Hotel>
	 */
	@GetMapping(value="/{hotelId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Hotel> findByHotellId(@PathVariable final Long hotelId) {
		Optional.ofNullable(hotelService.findByHotelId(hotelId))
		.orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NOT_FOUND_MESSAGE));
		return new ResponseEntity<>(hotelService.findByHotelId(hotelId), HttpStatus.OK);
	}	
	/**
	 * Rest endpoint for creating and saving hotel.
	 * 
	 * @param hotel the hotel to create and save
	 * @param builder
	 * @return 
	 */
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	public HttpHeaders create(@Valid @RequestBody final Hotel hotel, final UriComponentsBuilder builder) {		
		try {
			final Hotel newHotel = Hotel.builder().name(hotel.getName()).rating(hotel.getRating()).totalRooms(hotel.getTotalRooms()).build();
			hotelService.save(newHotel);
			final List<HotelRoomType> newRoomTypes = setHotelRoomTypes(hotel.getHotelRoomTypes(), newHotel);
			newHotel.setHotelRoomTypes(newRoomTypes);		
			publishHotelEvent(newHotel, EventType.CREATE);
			
			final HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.setLocation(builder.path("/hotels/{hotelId}").buildAndExpand(newHotel.getId()).toUri());
			return httpHeaders;
		} catch (IllegalArgumentException | TransactionRequiredException e) {
			throw new PersistEntityException("create Hotel Error");
		}
	}
	

	/**
	 * Rest endpoint for updating a hotel with hotel id.
	 *	
	 * @param hotelId id of hotel to update
	 * @param hotel new hotel value 
	 * @return esponseEntity<Hotel>
	 */
	@PutMapping(value="/{hotelId}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Hotel> update(@PathVariable final Long hotelId, @Valid @RequestBody final Hotel hotel) {			
		
		Optional.ofNullable(hotelService.findByHotelId(hotelId))
				.orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NOT_FOUND_MESSAGE));
		try {
			final Hotel updateHotel = Hotel.builder().id(hotelId).name(hotel.getName()).rating(hotel.getRating()).totalRooms(hotel.getTotalRooms()).build();
			final Hotel updatedHotel = hotelService.update(updateHotel);
			final List<HotelRoomType> newRoomTypes = setHotelRoomTypes(hotel.getHotelRoomTypes(), updatedHotel);
			updatedHotel.setHotelRoomTypes(newRoomTypes);		
			publishHotelEvent(updatedHotel, EventType.UPDATE);
			return new ResponseEntity<>(updatedHotel, HttpStatus.OK);
		} catch (IllegalArgumentException | TransactionRequiredException e) {
			log.error("Exception occurred while updating hotel with id {}. Cause: ", hotelId, e);
			throw new UpdateEntityException();
		}
	}

	/**
	 * Rest endpoint for deleting a hotel with requested id.
	 *
	 * @param hotelId
	 */
	@DeleteMapping(value = "/{hotelId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable("hotelId") final Long hotelId) {
		final Hotel hotel = Optional
				.ofNullable(hotelService.findByHotelId(hotelId))
				.orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NOT_FOUND_MESSAGE));
		try {
			hotelService.delete(hotelId);
			publishHotelEvent(hotel, EventType.DELETE);
		} catch (final DataIntegrityViolationException e) {
			log.error("Exception occurred while deleting hotel entity with hotel id {}. Cause: ", hotelId, e);
			throw new DeleteEntityException("deleteError");
		}
	}
	
	/**
	 * Rest endpoint to check all hotels for available rooms with specified room type and rating.
	 *
	 * @param pageable
	 * @return ResponseEntity<List<Hotel>>
	 */
	@GetMapping(value="/checkAvailableRooms/{roomType}/{hotelRating}",  produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Map<Hotel, Boolean>> checkHotelAvailableRooms(@PathVariable("roomType") final RoomType roomType, @PathVariable("hotelRating") final HotelRating hotelRating) {
		return new ResponseEntity<>(manageHotelService.checkHotelsAvailableRooms(roomType, hotelRating), HttpStatus.OK);
	}
	

	/**
	 * Rest endpoint to get summary of total rooms for hotels.
	 *
	 * @param pageable
	 * @return ResponseEntity<List<Hotel>>
	 */
	@GetMapping(value="/summaryHotelsTotalRooms",  produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Map<Hotel, List<HotelRoomType>>> summaryHotelsTotalRooms() {
		return new ResponseEntity<>(manageHotelService.summaryHotelsTotalRooms(), HttpStatus.OK);
	}
	
	/**
	 * Rest endpoint to get summary of reserved rooms for hotels.
	 *
	 * @return  ResponseEntity<Map<Hotel, List<HotelRoomType>>> 
	 */
	@GetMapping(value="/summaryHotelsReservedRooms",  produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Map<Hotel, List<HotelRoomType>>> summaryHotelsReservedRooms() {
		return new ResponseEntity<>(manageHotelService.summaryHotelsReservedRooms(), HttpStatus.OK);
	}
	
	
	/**
	 * Rest endpoint to get summary of all available rooms for hotels.
	 *
	 * @return ResponseEntity<Map<Hotel, List<HotelRoomType>>> 
	 */
	@GetMapping(value="/summaryHotelsAvailableRooms",  produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Map<Hotel, List<HotelRoomType>>> summaryHotelsAvailableRooms() {
		return new ResponseEntity<>(manageHotelService.summaryHotelsAvailableRooms(), HttpStatus.OK);
	}
	

	/**
	 * Set missing properties for HotelRoomType.
	 * 
	 * @param oldRoomTypes
	 * @param hotel to set hotel property
	 * @return List<HotelRoomType> 
	 */
	private List<HotelRoomType> setHotelRoomTypes(final List<HotelRoomType> oldRoomTypes, final Hotel hotel) {
		// We only have data for price and room type  and need to set hotel property for HotelRoomType.			
		final List<HotelRoomType> newRoomTypes = new ArrayList<HotelRoomType>();
		oldRoomTypes.stream().forEach(rt -> {
			final HotelRoomType newRoomType = HotelRoomType.builder().hotel(hotel).price(rt.getPrice()).roomType(rt.getRoomType()).build();
			roomService.save(newRoomType);
			newRoomTypes.add(newRoomType);
		});
		return newRoomTypes;
	}
	
	/**
	 * Publish Hotel message events
	 * 
	 * @param newHotel
	 * @param eventType
	 */
	private void publishHotelEvent(Hotel newHotel, EventType eventType) {
		rabbitTemplate.setExchange(exchange);
		rabbitTemplate.convertAndSend(hotelRequestRoutingKey,
				new HotelEventMessage(newHotel.getId(), eventType));
	}
}
