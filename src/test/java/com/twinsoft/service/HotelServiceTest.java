/**
 * 
 */
package com.twinsoft.service;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.data.redis.core.RedisTemplate;

import com.google.common.collect.Lists;
import com.twinsoft.domain.Hotel;
import com.twinsoft.domain.HotelRating;
import com.twinsoft.domain.HotelRoomType;
import com.twinsoft.domain.RoomType;
import com.twinsoft.repository.HotelRepository;

/**
 * HotelServiceTest tests for HotelService.
 * 
 * @author Miodrag Pavkovic
 */
public class HotelServiceTest {
	
	@Mock
	private HotelRepository hotelRepository;
	@InjectMocks
	private HotelServiceImpl hotelService;
	@Spy
	private RedisTemplate<String, Hotel> redisTemplate = new RedisTemplate<String, Hotel>();
	
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		hotelService = new HotelServiceImpl(hotelRepository, redisTemplate, 1L);
	}	
	

	/**
	 * Test for retrieving all of hotels.
	 */
	@Test
	public void findAll() {

		final List<Hotel> data = new ArrayList<>(Lists.newArrayList(createHotel()));
		when(hotelService.findAll()).thenReturn(data);
		hotelService.findAll();
		verify(hotelRepository).findAll();
	}
	
	/**
	 * Test for saving hotel.
	 */
	@Test
	public void save() {
		when(hotelRepository.save(any(Hotel.class))).thenReturn(createHotel());
		hotelService.save(any(Hotel.class));
		verify(hotelRepository).save(any(Hotel.class));
	}


	 /**
	 * @return
	 */
	private Hotel createHotel() {
		// TODO Auto-generated method stub
		return new Hotel(1L, "Rossa De Mar", Integer.valueOf(1), HotelRating.FOUR_STAR, getHotelRoomTypes());
	}


	/**
	 * @return
	 */
	private List<HotelRoomType> getHotelRoomTypes() {
		return new ArrayList<HotelRoomType>(Lists.newArrayList(createHotelRoomType()));
	}


	/**
	 * @return
	 */
	private HotelRoomType createHotelRoomType() {
		return new HotelRoomType(1L, new Hotel(), RoomType.SINGLE, BigDecimal.valueOf(100.00));
	}





}
