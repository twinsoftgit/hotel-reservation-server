package com.twinsoft.domain;

/**
 * Represent room type. It can be single or double room type.
 * 
 * @author Miodrag Pavkovic
 */
public enum RoomType {
	SINGLE("Single"),
	DOUBLE("Double");
	private final String value;
    private RoomType(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
}
