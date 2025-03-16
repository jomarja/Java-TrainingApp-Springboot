# Gym Application – Microservices Architecture

This Gym Application is built using a microservices architecture with Spring Boot, Eureka Server for service discovery, and JWT-based authentication. The system is composed of several independently deployable modules that communicate with each other over REST APIs. The main modules are:

- **Eureka Server:** Acts as a discovery service for other microservices.

- **Main Microservice:** Provides core functionalities including data persistence with PostgreSQL, API documentation, and various training endpoints and Offers controllers for managing trainers, trainees, training sessions, and authentication.

- **Trainer Workload Microservice:** Manages trainer workload updates and aggregation with internal database.

## Architecture Overview

The application is split into several microservices to improve scalability and maintainability:

- **Eureka Server** – Runs on port `8761` and registers all microservices.
- **Main Microservice** – Runs on port `8081`. It handles the core functionalities of the training application, including trainer and trainee management, training scheduling, and more.
- **Trainer Workload Microservice** – Runs on port `8082` and manages workload updates for trainers. It provides endpoints to update, retrieve monthly summaries, and get aggregate workload data.
- **Communication:** The microservices communicate with one another using REST endpoints. Eureka acts as the service registry to allow dynamic discovery of available services.

## Modules and Responsibilities

- **Eureka Server:**
    - **Main Class:** `com.trainingapp.server.server.ServerApplication`
    - **Properties:**
      ```properties
      server.port=8761
      eureka.client.register-with-eureka=false
      eureka.client.fetch-registry=false
      spring.application.name=eureka-server
      ```

- **Main Microservice:**
    - **Main Class:** `com.trainignapp.trainingapp.TrainingappApplication`
    - **Properties:**
      ```yaml
      spring:
        application:
          name: main-microservice
        profiles:
          active: local
        datasource:
          url: jdbc:postgresql://localhost:5432/trainingapp
          username: postgres
          password: password
          driver-class-name: org.postgresql.Driver
        jpa:
          hibernate:
            ddl-auto: update
          show-sql: true
          properties:
            hibernate:
              dialect: org.hibernate.dialect.PostgreSQLDialect
        doc:
          api-docs:
            enabled: true
          swagger-ui:
            enabled: true
      server:
        port: 8081
      management:
        endpoints:
          web:
            exposure:
              include: prometheus, metrics, info, health, shutdown, beans
      jwt:
        secret: "SOVhpdiDzs659O9ZiK2k+QTlt/Qf4fkHpQRdnn5/0FI="
      eureka:
        client:
          register-with-eureka: true
          fetch-registry: true
      ```

- **Trainer Workload Microservice:**
    - **Main Class:** `com.trainingapp.microservice.MicroserviceApplication`
    - **Properties:**
      ```properties
      server.port=8082
      spring.application.name=trainer-workload-microservice
      eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
      eureka.client.register-with-eureka=true
      eureka.client.fetch-registry=true
      jwt.secret=SOVhpdiDzs659O9ZiK2k+QTlt/Qf4fkHpQRdnn5/0FI=
      ```

## Prerequisites

Before running the application, ensure that you have the following installed:

- **Java 21 or later**
- **Maven 3.6+**
- **PostgreSQL:** Ensure a PostgreSQL database is running and accessible (configured in the Main Microservice properties).
- **Docker (Optional):** For running the database or other services in containers.

## Configuration Details

The application uses YAML and properties files for configuration:

- **Database:** Configured in the Main Microservice via `spring.datasource` properties.
- **Eureka:** Both the Main and Trainer Workload Microservices are configured to register with Eureka Server.
- **JWT:** Used for securing API endpoints. The secret is specified in the properties.
- **Swagger API Docs:** Enabled for easy API exploration in the Main Microservice.

## How to Run the Application

### 1. Build the Entire Project

Make sure you are going to each microservice folder of your project where the `pom.xml` is located.

```bash
mvn clean install //for each of the microservices
```
