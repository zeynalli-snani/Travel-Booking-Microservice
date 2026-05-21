package com.travelbooking.api.hotel.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelbooking.api.hotel.model.Hotel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HotelServiceTest {

    @Mock
    private RestTemplate restTemplate;

    private HotelService hotelService;

    @BeforeEach
    void setUp() {
        hotelService = new HotelService(restTemplate, new ObjectMapper(), "https://example.test/hotels.json", 2);
    }

    @Test
    void searchHotelsReturnsMappedApiResultsForMatchingLocation() {
        String response = "{\"hotels\":[{\"id\":\"la-1\",\"hotelId\":5001,\"name\":\"Sunset Suites\",\"city\":\"Los Angeles\",\"address1\":\"Downtown LA\",\"locationDescription\":\"Central LA\",\"stateProvinceCode\":\"CA\",\"lowRate\":199.0,\"tripAdvisorRating\":4.7}]}";
        when(restTemplate.getForObject("https://example.test/hotels.json", String.class)).thenReturn(response);

        List<Hotel> hotels = hotelService.searchHotels("Los Angeles");

        assertEquals(1, hotels.size());
        Hotel hotel = hotels.get(0);
        assertEquals(5001L, hotel.getId());
        assertEquals("Sunset Suites", hotel.getName());
        assertEquals("Los Angeles", hotel.getLocation());
        assertEquals(199.0, hotel.getPricePerNight());
        assertEquals(4.7, hotel.getRating());
    }

    @Test
    void searchHotelsFallsBackWhenApiHasNoMatchingEntries() {
        String response = "{\"hotels\":[{\"id\":\"sea-1\",\"hotelId\":6001,\"name\":\"Waterfront Hotel\",\"city\":\"Seattle\",\"lowRate\":180.0,\"hotelRating\":4.1}]}";
        when(restTemplate.getForObject("https://example.test/hotels.json", String.class)).thenReturn(response);

        List<Hotel> hotels = hotelService.searchHotels("NYC");

        assertEquals(2, hotels.size());
        assertTrue(hotels.stream().allMatch(hotel -> "New York".equals(hotel.getLocation())));
    }

    @Test
    void searchHotelsThrowsMeaningfulErrorForInvalidJson() {
        when(restTemplate.getForObject("https://example.test/hotels.json", String.class)).thenReturn("{invalid-json");

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> hotelService.searchHotels("Seattle"));

        assertTrue(exception.getMessage().contains("Failed to parse hotel API response"));
    }
}
