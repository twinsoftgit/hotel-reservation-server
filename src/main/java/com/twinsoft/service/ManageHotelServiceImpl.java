/**
 * 
 */
package com.twinsoft.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.twinsoft.domain.Hotel;
import com.twinsoft.domain.HotelRating;
import com.twinsoft.domain.HotelReservation;
import com.twinsoft.domain.HotelReservationPredicate;
import com.twinsoft.domain.HotelRoomType;
import com.twinsoft.domain.HotelRoomTypePredicate;
import com.twinsoft.domain.RoomType;
import com.twinsoft.repository.HotelRepository;
import com.twinsoft.repository.HotelReservationRepository;

/**
 * @author Miodrag Pavkovic
 */
@Service
public class ManageHotelServiceImpl implements ManageHotelService {

	private final HotelRepository hotelRepository;
	private final HotelReservationRepository hotelReservationRepository;
	/**
	 * @param hotelRepository
	 * @param hotelReservationRepository
	 */
	@Inject
	public ManageHotelServiceImpl(HotelRepository hotelRepository,
			HotelReservationRepository hotelReservationRepository) {
		this.hotelRepository = hotelRepository;
		this.hotelReservationRepository = hotelReservationRepository;
	}


	/* (non-Javadoc)
	 * @see com.twinsoft.service.ManageHotelService#checkHotelAvailableRooms(com.twinsoft.domain.RoomType, com.twinsoft.domain.HotelRating)
	 */
	@Override
	public Map<Hotel, Boolean> checkHotelsAvailableRooms(RoomType roomType, HotelRating hotelRating) {
		List<Hotel> hotels = hotelRepository.findAll();
		return hotels.stream()
				.collect(Collectors.toMap(hotel -> hotel, hotel -> checkHotelAvailableRooms(hotel, roomType, hotelRating)));
		
	}


	/* (non-Javadoc)
	 * @see com.twinsoft.service.ManageHotelService#availableHotelRooms()
	 */
	@Override
	public  Map<Hotel, List<HotelRoomType>> summaryHotelsAvailableRooms() {
		List<Hotel> hotels =  hotelRepository.findAll();
		return hotels.stream().collect(Collectors.toMap(hotel -> hotel, hotel -> findAvailableHotelRooms(hotel)));

	}

	/* (non-Javadoc)
	 * @see com.twinsoft.service.ManageHotelService#reservedHotelRooms(com.twinsoft.domain.Hotel)
	 */
	@Override
	public Map<Hotel, List<HotelRoomType>> summaryHotelsReservedRooms() {
		List<Hotel> hotels =  hotelRepository.findAll();
		return hotels.stream().collect(Collectors.toMap(hotel -> hotel, hotel -> findHotelReservations(hotel)));
	}
	
	/* (non-Javadoc)
	 * @see com.twinsoft.service.ManageHotelService#hotelTotalRooms()
	 */
	@Override
	public Map<Hotel, List<HotelRoomType>> summaryHotelsTotalRooms() {
		List<Hotel> hotels =  hotelRepository.findAll();
		return hotels.stream().collect(Collectors.toMap(hotel -> hotel, hotel -> hotel.getHotelRoomTypes()));
	}
	
	
	/**
	 * Check if the hotel has available rooms by specifying room type and hotel stars (rating).
	 * 
	 * @param hotel
	 * @param roomType
	 * @param hotelRating
	 * @return
	 */
	private boolean checkHotelAvailableRooms(Hotel hotel, RoomType roomType, HotelRating hotelRating) {
		List<HotelReservation> hotelReservations = getMatchingHotelReservations(hotel, roomType);
		List<HotelRoomType> matchingHotelRoomTypes = HotelRoomTypePredicate.filter(hotel.getHotelRoomTypes(), HotelRoomTypePredicate.matchRoomType(roomType));
		if (hotelReservations.size() < matchingHotelRoomTypes.size() ) {
			return true;
		}
		return false;
	}

