package com.travelbooking.api.discovery;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DiscoveryServiceApplicationTests {

    @Test
    void discoveryServiceApplicationHasRequiredAnnotations() {
        assertTrue(DiscoveryServiceApplication.class.isAnnotationPresent(SpringBootApplication.class));
        assertTrue(DiscoveryServiceApplication.class.isAnnotationPresent(EnableEurekaServer.class));
    }

    @Test
    void discoveryServiceApplicationClassLoads() {
        assertNotNull(new DiscoveryServiceApplication());
    }
}
