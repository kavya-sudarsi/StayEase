# 🏨 StayEase – Microservices-Based Accommodation Platform

## 📌 Overview

**StayEase** is a full-stack **microservices-based accommodation platform** designed for managing PGs, hostels, rooms, bed inventory, bookings, and online payments.

The application supports two types of users:

* **OWNER** – Manage properties, rooms, and bed availability
* **USER** – Browse properties, select rooms, make bookings, and complete payments

The project demonstrates a real-world backend architecture using **Spring Boot, Spring Cloud, JWT authentication, Eureka Service Discovery, API Gateway, OpenFeign, MySQL, Razorpay, Cloudinary**, and a **React frontend**.

The application is deployed and accessible online using **Vercel, Render, and Railway**.

---

## 🌐 Live Application

### Frontend

**StayEase:**
https://stay-ease-frontend-kappa.vercel.app/

### Backend API Gateway

https://stayease-nwql.onrender.com/

The React frontend communicates with the backend through the API Gateway.

---

# 🏗 Architecture Overview

```text
                    ┌──────────────────────┐
                    │     React Frontend   │
                    │   Vercel Deployment  │
                    └──────────┬───────────┘
                               │
                               ▼
                    ┌──────────────────────┐
                    │     API Gateway      │
                    │   Spring Cloud       │
                    │      Gateway         │
                    └──────────┬───────────┘
                               │
              ┌────────────────┼────────────────┐
              │                │                │
              ▼                ▼                ▼
       ┌────────────┐   ┌──────────────┐  ┌───────────────┐
       │   Auth     │   │   Property   │  │    Booking    │
       │  Service   │   │   Service    │  │    Service    │
       │   :8081    │   │    :8082     │  │     :8083     │
       └─────┬──────┘   └──────┬───────┘  └───────┬───────┘
             │                 │                   │
             ▼                 ▼                   ▼
       ┌────────────┐   ┌──────────────┐  ┌───────────────┐
       │   MySQL    │   │    MySQL     │  │     MySQL     │
       │ Auth DB    │   │ Property DB  │  │   Booking DB  │
       └────────────┘   └──────────────┘  └───────────────┘
                                 ▲
                                 │
                           OpenFeign
                                 │
                                 │
                         Booking Service
                         ↔ Property Service

                    ┌──────────────────────┐
                    │    Eureka Server     │
                    │   Service Discovery  │
                    └──────────────────────┘

External Services:

Cloudinary → Property Image Storage
Razorpay   → Online Payments
Railway    → MySQL Hosting
Render     → Backend Deployment
Vercel     → Frontend Deployment
```

---

# 🔧 Microservices

## 1. Auth Service

Handles authentication and user management.

### Features

* User registration
* User login
* Password encryption using BCrypt
* JWT token generation
* JWT-based authentication
* Role-based authorization

### Roles

| Role  | Description                                      |
| ----- | ------------------------------------------------ |
| OWNER | Can create and manage properties and rooms       |
| USER  | Can browse properties and create/cancel bookings |

---

# 2. Property Service

Manages accommodation properties, rooms, and bed inventory.

### Features

* Create property
* Update property
* Delete property
* View properties
* Search properties
* Filter properties
* Manage rooms
* Track available beds
* Increase/decrease bed availability
* Owner-specific property management
* Property image upload
* Cloudinary image storage

### Property Information

A property can contain:

* Name
* Address
* City
* State
* Pincode
* Property type
* Gender category
* Contact number
* Description
* Amenities
* Rooms
* Bed availability
* Property image

---

# 3. Booking Service

Handles the complete booking workflow.

### Features

* Create booking
* Cancel booking
* View user's bookings
* View property bookings
* Check room availability
* Prevent booking when beds are unavailable
* Communicate with Property Service using OpenFeign
* Update bed inventory after booking
* Restore inventory after cancellation

### Booking Flow

```text
USER
 │
 ▼
Create Booking
 │
 ▼
Booking Service
 │
 ├── Check existing booking
 │
 ├── Get room details
 │
 ├── Check available beds
 │
 ▼
Property Service
 │
 ├── Decrease available beds
 │
 ▼
Booking Created
```

---

# 4. API Gateway

The API Gateway acts as the **single entry point** for the frontend.

