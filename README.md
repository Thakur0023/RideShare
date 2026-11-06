# RideShare Backend

A ride-hailing backend built with Java and Spring Boot: JWT authentication, nearest-driver matching, ride lifecycle, fare pricing with surge, and live driver location over WebSocket.

## Tech stack
Java 17, Spring Boot 4, Spring Security (JWT), Spring Data JPA, MySQL, WebSocket, JUnit 5, Mockito

## Features
- Register and log in as a rider or driver (BCrypt-hashed passwords, JWT tokens)
- Nearest-driver matching: bounding-box SQL filter, then haversine ranking
- Double-booking prevention with an atomic conditional update in the database
- Ride lifecycle state machine with a status history audit trail
- Fare estimate with a surge multiplier based on nearby driver availability
- Role-based access: only the ride's rider or driver can change it
- Live driver location to the rider over WebSocket

## Run locally
1. Install MySQL and start it on port 3306.
2. Set environment variables: `DB_PASSWORD`, `JWT_SECRET` (32+ characters), and optionally `DB_USERNAME`.
3. Run `RideshareBackendApplication`, or `./mvnw spring-boot:run`.

## API
| Method | Endpoint | Who |
|---|---|---|
| POST | /api/auth/register | anyone |
| POST | /api/auth/login | anyone |
| POST | /api/rides | rider |
| PATCH | /api/rides/{id}/status?target=... | the ride's rider or driver |
| PATCH | /api/drivers/me/location | driver |
| WS | /ws/rides/{id}/location | the ride's rider or driver |

## Tests
`./mvnw test` runs unit tests (Mockito) and a Spring context test on an in-memory H2 database.

## Roadmap
Payments, Docker, spatial index for matching, Redis for multi-server WebSocket.
