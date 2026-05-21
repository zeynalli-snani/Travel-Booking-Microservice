package com.travelbooking.api.flight.service;

import com.travelbooking.api.flight.model.Flight;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FlightServiceTest {

    @Mock
    private RestTemplate restTemplate;

    private FlightService flightService;

    @BeforeEach
    void setUp() {
        flightService = new FlightService(restTemplate, "https://example.test/routes.csv", "https://example.test/airlines.csv", 3);
    }

    @Test
    void searchFlightsNormalizesAirportCodesAndMapsAirlineNames() {
        String routesCsv = "AA,24,JFK,3490,LAX,3484\nDL,25,JFK,3490,SFO,3469";
        String airlinesCsv = "24,\"American Airlines\",United States,AA\n25,\"Delta Air Lines\",United States,DL";

        when(restTemplate.getForObject("https://example.test/routes.csv", String.class)).thenReturn(routesCsv);
        when(restTemplate.getForObject("https://example.test/airlines.csv", String.class)).thenReturn(airlinesCsv);

        List<Flight> flights = flightService.searchFlights("nyc", "la");

        assertEquals(1, flights.size());
        Flight flight = flights.get(0);
        assertEquals("AA-JFK-LAX-0", flight.getId());
        assertEquals("American Airlines", flight.getAirline());
        assertEquals("JFK", flight.getOrigin());
        assertEquals("LAX", flight.getDestination());
        assertTrue(flight.getPrice() >= 120.0);
    }

    @Test
    void searchFlightsReturnsEmptyListWhenRoutesAreBlank() {
        when(restTemplate.getForObject("https://example.test/routes.csv", String.class)).thenReturn(" ");
        when(restTemplate.getForObject("https://example.test/airlines.csv", String.class)).thenReturn("");

        List<Flight> flights = flightService.searchFlights("JFK", "LAX");

        assertTrue(flights.isEmpty());
    }

    @Test
    void searchFlightsStopsAtConfiguredMaximumResults() {
        String routesCsv = "AA,24,JFK,3490,LAX,3484\nUA,26,JFK,3490,LAX,3484\nB6,27,JFK,3490,LAX,3484\nDL,25,JFK,3490,LAX,3484";
        String airlinesCsv = "24,\"American Airlines\",United States,AA\n26,\"United Airlines\",United States,UA\n27,\"JetBlue\",United States,B6\n25,\"Delta Air Lines\",United States,DL";

        when(restTemplate.getForObject("https://example.test/routes.csv", String.class)).thenReturn(routesCsv);
        when(restTemplate.getForObject("https://example.test/airlines.csv", String.class)).thenReturn(airlinesCsv);

        List<Flight> flights = flightService.searchFlights("JFK", "LAX");

        assertEquals(3, flights.size());
    }
}