### Responsibilities

* Route requests to microservices
* JWT token validation
* Forward authenticated user information
* Role information propagation
* Centralized API entry point
* Service routing

Example:

```text
/auth/**       → Auth Service

/properties/** → Property Service

/bookings/**   → Booking Service

/payments/**   → Booking Service
```

The frontend does not directly communicate with individual backend services.

---

# 5. Eureka Server

Eureka is used for **service discovery**.

### Responsibilities

* Register microservices
* Maintain service registry
* Allow services to discover each other
* Support dynamic service locations
* Remove dependency on hardcoded internal service addresses

Services registered with Eureka include:

```text
AUTH-SERVICE
PROPERTY-SERVICE
BOOKING-SERVICE
API-GATEWAY
```

---

# 🔐 Security

StayEase uses **JWT-based authentication**.

### Authentication Flow

```text
User
 │
 ▼
Login
 │
 ▼
Auth Service
 │
 ▼
JWT Token
 │
 ▼
React Frontend
 │
 ▼
Authorization Header
 │
 ▼
API Gateway
 │
 ▼
JWT Validation
 │
 ▼
Microservice
```

The JWT contains information such as:

* User email
* User role
* Token expiration

The API Gateway validates the token before forwarding protected requests.

---

# 👥 Role-Based Authorization

| Feature                  | OWNER | USER |
| ------------------------ | :---: | :--: |
| Register                 |   ✅   |   ✅  |
| Login                    |   ✅   |   ✅  |
| Browse Properties        |   ✅   |   ✅  |
| Search Properties        |   ✅   |   ✅  |
| View Property Details    |   ✅   |   ✅  |
| Create Property          |   ✅   |   ❌  |
| Update Property          |   ✅   |   ❌  |
| Delete Property          |   ✅   |   ❌  |
| Manage Rooms             |   ✅   |   ❌  |
| Create Booking           |   ❌   |   ✅  |
| Cancel Booking           |   ❌   |   ✅  |
| View Own Bookings        |   ❌   |   ✅  |
| Manage Property Bookings |   ✅   |   ❌  |

---

# 💳 Payment Integration

StayEase integrates **Razorpay** for online booking payments.

### Payment Flow

```text
User
 │
 ▼
Create Booking
 │
 ▼
Create Razorpay Order
 │
 ▼
Razorpay Checkout
 │
 ▼
Payment
 │
 ▼
Razorpay Response
 │
 ▼
Payment Verification
 │
 ▼
Booking Payment Status Updated
```

The application verifies the Razorpay payment before treating the booking payment as successful.

---

# 🖼 Image Management

Property images are uploaded to **Cloudinary** instead of being stored on the application server.

### Flow

```text
React Frontend
      │
      ▼
Property Service
      │
      ▼
Cloudinary
      │
      ▼
Image URL
      │
      ▼
MySQL
```

The database stores the image URL while the actual image is managed by Cloudinary.

This avoids relying on the local filesystem of the deployed Render instance.

---

# 🔄 Inter-Service Communication

Booking Service communicates with Property Service using **OpenFeign**.

Example:

```text
Booking Service
       │
       │ OpenFeign
       ▼
Property Service
       │
       ├── Get Room
       ├── Get Property
       ├── Decrease Available Beds
       └── Increase Available Beds
```

This keeps service communication clean while allowing each service to remain independently deployable.

---

# 🗄 Database Strategy

Each microservice maintains its own database.

| Service          | Database          |
| ---------------- | ----------------- |
| Auth Service     | Auth Database     |
| Property Service | Property Database |
| Booking Service  | Booking Database  |

The databases are hosted using **Railway MySQL**.

This follows the microservices principle of **database-per-service**, keeping services loosely coupled.

---

# 🛠 Technology Stack

## Backend

| Technology           | Purpose                     |
| -------------------- | --------------------------- |
| Java 17              | Programming Language        |
| Spring Boot          | Microservices Development   |
| Spring Cloud         | Cloud-native Microservices  |
| Spring Cloud Gateway | API Gateway                 |
| Eureka               | Service Discovery           |
| Spring Security      | Security                    |
| JWT                  | Authentication              |
| OpenFeign            | Inter-Service Communication |
| Spring Data JPA      | Database Access             |
| Hibernate            | ORM                         |
| MySQL                | Relational Database         |
| Maven                | Build Tool                  |
| Resilience4j         | Fault Tolerance             |
| Bean Validation      | Request Validation          |

