package com.twinsoft.util.event;

import lombok.Data;

/**
 * HotelEventMessage entity.
 * 
 * @author Miodrag Pavkovic
 */
@Data
public class HotelEventMessage {

	private final Long id;
	private final EventType eventType;
	public HotelEventMessage(final Long id, final EventType eventType) {
		this.id = id;
		this.eventType = eventType;
	}
	
}
