# 🏨 StayEase – Microservices-Based Accommodation Platform

## 📌 Project Overview

**StayEase** is a **Spring Boot Microservices-based accommodation management platform** designed to manage **PG/hostel properties, room inventory, and booking workflows**.

The system demonstrates a **real-world microservices architecture** with:

- Centralized **API Gateway routing**
- **Service discovery** using Eureka
- **JWT-based authentication**
- **Role-based authorization**
- **Inter-service communication using OpenFeign**
- **Distributed inventory management**

This project focuses on **scalable backend architecture** and **secure service-to-service communication**.

---

## 🏗 Architecture Overview


Client
│
▼
API Gateway (JWT Validation)
│
▼
Eureka Server (Service Discovery)
│
▼
────────────────────────────────────
| Auth Service | Property Service |
| Booking Service | |
────────────────────────────────────
│
▼
Separate MySQL Database per Service


Each microservice is **independently deployable** and communicates through **service discovery and Feign clients**.

---

## 🔧 Microservices Breakdown

### 1️⃣ Eureka Server

- Service Discovery mechanism
- Dynamic service registration
- Removes need for hardcoded service URLs
- Enables load-balanced communication between services

---

### 2️⃣ API Gateway

Acts as the **single entry point for all client requests**.

Features:

- Request routing to microservices
- **JWT token validation**
- **Role-based request filtering**
- Header propagation to downstream services

---

### 3️⃣ Auth Service

Handles **authentication and user management**.

Features:

- User registration
- User login
- JWT token generation
- Role support

Supported roles:


OWNER
USER


---

### 4️⃣ Property Service

Manages **properties, rooms, and bed inventory**.

Features:

- Owner-only property creation
- Room management
- Bed inventory tracking
- Increase / decrease bed availability
- DTO-based API design
- Global exception handling

---

### 5️⃣ Booking Service

Handles **room bookings and cancellations**.

Features:

- Booking creation
- Booking cancellation
- **Feign Client** for communication with Property Service
- Distributed inventory synchronization
- Conflict handling (`409` when beds are unavailable)
- Ownership validation

---

## 🔐 Security Features

The system implements **JWT-based security**.

### Authentication Flow


User Login
│
▼
Auth Service
│
▼
JWT Token Generated
│
▼
Client Sends Token
│
▼
API Gateway Validates Token
│
▼
Request Forwarded to Microservices


### Role-Based Access

| Role | Permissions |
|-----|-------------|
| OWNER | Create properties and rooms |
| USER | Create and cancel bookings |

---

## 📦 Booking Workflow


1️⃣ User logs in → receives JWT token

2️⃣ Client sends request with token

3️⃣ API Gateway validates JWT

4️⃣ Booking Service processes booking request

5️⃣ Booking Service calls Property Service using Feign

6️⃣ Property Service decreases bed inventory

7️⃣ If beds available → Booking created

8️⃣ If no beds available → 409 Conflict returned

9️⃣ On booking cancellation → beds are restored


---

## 🛠 Tech Stack

| Technology | Purpose |
|------------|--------|
| Java 17 | Core programming language |
| Spring Boot | Microservices framework |
| Spring Cloud | Gateway & Service Discovery |
| Eureka Server | Service discovery |
| Spring Security | Authentication & authorization |
| JWT | Secure authentication |
| OpenFeign | Inter-service communication |
| JPA / Hibernate | ORM |
| MySQL | Database |
| Maven | Dependency management |
| Docker | Containerization |

---

## 🗄 Database Strategy

Each microservice maintains its **own dedicated database**.

| Service | Database |
|-------|---------|
| Auth Service | `stayease_auth` |
| Property Service | `stayease_property` |
| Booking Service | `stayease_booking` |

This ensures:

- **Loose coupling**
- **Independent scaling**
- **Service autonomy**

---

## 🚀 How To Run The Project

### 1️⃣ Clone the repository

```bash
git clone https://github.com/your-repository/stayease.git
cd stayease
2️⃣ Start MySQL

Create databases:

stayease_auth
stayease_property
stayease_booking
3️⃣ Start Services

Run services in the following order:

Eureka Server

Auth Service

Property Service

Booking Service

API Gateway

4️⃣ Access Application
http://localhost:8080

All requests go through API Gateway.

📡 Sample APIs
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

All services implement centralized global exception handling.

Example response:

{
  "timestamp": "2026-03-01T12:00:00",
  "status": 409,
  "error": "Conflict",
  "message": "No beds available",
  "path": "/bookings"
}
⭐ Key Highlights

Distributed workflow management

Inventory consistency across services

Proper HTTP status handling

DTO-based API design

Clean layered architecture

Structured global error handling

Secure JWT-based authentication

Inter-service communication using Feign

📈 Future Enhancements

Docker containerization

Spring Cloud Config Server

Circuit Breaker (Resilience4j)

Swagger API documentation

Centralized logging

👩‍💻 Author

Kavya Sudarsi

Developed as a hands-on microservices architecture project to demonstrate:

Distributed system design

Secure service communication

Scalable backend architecture
