# 🏨 StayEase – Microservices-Based Accommodation Platform

## 📌 Overview

**StayEase** is a **Spring Boot microservices-based accommodation management platform** for managing **PG/hostel properties, room inventory, and booking workflows**.

The system demonstrates a **real-world microservices architecture** with:

- API Gateway routing  
- Service discovery using Eureka  
- JWT-based authentication  
- Role-based authorization  
- Inter-service communication using OpenFeign  
- Distributed inventory management  


## 🏗 Architecture Overview

Client  

API Gateway  

Eureka Server  

Microservices
- Auth Service
- Property Service
- Booking Service

Database Layer
- Separate MySQL Database per Service

Each microservice is **independently deployable** and communicates using **service discovery and Feign clients**.


## 🔧 Microservices

### Auth Service

Handles **authentication and user management**.

**Features**

- User registration  
- Login authentication  
- JWT token generation  
- Role-based access  

**Roles Supported**

- OWNER  
- USER  


### Property Service

Manages **properties, rooms, and bed inventory**.

**Features**

- Property creation (OWNER only)  
- Room management  
- Bed availability tracking  
- Increase / decrease available beds  


### Booking Service

Handles **booking operations**.

**Features**

- Create booking  
- Cancel booking  
- Feign communication with Property Service  
- Bed inventory synchronization  
- Conflict handling when beds are unavailable  


### API Gateway

Acts as the **single entry point** for all requests.

**Responsibilities**

- Request routing  
- JWT validation  
- Forwarding requests to microservices  


### Eureka Server

Provides **service discovery**.

**Responsibilities**

- Register services dynamically  
- Enable services to locate each other  
- Remove hardcoded URLs  



## 🔐 Security

The system uses **JWT-based authentication**.

### Authentication Flow

1. User logs in with credentials  
2. Request is processed by the Auth Service  
3. Auth Service generates a JWT token  
4. Client includes the JWT token in subsequent requests  
5. API Gateway validates the token  
6. Request is forwarded to the respective microservice  


## 👥 Role-Based Access

| Role | Permissions |
|-----|-------------|
| OWNER | Create properties and rooms |
| USER | Create and cancel bookings |


## 📦 Booking Workflow

1. User logs in and receives a JWT token  
2. Client sends request with token  
3. API Gateway validates the token  
4. Booking Service processes the request  
5. Booking Service calls Property Service using Feign  
6. Property Service updates bed availability  
7. Booking is created if beds are available  


## 🛠 Tech Stack

| Technology | Purpose |
|------------|--------|
| Java 17 | Programming language |
| Spring Boot | Microservices framework |
| Spring Cloud | Gateway & service discovery |
| Eureka Server | Service registry |
| Spring Security | Authentication & authorization |
| JWT | Secure authentication |
| OpenFeign | Inter-service communication |
| JPA / Hibernate | ORM |
| MySQL | Database |
| Maven | Dependency management |
| Docker | Containerization |


## 🗄 Database Strategy

Each microservice maintains its **own database**.

| Service | Database |
|--------|---------|
| Auth Service | stayease_auth |
| Property Service | stayease_property |
| Booking Service | stayease_booking |

This ensures **loose coupling and independent scaling**.


##  Running the Project

### 1. Clone the repository

```bash
git clone https://github.com/your-repo/stayease.git
2. Create MySQL Databases
stayease_auth
stayease_property
stayease_booking
3. Start Services in Order

Eureka Server

Auth Service

Property Service

Booking Service

API Gateway

4. Access the Application
http://localhost:8080
👩‍💻 Author

Kavya Sudarsi

Built as a hands-on project to demonstrate:

Microservices architecture

Secure service communication

Distributed system design
