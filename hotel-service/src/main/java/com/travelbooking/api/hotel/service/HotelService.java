package com.travelbooking.api.hotel.service;

import com.travelbooking.api.hotel.model.Hotel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class HotelService {
    private final RestTemplate restTemplate;
    private final String hotelsUrl;
    private final int maxResults;

    public HotelService(
            RestTemplate restTemplate,
            @Value("${external.api.hotels-url}") String hotelsUrl,
            @Value("${external.api.max-results:10}") int maxResults
    ) {
        this.restTemplate = restTemplate;
        this.hotelsUrl = hotelsUrl;
        this.maxResults = maxResults;
    }

    public List<Hotel> searchHotels(String location) {
        HotelApiResponse response = restTemplate.getForObject(hotelsUrl, HotelApiResponse.class);
        if (response == null || response.getHotels() == null) {
            return Collections.emptyList();
        }

        String normalizedLocation = location.trim().toLowerCase(Locale.ROOT);

        return response.getHotels().stream()
                .filter(hotel -> matchesLocation(hotel, normalizedLocation))
                .limit(maxResults)
                .map(this::mapHotel)
                .collect(Collectors.toList());
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
        return containsIgnoreCase(hotel.getCity(), normalizedLocation)
                || containsIgnoreCase(hotel.getLocationDescription(), normalizedLocation)
                || containsIgnoreCase(hotel.getAddress1(), normalizedLocation)
                || containsIgnoreCase(hotel.getStateProvinceCode(), normalizedLocation);
    }

    private boolean containsIgnoreCase(String value, String query) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(query);
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
