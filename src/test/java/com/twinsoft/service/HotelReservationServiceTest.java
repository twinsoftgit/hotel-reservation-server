/**
 * 
 */
package com.twinsoft.service;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.Lists;
import com.twinsoft.domain.Hotel;
import com.twinsoft.domain.HotelReservation;
import com.twinsoft.domain.RoomType;
import com.twinsoft.repository.HotelReservationRepository;

/**
 * HotelReservationServiceTest tests for HotelReservationService
 * 
 * @author Miodrag Pavkovic
 */
public class HotelReservationServiceTest {

	@Mock
	private HotelReservationRepository hotelReservationRepository;
	@InjectMocks
	private HotelReservationServiceImpl hotelReservationService;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		hotelReservationService = new HotelReservationServiceImpl(hotelReservationRepository);
	}	
	
	/**
	 * Test for retrieving hotel reservation.
	 */
	@Test
	public void findByHoteReservationlId() {
		when(hotelReservationService.findByHoteReservationlId(any(Long.class))).thenReturn(createHotelReservation());
		hotelReservationService.findByHoteReservationlId(any(Long.class));
		verify(hotelReservationRepository).findById(any(Long.class));
	}
	

	/**
	 * Test for retrieving all of hotel reservations.
	 */
	@Test
	public void findAll() {

		final List<HotelReservation> data = new ArrayList<>(Lists.newArrayList(createHotelReservation()));
		when(hotelReservationService.findAll()).thenReturn(data);
		hotelReservationService.findAll();
		verify(hotelReservationRepository).findAll();
	}
	
	/**
	 * Test for saving hotel reservation.
	 */
	@Test
	public void save() {
		when(hotelReservationRepository.save(any(HotelReservation.class))).thenReturn(createHotelReservation());
		hotelReservationService.save(any(HotelReservation.class));
		verify(hotelReservationRepository).save(any(HotelReservation.class));
	}

	/**
	 * Test for updating a HotelReservation.
	 */
	@Test
	public void update() {
		when(hotelReservationRepository.save(any(HotelReservation.class))).thenReturn(getHotelReservation(1L));
		hotelReservationService.save(any(HotelReservation.class));
		verify(hotelReservationRepository).save(any(HotelReservation.class));
	}

	 /**
	 * Test for delete a HotelReservation.
	 */
	 @Test
	 public void delete() {
		 doNothing().when(hotelReservationRepository).delete(any(Long.class));
		 hotelReservationRepository.delete(1L);
		 verify(hotelReservationRepository).delete(any(Long.class));
	
	 }
	
	
	private HotelReservation getHotelReservation(long reservationId) {
		return new HotelReservation(reservationId, new Hotel(), RoomType.SINGLE,
				LocalDate.of(2017, 07, 04), LocalDate.of(2017, 07, 14), BigDecimal.valueOf(1000));
	}

	private HotelReservation createHotelReservation() {

		return new HotelReservation(1L, new Hotel(), RoomType.SINGLE,
				LocalDate.of(2017, 07, 04), LocalDate.of(2017, 07, 14), BigDecimal.valueOf(1000));
	}

}