	/**	 * 
	 * Return hotelReservation for specified hotel and roomType.
	 *  
	 * @param hotel
	 * @param roomType
	 * @return
	 */
	private List<HotelReservation> getMatchingHotelReservations(Hotel hotel, RoomType roomType) {
		List<HotelReservation> reservations = hotelReservationRepository.findAllByStartDateBeforeAndEndDateAfter(LocalDate.now());
		return HotelReservationPredicate.doubleFilter(reservations, HotelReservationPredicate.matchHotelId(hotel), HotelReservationPredicate.matchRoomType(roomType));
	}
	
	/**
	 * @param hotel
	 * @return
	 */
	private List<HotelRoomType> findAvailableHotelRooms(Hotel hotel) {
		// Find active reservations.
		List<HotelReservation> reservations = hotelReservationRepository.findAllByStartDateBeforeAndEndDateAfter(LocalDate.now());	
		// Filter active reservations for specific hotel
		List<HotelReservation> hotelReservations = HotelReservationPredicate.filter(reservations, HotelReservationPredicate.matchHotelId(hotel));
		// List of hotel rooms.
		List<HotelRoomType> totalHotelRooms = hotel.getHotelRoomTypes();
		// Find available hotel rooms single and double.
		List<HotelRoomType>  availableHotelRooms = availableHotelRoomTypes(hotelReservations, hotel, RoomType.SINGLE, totalHotelRooms);		
		availableHotelRooms.addAll(availableHotelRoomTypes(hotelReservations, hotel, RoomType.DOUBLE, totalHotelRooms));
		return availableHotelRooms;
	}

	/**
	 * Find all reservation for given hotel.
	 * @param hotel
	 */
	private List<HotelRoomType> findHotelReservations(Hotel hotel) {
		// Find active reservations.
		List<HotelReservation> reservations = hotelReservationRepository.findAllByStartDateBeforeAndEndDateAfter(LocalDate.now());		
		// Filter active reservations for specific hotel
		List<HotelReservation> hotelReservations = HotelReservationPredicate.filter(reservations, HotelReservationPredicate.matchHotelId(hotel));
		// List of hotel rooms.
		List<HotelRoomType> totalHotelRooms = hotel.getHotelRoomTypes();
		List<HotelRoomType>  reservedHotelRooms = reservedHotelRoomTypes(hotelReservations, RoomType.SINGLE, totalHotelRooms);
		
		reservedHotelRooms.addAll(reservedHotelRoomTypes(hotelReservations, RoomType.DOUBLE, totalHotelRooms));
		return reservedHotelRooms;
	}
	
	/**
	 * @param hotelReservations
	 * @param hotel 
	 * @param roomType
	 * @param totalSingleHotelRooms
	 * @return
	 */
	private List<HotelRoomType> availableHotelRoomTypes (List<HotelReservation> hotelReservations, Hotel hotel, RoomType roomType, List<HotelRoomType> hotelRoomTypes) { 
		long countReservedRooms = hotelReservations.stream().filter(HotelReservationPredicate.matchRoomType(roomType)).count();
		// Total number of hotel rooms specific room type.
		long totalHotelRooms = hotel.getHotelRoomTypes().stream().filter(HotelRoomTypePredicate.matchRoomType(roomType)).count();
		return HotelRoomTypePredicate.filterWithLimit(hotelRoomTypes, HotelRoomTypePredicate.matchRoomType(roomType), totalHotelRooms- countReservedRooms);
	}
	
	/**
	 * @param hotelReservations
	 * @param hotel 
	 * @param roomType
	 * @param totalHotelRooms
	 * @return
	 */
	private List<HotelRoomType> reservedHotelRoomTypes (List<HotelReservation> hotelReservations, RoomType roomType, List<HotelRoomType> hotelRoomTypes) {
		long countReservedRooms = hotelReservations.stream().filter(HotelReservationPredicate.matchRoomType(roomType)).count();
		return HotelRoomTypePredicate.filterWithLimit(hotelRoomTypes, HotelRoomTypePredicate.matchRoomType(roomType), countReservedRooms);
	}

	

}
