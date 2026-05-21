package com.travelbooking.api.flight;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class FlightServiceApplicationTests {

    @Test
    void restTemplateBeanIsCreated() {
        RestTemplate restTemplate = new FlightServiceApplication().restTemplate();
        assertNotNull(restTemplate);
    }
}
