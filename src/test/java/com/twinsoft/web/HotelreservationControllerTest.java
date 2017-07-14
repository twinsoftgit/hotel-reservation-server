/**
 * 
 */
package com.twinsoft.web;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import com.google.common.collect.Lists;
import com.twinsoft.domain.Hotel;
import com.twinsoft.domain.HotelRating;
import com.twinsoft.domain.HotelReservation;
import com.twinsoft.domain.HotelRoomType;
import com.twinsoft.domain.RoomType;
import com.twinsoft.service.HotelReservationService;
import com.twinsoft.service.HotelRoomTypeService;
import com.twinsoft.service.HotelService;
import com.twinsoft.service.ManageHotelService;

/**
 * @author Miodrag Pavkovic
 */
@RunWith(SpringRunner.class)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
@WebMvcTest(controllers = { HotelReservationController.class }, secure = false)
public class HotelreservationControllerTest extends AbstractRestControllerTest {
	
	@Inject
    private MockMvc mockMvc;	
	@Inject
	private HotelService hotelService;
	@Inject
	private HotelReservationService hotelReservationService;

	
	/**
	 * @throws Exception
	 */
	@Test
    public void testFindAllHotelReservations() throws Exception {    	
		final Hotel firstHotel = Hotel.builder().id(1L).name("Rossa De Mar").rating( HotelRating.THREE_STAR).totalRooms(Integer.valueOf(2)).build();	
		final Hotel secondHotel = Hotel.builder().id(2L).name("Rossa De Mar 2").rating( HotelRating.FOUR_STAR).totalRooms(Integer.valueOf(2)).build();
		final HotelReservation hotelReservation1 = HotelReservation.builder()
				.id(1L)
				.hotel(firstHotel)
				.roomType(RoomType.DOUBLE)
				.startDate(LocalDate.of(2017, 8, 11))
				.endDate(LocalDate.of(2017, 8, 21))
				.reservationPrice(BigDecimal.valueOf(1000.00))
				.build();
		final HotelReservation hotelReservation2 = HotelReservation.builder()
				.id(2L)
				.hotel(secondHotel)
				.roomType(RoomType.DOUBLE)
				.startDate(LocalDate.of(2017, 10, 11))
				.endDate(LocalDate.of(2017, 10, 21))
				.reservationPrice(BigDecimal.valueOf(1400.00))
				.build();
        when(hotelReservationService.findAll()).thenReturn(Lists.newArrayList(hotelReservation1, hotelReservation2));
        mockMvc.perform(get("/api/hotelreservations").accept(APPLICATION_JSON_UTF8))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].id", is(1)))
        .andExpect(jsonPath("$[0].hotel.name", is("Rossa De Mar")))
        .andExpect(jsonPath("$[0].hotel.rating", is(HotelRating.THREE_STAR.toString())))
        .andExpect(jsonPath("$[0].hotel.totalRooms", is(2)))
        .andExpect(jsonPath("$[0].roomType", is(RoomType.DOUBLE.toString())))
		.andExpect(jsonPath("$[0].reservationPrice", is(1000.00)))
        .andExpect(jsonPath("$[1].id", is(2)))
        .andExpect(jsonPath("$[1].hotel.name", is("Rossa De Mar 2")))
        .andExpect(jsonPath("$[1].hotel.rating", is(HotelRating.FOUR_STAR.toString())))
        .andExpect(jsonPath("$[1].hotel.totalRooms", is(2)))
        .andExpect(jsonPath("$[1].roomType", is(RoomType.DOUBLE.toString())))
		.andExpect(jsonPath("$[1].reservationPrice", is(1400.00)));    

    	verify(hotelReservationService, times(1)).findAll();
    	verifyNoMoreInteractions(hotelReservationService);
    }	

	
	@Test
    public void testSaveHotelReservation() throws Exception {    	
		final Hotel hotel = Hotel.builder().name("Rossa De Mar").rating( HotelRating.THREE_STAR).totalRooms(Integer.valueOf(2)).build();	
		final HotelRoomType hotelRoomType = HotelRoomType.builder().roomType(RoomType.SINGLE).price(BigDecimal.valueOf(100.00)).build();
		final List<HotelRoomType> hotelRoomTypes = Lists.newArrayList(hotelRoomType);
		hotel.setHotelRoomTypes(hotelRoomTypes);
		final HotelReservation hotelReservation = HotelReservation.builder()
				.hotel(hotel)
				.roomType(RoomType.SINGLE)
				.startDate(LocalDate.of(2017, 8, 11))
				.endDate(LocalDate.of(2017, 8, 21))
				.reservationPrice(BigDecimal.valueOf(1000.00))
				.build();
		// Mock Hotel reservation entity after saving
		final HotelReservation savedHotelReservation = HotelReservation.builder()
				.id(1L)
				.hotel(hotel)
				.roomType(RoomType.SINGLE)
				.startDate(LocalDate.of(2017, 8, 11))
				.endDate(LocalDate.of(2017, 8, 21))
				.reservationPrice(BigDecimal.valueOf(1000.00))
				.build();
        when(hotelReservationService.save(any(HotelReservation.class))).thenReturn(savedHotelReservation);
        when(hotelService.findByHotelId(anyLong())).thenReturn(hotel);

        mockMvc.perform(post("/api/hotelreservations")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(hotelReservation))
        )
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isCreated());
                
    	verify(hotelReservationService, times(1)).save(any(HotelReservation.class));
    	verifyNoMoreInteractions(hotelReservationService);
    }

	@Test
    public void testUpdateHotelReservation() throws Exception {    	
		final Hotel hotel = Hotel.builder().name("Rossa De Mar").rating( HotelRating.THREE_STAR).totalRooms(Integer.valueOf(2)).build();	
		final HotelRoomType hotelRoomType = HotelRoomType.builder().roomType(RoomType.SINGLE).price(BigDecimal.valueOf(100.00)).build();
		final List<HotelRoomType> hotelRoomTypes = Lists.newArrayList(hotelRoomType);
		hotel.setHotelRoomTypes(hotelRoomTypes);
		final HotelReservation hotelReservation = HotelReservation.builder()
				.hotel(hotel)
				.roomType(RoomType.SINGLE)
				.startDate(LocalDate.of(2017, 8, 11))
				.endDate(LocalDate.of(2017, 8, 21))
				.reservationPrice(BigDecimal.valueOf(1000.00))
				.build();
		// Mock Hotel reservation entity after saving
		final HotelReservation savedHotelReservation = HotelReservation.builder()
				.id(1L)
				.hotel(hotel)
				.roomType(RoomType.SINGLE)
				.startDate(LocalDate.of(2017, 8, 11))
				.endDate(LocalDate.of(2017, 8, 21))
				.reservationPrice(BigDecimal.valueOf(1000.00))
				.build();
        when(hotelReservationService.update(any(HotelReservation.class))).thenReturn(savedHotelReservation);
        when(hotelService.findByHotelId(anyLong())).thenReturn(hotel);

        mockMvc.perform(put("/api/hotelreservations/{hotelReservationId}", 1L)
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(hotelReservation))
        )
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk());
                
    	verify(hotelReservationService, times(1)).update(any(HotelReservation.class));
    	verifyNoMoreInteractions(hotelReservationService);
    }
	


}
