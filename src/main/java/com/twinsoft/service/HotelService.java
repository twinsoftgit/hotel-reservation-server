package com.twinsoft.service;

import java.util.List;

import com.twinsoft.domain.Hotel;

/**
 * HotelService interface
 *
 * @author Miodrag Pavkovic
 */
public interface HotelService  {
	
	/**
	 * Find specific hotel.
	 * 
	 * @param id to find the hotel with requested id
	 * @return hotel entity with requested id
	 */
	Hotel findByHotelId(Long id);
	/**
	 * Retrieves the list of all hotels.
	 * @return list of hotels
	 */
	List<Hotel> findAll();
	
	/**
	 * Create and save new hotel.
	 * @return Hotel entity
	 */
	Hotel save(Hotel hotel);
	
	/**
	 * Updates hotel.
	 * @return Hotel entity
	 */
	Hotel update(Hotel hotel);
	
	/**
	 * Delete hotel entity.
	 * @param id to delete hotel entity with requested id
	 */
	void delete(Long id);
	
	
}
