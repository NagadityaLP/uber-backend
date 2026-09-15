# Uber Backend

A production-oriented ride-hailing backend built using **Java, Spring Boot, PostgreSQL, Docker, and microservices architecture**.

The project is designed as a learning and portfolio project to explore how a large-scale ride-hailing backend can be decomposed into independently deployable services, each owning its own business capability and data.

---

## 🎯 Project Goals

The primary goals of this project are to:

* Build a real-world backend using **Java and Spring Boot**
* Understand and implement **microservices architecture**
* Design clear service boundaries and responsibilities
* Use **database-per-service** architecture
* Implement synchronous and asynchronous service communication
* Handle distributed workflows and eventual consistency
* Explore scalability, reliability, and fault tolerance
* Implement concepts such as:

    * REST APIs
    * Kafka/event-driven communication
    * Retry mechanisms
    * Idempotency
    * Saga pattern
    * Distributed transactions
    * Caching
    * Observability
    * Real-time location tracking

The project will be developed incrementally, with architectural complexity introduced as the corresponding functionality is implemented.

---

# 🏗️ Architecture

The system is being designed as a collection of independently deployable microservices.

### High-Level Architecture

```text
                              ┌───────────────┐
                              │    Client     │
                              └───────┬───────┘
                                      │
                                      ▼
                              ┌───────────────┐
                              │  Trip Service │
                              └───────┬───────┘
                                      │
                    ┌─────────────────┼─────────────────┐
                    │                 │                 │
                    ▼                 ▼                 ▼
             ┌────────────┐   ┌────────────┐   ┌─────────────┐
             │  Routing   │   │  Pricing   │   │  Dispatch   │
             │  Service   │   │  Service   │   │  Service    │
             └────────────┘   └────────────┘   └──────┬──────┘
                                                      │
                                                      ▼
                                               ┌──────────────┐
                                               │    Driver    │
                                               │   Service    │
                                               └──────────────┘

             ┌──────────────┐
             │    User      │
             │   Service    │
             └──────────────┘

             ┌──────────────┐
             │   Location   │
             │   Service    │
             └──────────────┘
```

The architecture will evolve as additional services and communication mechanisms are implemented.

---

# 🧩 Microservices

| Service                | Responsibility                        | Status        |
| ---------------------- | ------------------------------------- | ------------- |
| **User Service**       | Rider/user management                 | ✅ Implemented |
| **Driver Service**     | Driver and vehicle management         | ✅ Implemented |
| **Pricing Service**    | Fare calculation and pricing rules    | ✅ Implemented |
| **Trip Service**       | Trip lifecycle and booking management | 🚧 Next       |
| **Routing Service**    | Distance, route and ETA calculation   | 📋 Planned    |
| **Dispatch Service**   | Driver-trip matching                  | 📋 Planned    |
| **Location Service**   | Real-time driver location tracking    | 🚧 Initial    |
| **Rating Service**     | Rider and driver ratings              | 📋 Planned    |
| **Monitoring Service** | System monitoring and observability   | 📋 Planned    |

---

# 🗄️ Database Architecture

Each microservice owns its own database.

```text
User Service
     │
     └── user_db

Driver Service
     │
     └── driver_db

Pricing Service
     │
     └── pricing_db

Trip Service
     │
     └── trip_db

Routing Service
     │
     └── routing_db

...
```

Services **do not directly access another service's database**.

For example:

```text
Trip Service
     │
     │  userId
     ▼
User Service
```

rather than:

```text
Trip Service
     │
     └── direct access → user_db   ❌
```

This keeps service ownership and boundaries clear.

---

# 🔄 Fare Estimation Flow

When a rider enters a pickup location and destination, the system needs route information before calculating the fare.

The intended flow is:

```text
Pickup + Destination
        │
        ▼
  Trip Service
        │
        ├───────────────► Routing Service
        │                     │
        │                     └── Distance + ETA
        │
        └───────────────► Pricing Service
                              │
                              └── Fare Estimate
```

The **Routing Service** is responsible for determining route-related information such as distance and estimated duration.

The **Pricing Service** uses that information together with the selected vehicle type and pricing rules to calculate the estimated fare.

---

