/**
 * 
 */
package com.twinsoft.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.twinsoft.domain.Hotel;

/**
 * The Hotel repository.
 *
 * @author Miodrag Pavkovic
 */
@Repository
public interface HotelRepository extends CrudRepository<Hotel, Long> {
	List<Hotel> findAll();
	Hotel findById(@Param("hotelid") Long hotelid);
}
