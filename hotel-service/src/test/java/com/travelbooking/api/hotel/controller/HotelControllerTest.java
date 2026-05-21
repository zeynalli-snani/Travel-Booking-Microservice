package com.travelbooking.api.hotel.controller;

import com.travelbooking.api.hotel.model.Hotel;
import com.travelbooking.api.hotel.service.HotelService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = HotelController.class, properties = "spring.main.banner-mode=off")
class HotelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HotelService hotelService;

    @Test
    void searchHotelsReturnsServiceResponse() throws Exception {
        Hotel hotel = new Hotel(1001L, "Downtown Stay", "Seattle", 219.0, 4.4);
        when(hotelService.searchHotels("Seattle")).thenReturn(Collections.singletonList(hotel));

        mockMvc.perform(get("/hotels/search").param("location", "Seattle"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1001))
                .andExpect(jsonPath("$[0].name").value("Downtown Stay"))
                .andExpect(jsonPath("$[0].location").value("Seattle"))
                .andExpect(jsonPath("$[0].pricePerNight").value(219.0));

        verify(hotelService).searchHotels("Seattle");
    }
}
