package com.travelbooking.api.car.controller;

import com.travelbooking.api.car.model.Car;
import com.travelbooking.api.car.service.CarRentalService;
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

@WebMvcTest(value = CarRentalController.class, properties = "spring.main.banner-mode=off")
class CarRentalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CarRentalService carRentalService;

    @Test
    void searchCarsReturnsServiceResponse() throws Exception {
        Car car = new Car(2001L, "Toyota Corolla", "Economy", 59.75, true);
        when(carRentalService.searchCars("Tokyo")).thenReturn(Collections.singletonList(car));

        mockMvc.perform(get("/cars/search").param("location", "Tokyo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2001))
                .andExpect(jsonPath("$[0].model").value("Toyota Corolla"))
                .andExpect(jsonPath("$[0].type").value("Economy"))
                .andExpect(jsonPath("$[0].available").value(true));

        verify(carRentalService).searchCars("Tokyo");
    }
}
