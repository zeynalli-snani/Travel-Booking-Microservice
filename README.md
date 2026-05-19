# Travel Booking Microservices

This project is a small Spring Boot microservices system for travel booking. It uses:

- Netflix Eureka for service discovery
- Netflix Zuul as the API gateway
- Separate microservices for flights, hotels, and car rentals
- External mock/public data sources for travel search results

## Services

- `discovery-service` on `http://localhost:8761`
- `api-gateway` on `http://localhost:8080`
- `flight-service` on `http://localhost:8081`
- `hotel-service` on `http://localhost:8082`
- `car-rental-service` on `http://localhost:8083`

## Run Order

Start the services in this order:

1. `discovery-service`
2. `flight-service`
3. `hotel-service`
4. `car-rental-service`
5. `api-gateway`

After all services start, wait a few seconds for Eureka registration to finish.

## Example Gateway Endpoints

These URLs work through Zuul on port `8080`:

- Flights: [http://localhost:8080/flights/search?origin=NYC&destination=LAX](http://localhost:8080/flights/search?origin=NYC&destination=LAX)
- Hotels: [http://localhost:8080/hotels/search?location=LosAngeles](http://localhost:8080/hotels/search?location=LosAngeles)
- Cars: [http://localhost:8080/cars/search?location=LosAngeles](http://localhost:8080/cars/search?location=LosAngeles)

## Direct Service Endpoints

- Flights: [http://localhost:8081/flights/search?origin=NYC&destination=LAX](http://localhost:8081/flights/search?origin=NYC&destination=LAX)
- Hotels: [http://localhost:8082/hotels/search?location=LosAngeles](http://localhost:8082/hotels/search?location=LosAngeles)
- Cars: [http://localhost:8083/cars/search?location=LosAngeles](http://localhost:8083/cars/search?location=LosAngeles)

## Notes

- `NYC` is normalized to `JFK` inside the flight service so the flight example returns matching results.
- Hotel and car services consume external mock/public datasets and normalize plain-text JSON responses before mapping them.
- The gateway timeout is increased so flight lookups have enough time to finish through Zuul.
