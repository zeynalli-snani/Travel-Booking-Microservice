package com.travelbooking.api.flight.controller;

import com.travelbooking.api.flight.model.Flight;
import com.travelbooking.api.flight.service.FlightService;
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

@WebMvcTest(value = FlightController.class, properties = "spring.main.banner-mode=off")
class FlightControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FlightService flightService;

    @Test
    void searchFlightsReturnsServiceResponse() throws Exception {
        Flight flight = new Flight("AA-JFK-LAX-0", "American Airlines", "JFK", "LAX", 245.50);
        when(flightService.searchFlights("JFK", "LAX")).thenReturn(Collections.singletonList(flight));

        mockMvc.perform(get("/flights/search")
                        .param("origin", "JFK")
                        .param("destination", "LAX"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("AA-JFK-LAX-0"))
                .andExpect(jsonPath("$[0].airline").value("American Airlines"))
                .andExpect(jsonPath("$[0].price").value(245.50));

        verify(flightService).searchFlights("JFK", "LAX");
    }
}