## Frontend

| Technology   | Purpose                |
| ------------ | ---------------------- |
| React        | UI Development         |
| Vite         | Frontend Build Tool    |
| React Router | Client-side Routing    |
| Axios        | REST API Communication |
| CSS          | UI Styling             |

## External Services

| Service    | Purpose             |
| ---------- | ------------------- |
| Razorpay   | Online Payments     |
| Cloudinary | Image Storage       |
| Railway    | MySQL Hosting       |
| Render     | Backend Deployment  |
| Vercel     | Frontend Deployment |

---

# 📦 Major Application Features

### Authentication

* Registration
* Login
* JWT authentication
* BCrypt password encryption
* Role-based access

### Property Management

* Create property
* Edit property
* Delete property
* Property details
* Property search
* Property filtering
* Room management
* Bed inventory

### Booking

* Room selection
* Check-in/check-out dates
* Availability checking
* Booking creation
* Booking cancellation
* Booking history
* Owner booking management

### Payments

* Razorpay order creation
* Razorpay checkout
* Payment verification
* Booking payment status

### Images

* Property image upload
* Cloudinary integration
* Persistent image URLs

---

# 🚀 Deployment

The application is deployed using free cloud services.

### Frontend

**Vercel**

```text
https://stay-ease-frontend-kappa.vercel.app/
```

### Backend

Backend microservices are deployed on **Render**.

```text
API Gateway
Auth Service
Property Service
Booking Service
Eureka Server
```

### Database

MySQL databases are hosted using **Railway**.

### Image Storage

Property images are stored using **Cloudinary**.

---

# ▶ Running the Project Locally

## 1. Clone the repository

```bash
git clone https://github.com/your-repo/stayease.git
```

---

## 2. Create MySQL Databases

Create separate databases for each service:

```text
stayease_auth
stayease_property
stayease_booking
```

---

## 3. Configure Environment Variables

Configure the required environment variables for:

```text
MYSQL_URL
MYSQLUSER
MYSQLPASSWORD

EUREKA_SERVER_URL

RAZORPAY_KEY
RAZORPAY_SECRET

CLOUDINARY_CLOUD_NAME
CLOUDINARY_API_KEY
CLOUDINARY_API_SECRET
```

---

## 4. Start Services

Start the services in the following order:

```text
1. Eureka Server
2. Auth Service
3. Property Service
4. Booking Service
5. API Gateway
6. React Frontend
```

---

## 5. Start Frontend

Navigate to the frontend project:

```bash
npm install
npm run dev
```

The frontend will normally be available at:

```text
http://localhost:5173
```

---

# 📸 Screenshots

### Login / Registration

*Add screenshot here*

### User Dashboard

*Add screenshot here*

### Property Details

*Add screenshot here*

### Owner Dashboard

*Add screenshot here*

### Booking

*Add screenshot here*

### Payment

*Add screenshot here*

---

# 💡 Key Technical Highlights

The project demonstrates practical experience with:

* Microservices architecture
* API Gateway pattern
* Service discovery
* JWT authentication
* Role-based authorization
* REST API development
* OpenFeign communication
* Database-per-service architecture
* Distributed inventory management
* Payment gateway integration
* Cloud-based image storage
* Exception handling
* Request validation
* Fault tolerance
* Independent service deployment
* Cloud deployment and environment configuration

---

# 🎯 Project Objective

StayEase was built as a hands-on full-stack project to understand how a production-style application can be designed using **Java, Spring Boot, Spring Cloud, React, MySQL, and cloud services**.

The project focuses on solving practical problems such as:

* Authentication and authorization
* Property and room management
* Bed inventory synchronization
* Booking conflicts
* Payment processing
* Service-to-service communication
* Cloud deployment
* Persistent image storage

---

# 👩‍💻 Author

### Kavya Sudarsi

**Java Full Stack Developer**

Built as a hands-on project to demonstrate:

* Java & Spring Boot development
* Microservices architecture
* REST API development
* React frontend development
* Secure authentication
* Distributed service communication
* Payment integration
* Cloud deployment
