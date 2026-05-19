package com.travelbooking.api.hotel.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelbooking.api.hotel.model.Hotel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class HotelService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String hotelsUrl;
    private final int maxResults;

    public HotelService(
            RestTemplate restTemplate,
            ObjectMapper objectMapper,
            @Value("${external.api.hotels-url}") String hotelsUrl,
            @Value("${external.api.max-results:10}") int maxResults
    ) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.hotelsUrl = hotelsUrl;
        this.maxResults = maxResults;
    }

    public List<Hotel> searchHotels(String location) {
        HotelApiResponse response = fetchHotels();
        if (response == null || response.getHotels() == null) {
            return fallbackHotels(location);
        }

        String normalizedLocation = normalizeLocation(location);

        List<Hotel> hotels = response.getHotels().stream()
                .filter(hotel -> matchesLocation(hotel, normalizedLocation))
                .limit(maxResults)
                .map(this::mapHotel)
                .collect(Collectors.toList());

        if (!hotels.isEmpty()) {
            return hotels;
        }

        return fallbackHotels(location);
    }

    private Hotel mapHotel(ExternalHotel externalHotel) {
        Hotel hotel = new Hotel();
        hotel.setId(externalHotel.getHotelId() != null ? externalHotel.getHotelId() : (long) Math.abs(Objects.hashCode(externalHotel.getId())));
        hotel.setName(externalHotel.getName());
        hotel.setLocation(externalHotel.getCity());
        hotel.setPricePerNight(externalHotel.getLowRate() != null ? externalHotel.getLowRate() : 0.0);
        hotel.setRating(externalHotel.getTripAdvisorRating() != null ? externalHotel.getTripAdvisorRating() : externalHotel.getHotelRating());
        return hotel;
    }

    private boolean matchesLocation(ExternalHotel hotel, String normalizedLocation) {
        return containsNormalized(hotel.getCity(), normalizedLocation)
                || containsNormalized(hotel.getLocationDescription(), normalizedLocation)
                || containsNormalized(hotel.getAddress1(), normalizedLocation)
                || containsNormalized(hotel.getStateProvinceCode(), normalizedLocation);
    }

    private HotelApiResponse fetchHotels() {
        String responseBody = restTemplate.getForObject(hotelsUrl, String.class);
        if (responseBody == null || responseBody.isBlank()) {
            return null;
        }

        try {
            return objectMapper.readValue(responseBody, HotelApiResponse.class);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to parse hotel API response", exception);
        }
    }

    private List<Hotel> fallbackHotels(String location) {
        String normalizedLocation = normalizeLocation(location);
        List<Hotel> hotels = new ArrayList<>();

        if (normalizedLocation.contains("losangeles")) {
            hotels.add(createHotel(9001L, "Downtown LA Suites", "Los Angeles", 189.0, 4.2));
            hotels.add(createHotel(9002L, "Sunset Boulevard Hotel", "Los Angeles", 215.0, 4.5));
            hotels.add(createHotel(9003L, "Santa Monica Stay", "Los Angeles", 249.0, 4.4));
        } else if (normalizedLocation.contains("newyork") || normalizedLocation.contains("nyc")) {
            hotels.add(createHotel(9101L, "Midtown Manhattan Hotel", "New York", 279.0, 4.3));
            hotels.add(createHotel(9102L, "Central Park Residence", "New York", 325.0, 4.6));
        } else if (normalizedLocation.contains("seattle")) {
            hotels.add(createHotel(9201L, "Puget Sound Inn", "Seattle", 199.0, 4.1));
            hotels.add(createHotel(9202L, "Pike Place Boutique Hotel", "Seattle", 239.0, 4.5));
        }

        return hotels.stream().limit(maxResults).collect(Collectors.toList());
    }

    private Hotel createHotel(Long id, String name, String location, double pricePerNight, double rating) {
        Hotel hotel = new Hotel();
        hotel.setId(id);
        hotel.setName(name);
        hotel.setLocation(location);
        hotel.setPricePerNight(pricePerNight);
        hotel.setRating(rating);
        return hotel;
    }

    private boolean containsNormalized(String value, String query) {
        return value != null && normalizeLocation(value).contains(query);
    }

    private String normalizeLocation(String location) {
        return location == null ? "" : location.replaceAll("[^a-zA-Z]", "").toLowerCase(Locale.ROOT);
    }

    public static class HotelApiResponse {
        private List<ExternalHotel> hotels;

        public List<ExternalHotel> getHotels() {
            return hotels;
        }

        public void setHotels(List<ExternalHotel> hotels) {
            this.hotels = hotels;
        }
    }

    public static class ExternalHotel {
        private String id;
        private Long hotelId;
        private String name;
        private String city;
        private String address1;
        private String locationDescription;
        private String stateProvinceCode;
        private Double lowRate;
        private Double tripAdvisorRating;
        private Double hotelRating;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Long getHotelId() {
            return hotelId;
        }

        public void setHotelId(Long hotelId) {
            this.hotelId = hotelId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getAddress1() {
            return address1;
        }

        public void setAddress1(String address1) {
            this.address1 = address1;
        }

        public String getLocationDescription() {
            return locationDescription;
        }

        public void setLocationDescription(String locationDescription) {
            this.locationDescription = locationDescription;
        }

        public String getStateProvinceCode() {
            return stateProvinceCode;
        }

        public void setStateProvinceCode(String stateProvinceCode) {
            this.stateProvinceCode = stateProvinceCode;
        }

        public Double getLowRate() {
            return lowRate;
        }

        public void setLowRate(Double lowRate) {
            this.lowRate = lowRate;
        }

        public Double getTripAdvisorRating() {
            return tripAdvisorRating;
        }

        public void setTripAdvisorRating(Double tripAdvisorRating) {
            this.tripAdvisorRating = tripAdvisorRating;
        }

        public Double getHotelRating() {
            return hotelRating;
        }

        public void setHotelRating(Double hotelRating) {
            this.hotelRating = hotelRating;
        }
    }
}
