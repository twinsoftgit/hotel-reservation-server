package com.twinsoft.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.twinsoft.domain.HotelRoomType;

/**
 * The HotelRoomType repository.
 *
 * @author Miodrag Pavkovic
 */
@Repository
public interface HotelRoomTypeRepository extends CrudRepository<HotelRoomType, Long> {

}
