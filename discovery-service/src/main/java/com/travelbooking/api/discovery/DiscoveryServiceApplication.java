package com.travelbooking.api.discovery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class DiscoveryServiceApplication {
    public static void main(String[] args) {
        System.out.println("Current Java Version: " + System.getProperty("java.version"));
        SpringApplication.run(DiscoveryServiceApplication.class, args);
    }
}