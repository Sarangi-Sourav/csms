# EV Charger Management System (CSMS)

A Spring Boot application designed to manage Electric Vehicle (EV) charging stations. This system provides functionalities for managing chargers, handling data persistence, and exposing RESTful APIs for integration. A key feature is its real-time communication with chargers using the OCPP 1.6-J protocol over WebSockets.

## Features

- **Charger Management**: Core logic for handling EV charger operations.
- **Real-time Charger Communication (OCPP 1.6-J)**: WebSocket endpoint for real-time, bidirectional communication with EV chargers.
- **RESTful APIs**: Endpoints for system management and interacting with the system (see `API_REQUEST_RESPONSE_SAMPLE.txt`).
- **Data Persistence**: Uses Spring Data JPA with MySQL for data storage.
- **Security**: Includes Spring Security Crypto for secure operations.
- **Validation**: Ensures data integrity using Jakarta Validation and Hibernate Validator.

## Tech Stack

- **Java**: 17
- **Framework**: Spring Boot 3.4.4
- **Real-time**: Spring WebSocket for OCPP 1.6-J communication
- **Build Tool**: Maven
- **Database**: MySQL
- **Other Libraries**: Lombok, Jackson (JSR310), SLF4J

## WebSocket Communication (OCPP 1.6-J)

The system exposes a WebSocket endpoint to handle communication with OCPP-compliant charging stations.

- **Endpoint**: `ws://<server-address>:<port>/ocpp1.6j/{chargerId}`
- **Example**: `ws://localhost:8089/ocpp1.6j/CP001`

The server listens for various OCPP actions from the charger, including:
- `BootNotification`
- `HeartbeatRequest`
- `StatusNotificationRequest`

It can also send commands to the charger, such as:
- `RemoteStartTransactionRequest`

The implementation handles message processing asynchronously to ensure high throughput.

## Prerequisites

- Java Development Kit (JDK) 17 or higher
- Maven
- MySQL Server

## Getting Started

### 1. Clone the Repository

```bash
git clone <repository-url>
cd csms
```

### 2. Database Setup

1.  Create a MySQL database named `CSMS`.
2.  Run the SQL script `CSMS_DB_Queries.sql` located in the root directory to initialize the database schema and data.

### 3. Configuration

Update the `src/main/resources/application.properties` file with your database credentials:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/CSMS
spring.datasource.username=root
spring.datasource.password=your_password
```

The application is configured to run on port **8089**.

### 4. Build and Run

Build the project using Maven:

```bash
mvn clean install
```

Run the application:

```bash
mvn spring-boot:run
```

## API Documentation

Sample API requests and responses for the RESTful endpoints are documented in the `API_REQUEST_RESPONSE_SAMPLE.txt` file.

## Project Structure

- `src/`: Main application source code.
- `CSMS_DB_Queries.sql`: Database initialization script.
- `API_REQUEST_RESPONSE_SAMPLE.txt`: API documentation samples.
- `pom.xml`: Maven project configuration.
- `CSM-Service/`: Additional service module.
