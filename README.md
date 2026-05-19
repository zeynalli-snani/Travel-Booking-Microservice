# Welcome to Lets Put This Together

***

## Task
The challenge of this project was to build a distributed Travel Booking System using a microservice architecture. The main technical hurdles included:

Service Discovery: Ensuring multiple independent services (Flights, Hotels, Cars) can find and communicate with each other dynamically.

Centralized Routing: Implementing a single entry point (API Gateway) to manage external requests and route them to the correct internal service.

External Integration: Transitioning from static local data to real-world integration by consuming external APIs using RestTemplate.

Legacy Compatibility: Overcoming versioning conflicts between modern Spring Boot and the required Netflix Zuul/Eureka stack.

## Description
The problem was solved by implementing a five-component system using Java 11 and Spring Cloud:

Discovery Service: Built with Netflix Eureka Server to act as the registry for all microservices.

API Gateway: Built with Netflix Zuul to provide a unified URL structure and load balancing.

Microservices: Three dedicated services (Flight, Hotel, and Car Rental) that use Spring Data JPA and H2 In-Memory Databases.

Integration: Each service utilizes RestTemplate to fetch and normalize travel data from external mock/public datasets, satisfying the requirements for 3rd-party API integration.

## Installation
This project uses Maven for dependency management.

Clone the repository.

Verify Java Version: Ensure you are using Java 11 (required for Zuul compatibility).

Build the project:
From the root directory, run:
```
mvn clean install
```
## Usage
To run the system, start the services in the following order to ensure proper registration:

1. Run DiscoveryServiceApplication (Port 8761)

2. Run FlightServiceApplication (Port 8081)

3. Run HotelServiceApplication (Port 8082)

4. Run CarRentalServiceApplication (Port 8083)
 
5. Run ApiGatewayApplication (Port 8080)

### Example API Calls (via Gateway)
Once started, you can access all services through the Gateway (Port 8080):

curl "http://localhost:8080/flights/search?origin=NYC&destination=LAX"

curl "http://localhost:8080/hotels/search?location=LosAngeles"

curl "http://localhost:8080/cars/search?location=LosAngeles"

### The Core Team
[zeynalli_s]

Made at Qwasar SV -- Software Engineering School
<img alt='Qwasar SV -- Software Engineering School's Logo' src='https://storage.googleapis.com/qwasar-public/qwasar-logo_50x50.png' width='20px' />