package com.twinsoft.service;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.twinsoft.domain.HotelRoomType;
import com.twinsoft.repository.HotelRoomTypeRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * HotelRoomTypeService implementation.
 * 
 * @author Miodrag Pavkovic
 */
@Service("HotelRoomTypeService")
@Slf4j
public class HotelRoomTypeServiceImpl implements HotelRoomTypeService {
	
	private HotelRoomTypeRepository repository;
	
	@Inject
	public HotelRoomTypeServiceImpl(final HotelRoomTypeRepository repository){
		this.repository = repository;
	}

	@Override
	public HotelRoomType save(HotelRoomType newRoomType) {
		final HotelRoomType hotelRoomType = repository.save(newRoomType);
		return hotelRoomType;
	}
}
