package com.travelbooking.api.car.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelbooking.api.car.model.Car;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarRentalServiceTest {

    @Mock
    private RestTemplate restTemplate;

    private CarRentalService carRentalService;

    @BeforeEach
    void setUp() {
        carRentalService = new CarRentalService(restTemplate, new ObjectMapper(), "https://example.test/cars.json", 2);
    }

    @Test
    void searchCarsFiltersByLocationPreferenceAndMapsCars() {
        String response = "[" +
                "{\"Name\":\"ford mustang\",\"Miles_per_Gallon\":18.0,\"Cylinders\":8,\"Horsepower\":140,\"Origin\":\"USA\"}," +
                "{\"Name\":\"toyota corolla\",\"Miles_per_Gallon\":33.0,\"Cylinders\":4,\"Horsepower\":95,\"Origin\":\"Japan\"}" +
                "]";
        when(restTemplate.getForObject("https://example.test/cars.json", String.class)).thenReturn(response);

        List<Car> cars = carRentalService.searchCars("New York");

        assertEquals(1, cars.size());
        Car car = cars.get(0);
        assertEquals("Ford Mustang", car.getModel());
        assertEquals("SUV", car.getType());
        assertTrue(car.isAvailable());
        assertTrue(car.getDailyRate() > 35.0);
    }

    @Test
    void searchCarsReturnsEmptyListWhenApiIsBlank() {
        when(restTemplate.getForObject("https://example.test/cars.json", String.class)).thenReturn(" ");

        List<Car> cars = carRentalService.searchCars("Paris");

        assertTrue(cars.isEmpty());
    }

    @Test
    void searchCarsAppliesConfiguredLimitWhenNoLocationFilterExists() {
        String response = "[" +
                "{\"Name\":\"volvo 240\",\"Miles_per_Gallon\":24.0,\"Cylinders\":4,\"Horsepower\":98,\"Origin\":\"Europe\"}," +
                "{\"Name\":\"honda civic\",\"Miles_per_Gallon\":31.0,\"Cylinders\":4,\"Horsepower\":88,\"Origin\":\"Japan\"}," +
                "{\"Name\":\"chevrolet impala\",\"Miles_per_Gallon\":20.0,\"Cylinders\":6,\"Horsepower\":120,\"Origin\":\"USA\"}" +
                "]";
        when(restTemplate.getForObject("https://example.test/cars.json", String.class)).thenReturn(response);

        List<Car> cars = carRentalService.searchCars("Baku");

        assertEquals(2, cars.size());
    }
}
