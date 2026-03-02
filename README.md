🏨 StayEase – Microservices-Based Accommodation Platform
 Project Overview

StayEase is a Spring Boot microservices application designed to manage PG/hostel accommodations.
It implements centralized authentication, API Gateway routing, service discovery, and secure booking workflows with distributed inventory management.

This project demonstrates real-world microservices architecture with inter-service communication and role-based security.

Architecture Overview
Client
   ↓
API Gateway (JWT Validation)
   ↓
Eureka Server (Service Discovery)
   ↓
-----------------------------------------
| Auth Service      | Property Service |
| Booking Service   |                  |
-----------------------------------------
   ↓
Separate MySQL Database per Service
 Microservices Breakdown
1. Eureka Server

Service discovery

Dynamic registration of all services

No hardcoded URLs

2. API Gateway

Single entry point

JWT validation

Role-based request filtering

Header propagation to downstream services

3. Auth Service

User registration

Login authentication

JWT generation

Role support (OWNER, USER)

4. Property Service

Owner-only property creation

Room management

Bed inventory tracking

Increase/decrease bed logic

DTO-based API design

Global exception handling

5. Booking Service

Booking creation

Booking cancellation

Feign client for inter-service communication

Distributed inventory synchronization

Conflict handling (409 when no beds available)

Ownership validation

Security Features

JWT-based authentication

Role-based access control

OWNER can create properties & rooms

USER can create & cancel bookings

Header forwarding via API Gateway

Booking Workflow

User logs in and receives JWT token

Request goes through API Gateway

Gateway validates JWT

Booking Service calls Property Service to decrease beds

If beds available → booking saved

If no beds → 409 Conflict returned

On cancellation → beds are increased again

🛠 Tech Stack

Java 17

Spring Boot

Spring Cloud (Eureka, Gateway)

Spring Security

OpenFeign

JPA / Hibernate

MySQL

Maven

📂 Database Strategy

Each microservice maintains its own database:

auth_db

property_db

booking_db

This ensures loose coupling and independent scaling.

🚀 How To Run

Start Eureka Server

Start Auth Service

Start Property Service

Start Booking Service

Start API Gateway

Access services via:
http://localhost:8080

Sample APIs
Login
POST /auth/login
Create Property (OWNER)
POST /properties
Add Room
POST /properties/{id}/rooms
Create Booking (USER)
POST /bookings
Cancel Booking
PUT /bookings/{id}/cancel
⚠ Error Handling

All services implement centralized global exception handling with standardized JSON response format:

{
  "timestamp": "...",
  "status": 409,
  "error": "Conflict",
  "message": "No beds available",
  "path": "/bookings"
}
 Key Highlights

Distributed workflow management

Inventory consistency across services

Proper HTTP status usage

DTO-based API design

Clean layered architecture

Structured global error handling

📌 Future Enhancements

Docker support

Config Server

Circuit breaker (Resilience4j)

Swagger API documentation

Centralized logging

Author

Developed as a hands-on microservices architecture project to demonstrate distributed system design and secure service communication.