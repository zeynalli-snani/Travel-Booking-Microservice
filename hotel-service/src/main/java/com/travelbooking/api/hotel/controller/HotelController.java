package com.travelbooking.api.hotel.controller;


import com.travelbooking.api.hotel.model.Hotel;
import org.springframework.web.bind.annotation.*;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/hotels")
public class HotelController {

    @GetMapping("/search")
    public List<Hotel> searchHotels(@RequestParam String location) {
        return Arrays.asList(
                new Hotel(1L, "Grand Plaza", location, 150.0, 4.5),
                new Hotel(2L, "Seaside Resort", location, 200.0, 4.8)
        );
    }
}
