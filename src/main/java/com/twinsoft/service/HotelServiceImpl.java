/**
 * 
 */
package com.twinsoft.service;

import java.util.List;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.twinsoft.domain.Hotel;
import com.twinsoft.repository.HotelRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of hotel service.
 * 
 * @author Miodrag Pavkovic
 */
@Service
@Slf4j
public class HotelServiceImpl implements HotelService {

	private final RedisTemplate<String, Hotel> redisTemplate;

	private final Long cacheTtl;

	private final HotelRepository repository;

	@Inject
	public HotelServiceImpl(HotelRepository repository,
			@Qualifier("hotelRedisTemplate") final RedisTemplate<String, Hotel> redisTemplate,
			@Value("${spring.redis.hotel.cache-ttl}") final Long cacheTtl) {
		this.repository = repository;
		this.redisTemplate = redisTemplate;
		this.cacheTtl = cacheTtl;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.twinsoft.service.HotelService#findAll()
	 */
	@Override
	@CachePut(value = "Hotel", keyGenerator = "keyGenerator")
	public List<Hotel> findAll() {
		return repository.findAll();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.twinsoft.service.HotelService#findOne(java.lang.String)
	 */
	@Override
	@CachePut(value = "Hotel", keyGenerator = "keyGenerator")
	public Hotel findByHotelId(Long id) {
		return repository.findById(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.twinsoft.service.HotelService#save(com.twinsoft.domain.Hotel)
	 */
	@Override
	@CacheEvict(value = "Hotel", allEntries = true)
	public Hotel save(Hotel hotel) {
		return repository.save(hotel);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.twinsoft.service.HotelService#update(com.twinsoft.domain.Hotel)
	 */
	@Override
	@CachePut(value = "Hotel", keyGenerator = "keyGenerator")
	public Hotel update(Hotel hotel) {
		// update the content of the cache without interfering the method execution. That is, the method would always be
		// executed and the result cached.
		return repository.save(hotel);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.twinsoft.service.HotelService#delete(java.lang.String)
	 */
	@Override
	@CacheEvict(value = "Hotel", allEntries = true)
	public void delete(final Long id) {
		repository.delete(id);
	}

}
