/**
 * 
 */
package com.twinsoft.domain;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Define HotelReservation predicates.
 * 
 * @author Miodrag Pavkovic
 */
public class HotelReservationPredicate   {
	public static Predicate<HotelReservation> matchHotelId(Hotel hotel) {
		return hotelReservation -> hotelReservation.getHotel().getId().equals(hotel.getId());
	}

	public static Predicate<HotelReservation> matchRoomType(RoomType roomType) {
		return hotelReservation -> hotelReservation.getRoomType().equals(roomType);
	}

	public static List<HotelReservation> filter(List<HotelReservation> reservations, Predicate<HotelReservation> predicate) {

		return reservations.stream().filter(predicate).collect(Collectors.toList());
	}
	
	public static List<HotelReservation> doubleFilter(List<HotelReservation> reservations,
			Predicate<HotelReservation> predicate, Predicate<HotelReservation> predicate2) {

		return reservations.stream().filter(predicate).filter(predicate2).collect(Collectors.toList());
	}

}
