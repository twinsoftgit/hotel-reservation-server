package com.twinsoft.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Hotel domain class. The hotels must have the following definitions :
 *  - Hotel Stars (how many stars the hotel has, 1 to 5) 
 *  - Hotel total rooms 
 *  - Hotel rooms types (single, double)
 *
 * @author Miodrag Pavkovic
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EqualsAndHashCode(callSuper = false)
@Builder
@ToString(exclude = "hotelRoomTypes")
public class Hotel implements Serializable {

	private static final long serialVersionUID = 8690390386555199353L;

	/** The hotel id. */
	@Id
	@GeneratedValue
	private Long id;

	@NotNull
	private String name;

	/** Number of rooms **/
	@NotNull
	private Integer totalRooms;

	/** Hotel rating - stars **/
	@NotNull
	@Enumerated(EnumType.STRING)
	private HotelRating rating;

	/** The hotel room types. */
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "hotel", cascade = CascadeType.ALL)
	@JsonIgnore
	private List<HotelRoomType> hotelRoomTypes = new ArrayList<>();

}
