package com.travelbooking.api.flight.service;

import com.travelbooking.api.flight.model.Flight;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class FlightService {
    private static final String CSV_SPLIT_REGEX = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";

    private final RestTemplate restTemplate;
    private final String routesUrl;
    private final String airlinesUrl;
    private final int maxResults;

    public FlightService(
            RestTemplate restTemplate,
            @Value("${external.api.routes-url}") String routesUrl,
            @Value("${external.api.airlines-url}") String airlinesUrl,
            @Value("${external.api.max-results:10}") int maxResults
    ) {
        this.restTemplate = restTemplate;
        this.routesUrl = routesUrl;
        this.airlinesUrl = airlinesUrl;
        this.maxResults = maxResults;
    }

    public List<Flight> searchFlights(String origin, String destination) {
        String normalizedOrigin = normalizeAirportCode(origin);
        String normalizedDestination = normalizeAirportCode(destination);

        String routesCsv = restTemplate.getForObject(routesUrl, String.class);
        String airlinesCsv = restTemplate.getForObject(airlinesUrl, String.class);

        Map<String, String> airlinesByCode = buildAirlineLookup(airlinesCsv);
        List<Flight> flights = new ArrayList<>();

        if (routesCsv == null || routesCsv.isBlank()) {
            return flights;
        }

        for (String line : routesCsv.split("\\R")) {
            String[] columns = parseCsvLine(line);
            if (columns.length < 5) {
                continue;
            }

            String airlineCode = clean(columns[0]);
            String routeOrigin = clean(columns[2]).toUpperCase(Locale.ROOT);
            String routeDestination = clean(columns[4]).toUpperCase(Locale.ROOT);

            if (!normalizedOrigin.equals(routeOrigin) || !normalizedDestination.equals(routeDestination)) {
                continue;
            }

            String airlineName = airlinesByCode.getOrDefault(airlineCode, airlineCode);
            double price = calculatePrice(airlineCode, normalizedOrigin, normalizedDestination, flights.size());

            Flight flight = new Flight();
            flight.setId(airlineCode + "-" + normalizedOrigin + "-" + normalizedDestination + "-" + flights.size());
            flight.setAirline(airlineName);
            flight.setOrigin(normalizedOrigin);
            flight.setDestination(normalizedDestination);
            flight.setPrice(price);
            flights.add(flight);

            if (flights.size() >= maxResults) {
                break;
            }
        }

        return flights;
    }

    private Map<String, String> buildAirlineLookup(String airlinesCsv) {
        Map<String, String> airlinesByCode = new HashMap<>();
        if (airlinesCsv == null || airlinesCsv.isBlank()) {
            return airlinesByCode;
        }

        for (String line : airlinesCsv.split("\\R")) {
            String[] columns = parseCsvLine(line);
            if (columns.length < 4) {
                continue;
            }

            String name = clean(columns[1]);
            String iataCode = clean(columns[3]);
            if (!iataCode.isBlank() && !name.isBlank()) {
                airlinesByCode.put(iataCode, name);
            }
        }

        return airlinesByCode;
    }

    private double calculatePrice(String airlineCode, String origin, String destination, int offset) {
        int hash = Math.abs((airlineCode + origin + destination).hashCode());
        return 120.0 + (hash % 180) + (offset * 17.5);
    }

    private String[] parseCsvLine(String line) {
        return line.split(CSV_SPLIT_REGEX, -1);
    }

    private String clean(String value) {
        return value.replace("\"", "").replace("\\N", "").trim();
    }

    private String normalizeAirportCode(String value) {
        String normalized = value == null ? "" : value.trim().toUpperCase(Locale.ROOT);
        if ("NYC".equals(normalized)) {
            return "JFK";
        }
        if ("LA".equals(normalized)) {
            return "LAX";
        }
        return normalized;
    }
}
