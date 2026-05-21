package com.travelbooking.api.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApiGatewayApplicationTests {

    @Test
    void apiGatewayApplicationHasRequiredAnnotations() {
        assertTrue(ApiGatewayApplication.class.isAnnotationPresent(SpringBootApplication.class));
        assertTrue(ApiGatewayApplication.class.isAnnotationPresent(EnableZuulProxy.class));
        assertTrue(ApiGatewayApplication.class.isAnnotationPresent(EnableEurekaClient.class));
    }

    @Test
    void apiGatewayApplicationClassLoads() {
        assertNotNull(new ApiGatewayApplication());
    }
}
