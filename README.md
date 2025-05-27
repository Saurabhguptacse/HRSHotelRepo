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

<img width="716" alt="Screenshot 2025-05-27 at 10 22 17 PM" src="https://github.com/user-attachments/assets/5544cb44-ff12-412c-95fb-f26d90f893ea" />

**3. How to Build and Run the Application**
To get the application running, follow these steps:
1.	Prerequisites:
o	Java Development Kit (JDK) 11 or higher installed.
o	Apache Maven installed.
2.	Clone/Download the Project: Ensure you have all the files in the correct directory structure as described in Section 2.
3.	Build the Project: Open your terminal or command prompt, navigate to the root directory of the project (where pom.xml is located), and run the Maven build command:
4.	mvn clean install

This command will:
o	Clean the target directory.
o	Compile the Java source code.
o	Run all defined tests (unit and integration tests).
o	Package the application into an executable JAR file (hotel-booking-springboot-0.0.1-SNAPSHOT.jar) in the target directory.
5.	Run the Application: After a successful build, you can run the Spring Boot application using the generated JAR file:
6.	java -jar target/hotel-booking-springboot-0.0.1-SNAPSHOT.jar
![image](https://github.com/user-attachments/assets/1959a25d-d91e-40de-b9d1-5648b9b86d53)

