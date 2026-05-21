package com.travelbooking.api.car;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class CarRentalServiceApplicationTests {

    @Test
    void restTemplateBeanIsCreated() {
        RestTemplate restTemplate = new CarRentalServiceApplication().restTemplate();
        assertNotNull(restTemplate);
    }
}
