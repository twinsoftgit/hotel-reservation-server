/**
 * 
 */
package com.twinsoft.domain;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Define HotelRoomType predicates.
 * 
 * @author Miodrag Pavkovic
 */
public class HotelRoomTypePredicate   {

	public static Predicate<HotelRoomType> matchRoomType(RoomType roomType) {
		return hotelRoomType -> hotelRoomType.getRoomType().equals(roomType);
	}

	public static List<HotelRoomType> filter(List<HotelRoomType> hotelRoomType, Predicate<HotelRoomType> predicate) {

		return hotelRoomType.stream().filter(predicate).collect(Collectors.toList());
	}
	
	public static List<HotelRoomType> filterWithLimit(List<HotelRoomType> hotelRoomType, Predicate<HotelRoomType> predicate, long size) {

		return hotelRoomType.stream().filter(predicate).limit(size).collect(Collectors.toList());
	}

}
