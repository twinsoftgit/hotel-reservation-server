/**
 * 
 */
package com.twinsoft.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.twinsoft.domain.HotelReservation;

/**
 * The HotelReservation repository.
 * 
 * @author Miodrag Pavkovic
 */
@Repository
public interface HotelReservationRepository extends CrudRepository<HotelReservation, Long> {
	
	List<HotelReservation> findAll();
	HotelReservation findById(@Param("hotelreservationid") Long hotelreservationid);
	
	List<HotelReservation> findAllByStartDateBeforeAndEndDateAfter(LocalDate currentDate);
}
