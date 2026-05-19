package com.travelbooking.api.car.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelbooking.api.car.model.Car;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class CarRentalService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String carsUrl;
    private final int maxResults;

    public CarRentalService(
            RestTemplate restTemplate,
            ObjectMapper objectMapper,
            @Value("${external.api.cars-url}") String carsUrl,
            @Value("${external.api.max-results:10}") int maxResults
    ) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.carsUrl = carsUrl;
        this.maxResults = maxResults;
    }

    public List<Car> searchCars(String location) {
        List<ExternalCar> cars = fetchCars();
        if (cars.isEmpty()) {
            return Collections.emptyList();
        }

        String normalizedLocation = normalizeLocation(location);
        String originPreference = determineOriginPreference(normalizedLocation);

        return cars.stream()
                .filter(car -> originPreference == null || originPreference.equalsIgnoreCase(car.getOrigin()))
                .limit(maxResults)
                .map(this::mapCar)
                .collect(Collectors.toList());
    }

    private Car mapCar(ExternalCar externalCar) {
        Car car = new Car();
        car.setId((long) Math.abs(externalCar.getName().hashCode()));
        car.setModel(capitalizeWords(externalCar.getName()));
        car.setType(determineType(externalCar.getCylinders(), externalCar.getMilesPerGallon()));
        car.setDailyRate(calculateDailyRate(externalCar.getHorsepower(), externalCar.getMilesPerGallon()));
        car.setAvailable(externalCar.getHorsepower() != null);
        return car;
    }

    private List<ExternalCar> fetchCars() {
        String responseBody = restTemplate.getForObject(carsUrl, String.class);
        if (responseBody == null || responseBody.isBlank()) {
            return Collections.emptyList();
        }

        try {
            return objectMapper.readValue(responseBody, new TypeReference<List<ExternalCar>>() {
            });
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to parse car API response", exception);
        }
    }

    private String determineOriginPreference(String location) {
        if (location.contains("usa") || location.contains("america") || location.contains("newyork") || location.contains("losangeles")) {
            return "USA";
        }
        if (location.contains("europe") || location.contains("paris") || location.contains("berlin")) {
            return "Europe";
        }
        if (location.contains("japan") || location.contains("tokyo") || location.contains("osaka")) {
            return "Japan";
        }
        return null;
    }

    private String normalizeLocation(String location) {
        return location == null ? "" : location.replaceAll("[^a-zA-Z]", "").toLowerCase(Locale.ROOT);
    }

    private String determineType(Integer cylinders, Double milesPerGallon) {
        if (cylinders == null) {
            return "Standard";
        }
        if (cylinders >= 8) {
            return "SUV";
        }
        if (milesPerGallon != null && milesPerGallon >= 30) {
            return "Economy";
        }
        if (cylinders <= 4) {
            return "Compact";
        }
        return "Sedan";
    }

    private double calculateDailyRate(Integer horsepower, Double milesPerGallon) {
        int hp = horsepower != null ? horsepower : 90;
        double mpg = milesPerGallon != null ? milesPerGallon : 25.0;
        return Math.round((35.0 + (hp * 0.12) + Math.max(0, 35.0 - mpg)) * 100.0) / 100.0;
    }

    private String capitalizeWords(String value) {
        if (value == null || value.isBlank()) {
            return "Rental Car";
        }

        String[] words = value.split("\\s+");
        StringBuilder builder = new StringBuilder();
        for (String word : words) {
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(Character.toUpperCase(word.charAt(0)))
                    .append(word.substring(1).toLowerCase(Locale.ROOT));
        }
        return builder.toString();
    }

    public static class ExternalCar {
        @JsonProperty("Name")
        private String name;
        @JsonProperty("Miles_per_Gallon")
        private Double milesPerGallon;
        @JsonProperty("Cylinders")
        private Integer cylinders;
        @JsonProperty("Horsepower")
        private Integer horsepower;
        @JsonProperty("Origin")
        private String origin;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Double getMilesPerGallon() {
            return milesPerGallon;
        }

        public void setMilesPerGallon(Double milesPerGallon) {
            this.milesPerGallon = milesPerGallon;
        }

        public Integer getCylinders() {
            return cylinders;
        }

        public void setCylinders(Integer cylinders) {
            this.cylinders = cylinders;
        }

        public Integer getHorsepower() {
            return horsepower;
        }

        public void setHorsepower(Integer horsepower) {
            this.horsepower = horsepower;
        }

        public String getOrigin() {
            return origin;
        }

        public void setOrigin(String origin) {
            this.origin = origin;
        }
    }
}
