/**
 * 
 */
package com.twinsoft.web;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import com.google.common.collect.Lists;
import com.twinsoft.domain.Hotel;
import com.twinsoft.domain.HotelRating;
import com.twinsoft.domain.HotelRoomType;
import com.twinsoft.domain.RoomType;
import com.twinsoft.service.HotelRoomTypeService;
import com.twinsoft.service.HotelService;
import com.twinsoft.service.ManageHotelService;

/**
 * @author Miodrag Pavkovic
 */
@RunWith(SpringRunner.class)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
@WebMvcTest(controllers = { HotelController.class }, secure = false)
public class HotelControllerTest extends AbstractRestControllerTest {
	
	@Inject
    private MockMvc mockMvc;	
	@Inject
	private HotelService hotelService;
	@Inject
	private HotelRoomTypeService roomService;
	@Inject
	private ManageHotelService manageHotelService;
	
	/**
	 * @throws Exception
	 */
	@Test
    public void testFindAllHotels() throws Exception {    	
		final Hotel firstHotel = Hotel.builder().id(1L).name("Rossa De Mar").rating( HotelRating.THREE_STAR).totalRooms(Integer.valueOf(2)).build();	
		final Hotel secondHotel = Hotel.builder().id(2L).name("Rossa De Mar 2").rating( HotelRating.FOUR_STAR).totalRooms(Integer.valueOf(2)).build();
        when(hotelService.findAll()).thenReturn(Lists.newArrayList(firstHotel, secondHotel));
        mockMvc.perform(get("/api/hotels").accept(APPLICATION_JSON_UTF8))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].id", is(1)))
        .andExpect(jsonPath("$[0].name", is("Rossa De Mar")))
        .andExpect(jsonPath("$[0].rating", is(HotelRating.THREE_STAR.toString())))
        .andExpect(jsonPath("$[0].totalRooms", is(2)))
        .andExpect(jsonPath("$[1].id", is(2)))
        .andExpect(jsonPath("$[1].name", is("Rossa De Mar 2")))
        .andExpect(jsonPath("$[1].rating", is(HotelRating.FOUR_STAR.toString())))
        .andExpect(jsonPath("$[1].totalRooms", is(2)));     

    	verify(hotelService, times(1)).findAll();
    }	

	
	@Test
    public void testSaveHotel() throws Exception {    	
		final Hotel hotel = Hotel.builder().name("Rossa De Mar").rating( HotelRating.THREE_STAR).totalRooms(Integer.valueOf(2)).build();	
		final HotelRoomType hotelRoomType = HotelRoomType.builder().roomType(RoomType.SINGLE).price(BigDecimal.valueOf(100.00)).build();
		final List<HotelRoomType> hotelRoomTypes = Lists.newArrayList(hotelRoomType);
		hotel.setHotelRoomTypes(hotelRoomTypes);
		// Mock Hotel entity after saving
		final Hotel savedHotel = Hotel.builder().id(1L).name("Rossa De Mar").rating( HotelRating.THREE_STAR).totalRooms(Integer.valueOf(2)).build();	
        when(hotelService.save(hotel)).thenReturn(savedHotel);
        final HotelRoomType newHotelRoomType = HotelRoomType.builder().hotel(savedHotel).roomType(RoomType.SINGLE).price(BigDecimal.valueOf(100.00)).build();
        // Mock HotelRoomType entity after saving, set id
		final HotelRoomType savedHotelRoomType = HotelRoomType.builder().id(1L).hotel(savedHotel).roomType(RoomType.SINGLE).price(BigDecimal.valueOf(100.00)).build();
		when(roomService.save(newHotelRoomType)).thenReturn(savedHotelRoomType);
		savedHotel.setHotelRoomTypes(Lists.newArrayList(savedHotelRoomType));

        mockMvc.perform(post("/api/hotels")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(hotel))
        )
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isCreated());
                
    	verify(hotelService, times(1)).save(any(Hotel.class));
    }
	
	@Test
	public void testUpdateHotel() throws Exception {   
		final Long hotelId = 1L;
		final Hotel updateHotel = Hotel.builder().id(hotelId).name("Rossa De Mar").rating( HotelRating.THREE_STAR).totalRooms(Integer.valueOf(2)).build();	
		when(hotelService.findByHotelId(any(Long.class))).thenReturn(updateHotel);
		final Hotel updatedHotel = Hotel.builder().id(hotelId).name(updateHotel.getName()).rating(updateHotel.getRating()).totalRooms(updateHotel.getTotalRooms()).build();
		
		final HotelRoomType newHotelRoomType = HotelRoomType.builder().hotel(updatedHotel).roomType(RoomType.SINGLE).price(BigDecimal.valueOf(100.00)).build();
		updateHotel.setHotelRoomTypes(Lists.newArrayList(newHotelRoomType));
		when(hotelService.update(any(Hotel.class))).thenReturn(updatedHotel);
//        // Mock HotelRoomType entity after saving, set id
		final HotelRoomType savedHotelRoomType = HotelRoomType.builder().id(hotelId).hotel(updatedHotel).roomType(RoomType.SINGLE).price(BigDecimal.valueOf(100.00)).build();
		when(roomService.save(newHotelRoomType)).thenReturn(savedHotelRoomType);
		updatedHotel.setHotelRoomTypes(Lists.newArrayList(savedHotelRoomType));
        mockMvc.perform(put("/api/hotels/{hotelId}", hotelId)
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(updateHotel))
        )
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk());
	                
    	verify(hotelService, times(1)).update(any(Hotel.class));

	}
	
    @Test
    public void testDeleteHotel() throws Exception {
        final Long hotelId = 1L;
        final Hotel hotel = Hotel.builder().id(1L).name("Rossa De Mar").rating( HotelRating.THREE_STAR).totalRooms(Integer.valueOf(2)).build();
        when(hotelService.findByHotelId(any(Long.class))).thenReturn(hotel);
        
        mockMvc.perform(delete("/api/hotels/{hotelId}", hotelId).contentType(MediaType.APPLICATION_JSON)
            .content(convertObjectToJsonBytes(hotel)))
            .andExpect(status().isNoContent());
            
        verify(hotelService).delete(any(Long.class));
       
    }

	
}
