**Hotel Booking Application: Codebase Explanation and Usage**

This document provides an overview of the Hotel Booking Spring Boot application, detailing its architecture, the purpose of each component, and instructions on how to build, run, and interact with the application.
1. Application Overview
The Hotel Booking application is a simple RESTful API built with Spring Boot, designed to manage hotel reservations. It follows a layered architecture, which promotes separation of concerns, making the codebase modular, maintainable, and scalable.

**1.	Key Features:**
•	CRUD Operations: Supports creating, viewing (all or by ID), updating, and canceling hotel bookings.
•	Hotel Search: Allows searching for bookings by hotel name (partial and case-insensitive).
•	RESTful API: All communication is done via standard HTTP methods with JSON payloads.
•	In-memory Database: Uses H2 database for easy development and testing, with a clear path to a production database.
•	Logging & Analytics: Basic logging is in place to track application activity.
•	Unit & Integration Tests: Comprehensive tests ensure the reliability of the application's logic and API endpoints.

**2.	Project Structure**
The project follows a standard Maven and Spring Boot directory structure:
hotel-booking-springboot/
├── pom.xml
├── src/main/java/com/hotelbooking/
│   ├── HotelBookingSpringbootApplication.java  (Main Spring Boot application)
│   ├── model/
│   │   └── Booking.java                      (JPA Entity for booking data)
│   ├── repository/
│   │   └── BookingRepository.java            (Spring Data JPA interface for database operations)
│   ├── service/
│   │   └── BookingService.java               (Business logic layer)
│   └── controller/
│       └── BookingController.java            (REST API endpoints)
├── src/main/resources/
│   └── application.properties                (Spring Boot configuration)
├── src/test/java/com/hotelbooking/
│   ├── service/
│   │   └── BookingServiceTest.java           (Unit tests for service layer)
│   └── controller/
│       └── BookingControllerTest.java        (Integration tests for controller layer)
└── README.md (Optional, but good practice)
![image](https://github.com/user-attachments/assets/e93fb8cd-0686-44c0-9997-b99bdb329b84)
