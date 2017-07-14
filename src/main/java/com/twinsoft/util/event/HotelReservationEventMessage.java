package com.twinsoft.util.event;

import java.time.LocalDate;
import com.twinsoft.domain.RoomType;
import lombok.Data;

/**
 * HotelReservationEventMessage entity
 * 
 * @author Miodrag Pavkovic
 */
@Data
public class HotelReservationEventMessage {

	private final String hotelName;
	private final RoomType roomType;
	private final LocalDate startDate;
	private final LocalDate endDate;
	private final EventType eventType;
	public HotelReservationEventMessage(final String hotelName, final RoomType roomType, final LocalDate startDate, final LocalDate endDate, final EventType eventType) {
		this.hotelName = hotelName;
		this.roomType = roomType;
		this.startDate = startDate;
		this.endDate = endDate;
		this.eventType = eventType;
	}
	
}
