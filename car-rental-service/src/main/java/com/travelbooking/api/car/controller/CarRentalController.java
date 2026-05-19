package com.travelbooking.api.car.controller;

import com.travelbooking.api.car.model.Car;
import com.travelbooking.api.car.service.CarRentalService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cars")
public class CarRentalController {
    private final CarRentalService carRentalService;

    public CarRentalController(CarRentalService carRentalService) {
        this.carRentalService = carRentalService;
    }

    @GetMapping("/search")
    public List<Car> searchCars(@RequestParam String location) {
        return carRentalService.searchCars(location);
    }
}
