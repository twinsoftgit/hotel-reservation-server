package com.twinsoft.domain;

/**
 * Enum class for the hotel rating represented as hotel stars from 1 to 5. 
 *
 * @author Miodrag Pavkovic
 */
public enum HotelRating {
	ONE_STAR(1),
    TWO_STAR(2),
    THREE_STAR(3),
    FOUR_STAR(4),
    FIVE_STAR(5);
    private final int rating;
    private HotelRating(int rating) {
        this.rating = rating;
    }
    public int getRating() {
        return rating;
    }
}
