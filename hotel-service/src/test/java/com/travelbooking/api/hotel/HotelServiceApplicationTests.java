package com.travelbooking.api.hotel;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class HotelServiceApplicationTests {

    @Test
    void restTemplateBeanIsCreated() {
        RestTemplate restTemplate = new HotelServiceApplication().restTemplate();
        assertNotNull(restTemplate);
    }
}
