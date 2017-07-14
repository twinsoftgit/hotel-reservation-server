package com.twinsoft.service;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.twinsoft.domain.HotelReservation;
import com.twinsoft.repository.HotelReservationRepository;


/**
 * Implementation of hotel reservation service.
 * 
 * @author Miodrag Pavkovic
 */
@Service
public class HotelReservationServiceImpl implements HotelReservationService {
	
	private final HotelReservationRepository hotelReservationRepository;
	
	/**
	 * @param hotelReservationRepository
	 */
	@Inject
	public HotelReservationServiceImpl(HotelReservationRepository hotelReservationRepository) {
		this.hotelReservationRepository = hotelReservationRepository;
	}	

	/* (non-Javadoc)
	 * @see com.twinsoft.service.HotelReservationService#findByHoteReservationlId(java.lang.Long)
	 */
	@Override
	public HotelReservation findByHoteReservationlId(Long id) {
		return hotelReservationRepository.findById(id);
	}

	/* (non-Javadoc)
	 * @see com.twinsoft.service.HotelReservationService#findAll()
	 */
	@Override
	public List<HotelReservation> findAll() {
		return hotelReservationRepository.findAll();
	}

	/* (non-Javadoc)
	 * @see com.twinsoft.service.HotelReservationService#save(com.twinsoft.domain.HotelReservation)
	 */
	@Override
	public HotelReservation save(HotelReservation hotelReservation) {
		return hotelReservationRepository.save(hotelReservation);
	}

	
	/* (non-Javadoc)
	 * @see com.twinsoft.service.HotelReservationService#delete(java.lang.Long)
	 */
	@Override
	public void delete(final Long id) {
		hotelReservationRepository.delete(id);		
	}

	/* (non-Javadoc)
	 * @see com.twinsoft.service.HotelReservationService#update(com.twinsoft.domain.HotelReservation)
	 */
	@Override
	public HotelReservation update(HotelReservation hotelReservation) {
		// TODO Auto-generated method stub
		return hotelReservationRepository.save(hotelReservation);
	}
	

	
}