# 💰 Pricing Service

The Pricing Service currently supports fare estimation based on:

```text
Base Fare
     +
Distance × Per-Kilometre Rate
     +
Duration × Per-Minute Rate
     ×
Surge Multiplier
     =
Estimated Fare
```

The service maintains pricing rules for different vehicle types:

* Sedan
* SUV
* Hatchback
* Auto

Monetary calculations use `BigDecimal` to avoid floating-point precision issues.

---

# 🔗 Service Communication

The project will use different communication mechanisms depending on the nature of the interaction.

### Synchronous communication

REST APIs will be used when an immediate response is required.

For example:

```text
Trip Service
     │
     │ REST
     ▼
Pricing Service
     │
     ▼
Fare Estimate
```

### Asynchronous communication

Event-driven communication using technologies such as Kafka will be introduced where asynchronous processing is more appropriate.

For example:

```text
Trip Service
     │
     │ TripCreated event
     ▼
    Kafka
     │
     ├────────► Dispatch Service
     │
     └────────► Monitoring Service
```

These mechanisms will be implemented as the corresponding services are developed.

---

# 🛠️ Technology Stack

### Backend

* Java
* Spring Boot
* Spring Data JPA
* Hibernate
* REST APIs
* Bean Validation

### Database

* PostgreSQL

### Infrastructure

* Docker
* Docker Compose

### API Documentation

* Swagger / OpenAPI

### Planned Technologies

* Apache Kafka
* Redis
* External routing/mapping API
* Centralized logging
* Distributed tracing
* Metrics and monitoring

---

# 📂 Project Structure

```text
Uber Backend/
│
├── README.md
├── docker-compose.yml
│
├── user-service/
├── driver-service/
├── pricing-service/
├── location-service/
│
├── trip-service/          # Coming next
├── routing-service/
├── dispatch-service/
├── rating-service/
└── monitoring-service/
```

The repository will also contain a `docs/` directory for detailed architectural documentation.

---

# 📊 Current Implementation Status

### User Service

* User entity
* PostgreSQL persistence
* Create/update/read APIs
* DTO-based API design
* Validation
* Exception handling
* Swagger documentation

### Driver Service

* Driver entity
* Vehicle information
* Driver status
* PostgreSQL persistence
* User validation through User Service
* REST APIs
* Exception handling

### Pricing Service

* Pricing rules
* Vehicle-specific pricing
* PostgreSQL persistence
* Fare estimation API
* Fare breakdown
* Surge multiplier
* Validation
* Exception handling

### Next

**Trip Service**

The Trip Service will own the lifecycle of a ride, including booking, driver assignment, trip state transitions, and eventually final fare handling.

---

# 🚀 Getting Started

The project uses Docker Compose for PostgreSQL infrastructure.

Start the databases with:

```bash
docker compose up -d
```

Each service can then be started independently from its respective Spring Boot project.

API documentation is available through Swagger UI for services that expose it.

---

# 📚 Documentation

Detailed architecture and design decisions will be maintained under:

```text
docs/
├── architecture.md
├── service-boundaries.md
├── communication.md
├── data-model.md
└── design-decisions.md
```

These documents will describe the reasoning behind architectural choices rather than simply documenting implementation details.

---

# 🔮 Future Enhancements

The project is intended to evolve toward a more production-oriented architecture.

Planned areas include:

* Driver dispatch and matching
* Real-time driver location tracking
* External routing integration
* Dynamic pricing
* Kafka-based event-driven communication
* Retry and fault-tolerance mechanisms
* Idempotent APIs
* Saga pattern
* Distributed transaction handling
* Redis caching
* API Gateway
* Authentication and authorization
* Centralized configuration
* Observability
* Metrics
* Distributed tracing
* Containerized deployment

---

# 🎓 Learning Focus

This project is primarily focused on understanding **why** certain backend architecture decisions are made.

The implementation will therefore evolve gradually rather than attempting to introduce every technology from the beginning.

Key areas of focus include:

* Microservice boundaries
* Domain ownership
* API design
* Database-per-service
* Service-to-service communication
* Distributed systems
* Scalability
* Reliability
* Consistency
* Event-driven architecture
* Production-oriented backend design
