package com.twinsoft.service;

import java.util.List;

import com.twinsoft.domain.HotelReservation;

/**
 * The hotel reservation service.
 * 
 * @author Miodrag Pavkovic
 */
public interface HotelReservationService {
	/**
	 * Find specific hotel.
	 * 
	 * @param id to find the hotel with requested id
	 * @return hotel entity with requested id
	 */
	HotelReservation findByHoteReservationlId(Long id);
	/**
	 * Retrieves the list of all hotel reservations.
	 * @return list of hotel reservations
	 */
	List<HotelReservation> findAll();
	
	/**
	 * Create and save new hotel reservation.
	 * @return HotelReservation entity
	 */
	HotelReservation save(HotelReservation hotel);
	
	/**
	 * Delete hotel reservation entity.
	 * @param id to delete hotel reservation entity with requested id
	 */
	void delete(Long id);
	/**
	 * @param hotelReservation
	 * @return
	 */
	HotelReservation update(HotelReservation hotelReservation);
}
