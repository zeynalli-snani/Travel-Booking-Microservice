package com.travelbooking.api.flight.controller;

import com.travelbooking.api.flight.model.Flight;
import org.springframework.web.bind.annotation.*;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/flights")
public class FlightController {

    @GetMapping("/search")
    public List<Flight> searchFlights(@RequestParam String origin, @RequestParam String destination) {
        // Dummy data for testing the connection
        return Arrays.asList(
                new Flight("1", "Global Air", origin, destination, 450.00),
                new Flight("2", "SkyHigh", origin, destination, 520.00)
        );
    }
}