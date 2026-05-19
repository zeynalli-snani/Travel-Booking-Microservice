package com.travelbooking.api.car.controller;

import com.travelbooking.api.car.model.Car;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/cars")
public class CarRentalController {

    @GetMapping("/search")
    public List<Car> searchCars(@RequestParam String location) {
        return Arrays.asList(
                new Car(1L, "Toyota Camry", "Economy", 45.0, true),
                new Car(2L, "Jeep Wrangler", "SUV", 85.0, true)
        );
    }
}
