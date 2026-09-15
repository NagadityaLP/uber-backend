# System Architecture

## 1. Overview

The Uber Backend project is designed as a distributed system consisting of multiple independently deployable microservices.

Each service is responsible for a specific business capability and owns the data associated with that capability.

The architecture follows the following principles:

* Single responsibility per service
* Database-per-service
* Independent service ownership
* Loose coupling between services
* API-based service communication
* Asynchronous communication where appropriate
* Eventual consistency where required
* Independent scalability of services

---

# 2. High-Level Architecture

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
                         ┌────────────────┼────────────────┐
                         │                │                │
                         ▼                ▼                ▼
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
                  │     User     │
                  │    Service   │
                  └──────────────┘

                  ┌──────────────┐
                  │   Location   │
                  │    Service   │
                  └──────────────┘
```

The diagram represents the intended architecture. Individual services will be implemented incrementally.

---

# 3. Service Responsibilities

## 3.1 User Service

The User Service owns rider/user information.

### Responsibilities

* Create users
* Update user information
* Retrieve user information
* Validate whether a user exists
* Manage user-specific data

### Data ownership

```text
User Service
     │
     └── user_db
```

The User Service is the only service responsible for modifying user data.

---

## 3.2 Driver Service

The Driver Service owns driver and vehicle information.

### Responsibilities

* Driver registration
* Driver information
* Vehicle information
* Driver availability/status
* Driver validation
* Driver-related operations

### Data ownership

```text
Driver Service
     │
     └── driver_db
```

The Driver Service does not directly access `user_db`.

If it needs to validate a user, it communicates with the User Service.

```text
Driver Service
      │
      │ REST
      ▼
User Service
      │
      ▼
 user_db
```

---

## 3.3 Pricing Service

The Pricing Service owns pricing rules and fare calculation.

### Responsibilities

* Maintain pricing rules
* Vehicle-specific pricing
* Calculate distance-based fare
* Calculate time-based fare
* Apply surge multiplier
* Return fare estimates
* Eventually calculate final trip fares

### Data ownership

```text
Pricing Service
     │
     └── pricing_db
```

The Pricing Service does not need access to the Trip Service database.

It receives the information required to calculate a fare through its API.

---

## 3.4 Trip Service

The Trip Service owns the lifecycle of a ride.

### Responsibilities

* Create trip requests
* Maintain trip state
* Store pickup and destination
* Coordinate route and fare estimation
* Track driver assignment
* Start trips
* Complete trips
* Handle trip cancellation
* Maintain estimated and final fare information

### Data ownership

```text
Trip Service
     │
     └── trip_db
```

The Trip Service acts as the central business workflow for a ride, while individual capabilities remain owned by their respective services.

---

## 3.5 Routing Service

The Routing Service is responsible for route-related calculations.

### Responsibilities

* Calculate distance
* Calculate estimated travel duration
* Determine route information
* Integrate with external mapping/routing providers

The service may eventually use an external routing provider through an abstraction layer.

```text
Trip Service
      │
      ▼
Routing Service
      │
      ▼
External Routing Provider
```

The external provider should remain an implementation detail of the Routing Service rather than being called directly by the Trip Service.

---

## 3.6 Dispatch Service

The Dispatch Service is responsible for matching a trip request with an appropriate driver.

### Responsibilities

* Find available drivers
* Consider driver location
* Match driver to trip
* Handle driver acceptance/rejection
* Retry matching when necessary
* Coordinate driver assignment

Conceptually:

```text
Trip Request
     │
     ▼
Dispatch Service
     │
     ├── Driver availability
     │
     ├── Driver location
     │
     └── Matching rules
     │
     ▼
Selected Driver
```

---

## 3.7 Location Service

The Location Service manages real-time driver location information.

### Responsibilities

* Receive driver location updates
* Store or cache current driver location
* Provide nearby-driver location information
* Support dispatch decisions
* Eventually support real-time location streaming

The Location Service is expected to become one of the more performance-sensitive services in the system.

---

## 3.8 Rating Service

The Rating Service will own rider and driver ratings.

### Responsibilities

* Submit ratings
* Retrieve ratings
* Maintain rating history
* Calculate aggregate ratings

```text
Rating Service
      │
      └── rating_db
```

---

## 3.9 Monitoring Service

The Monitoring Service will focus on system observability.

Potential responsibilities include:

* Service health
* Metrics
* Application logs
* Distributed tracing
* Operational monitoring
* Failure detection

The exact implementation will be decided when the observability layer is introduced.

---

# 4. Database Architecture

The system follows the **database-per-service** pattern.

```text
┌──────────────────┐       ┌──────────────────┐
│   User Service   │       │  Driver Service  │
│                  │       │                  │
│     user_db      │       │    driver_db     │
└──────────────────┘       └──────────────────┘


┌──────────────────┐       ┌──────────────────┐
│ Pricing Service  │       │   Trip Service   │
│                  │       │                  │
│   pricing_db     │       │     trip_db      │
└──────────────────┘       └──────────────────┘
```

Each service owns its database.

### Why?

This provides:

* Data ownership
* Service independence
* Independent schema evolution
* Independent scaling
* Reduced coupling
* Freedom to change persistence implementation later

---

# 5. Cross-Service Data

Services should not create direct database relationships with entities owned by another service.

For example, Trip Service should not contain:

```java
@ManyToOne
private User user;
```

where `User` is owned by User Service.

Instead, Trip Service stores:

```text
userId
```

Similarly, a trip stores:

```text
driverId
```

rather than maintaining a JPA relationship with the Driver entity.

The service can communicate with the owning service when additional information is required.

---

# 6. Fare Estimation Flow

A ride begins with a pickup location and destination.

The system first needs route information.

```text
Pickup + Destination
        │
        ▼
  Trip Service
        │
        ▼
 Routing Service
        │
        ├── Distance
        └── Estimated Duration
        │
        ▼
  Trip Service
        │
        ▼
 Pricing Service
        │
        ▼
   Fare Estimate
```

The rider can then review the estimated fare before confirming the booking.

---

# 7. Estimate vs Final Fare

The estimated fare and final fare are separate concepts.

### Estimated fare

Calculated before the trip begins using information such as:

* Estimated distance
* Estimated duration
* Vehicle type
* Current pricing rules
* Surge multiplier

### Final fare

Calculated after or during the trip using actual trip information.

Potential inputs include:

* Actual distance
* Actual duration
* Waiting time
* Applicable surge
* Cancellation fees
* Other applicable pricing rules

Conceptually:

```text
                   ┌──────────────────┐
                   │  Fare Estimate   │
                   └────────┬─────────┘
                            │
                       Booking
                            │
                            ▼
                       Trip Starts
                            │
                            ▼
                    Actual Trip Data
                            │
                            ▼
                   ┌──────────────────┐
                   │   Final Fare     │
                   └──────────────────┘
```

---

# 8. Synchronous Communication

REST will initially be used for operations where the caller requires an immediate response.

For example:

```text
Trip Service
     │
     │ GET/POST
     ▼
Pricing Service
     │
     ▼
Fare Estimate
```

Another example is driver validation:

```text
Driver Service
     │
     │ REST
     ▼
User Service
     │
     ▼
User exists?
```

Synchronous communication should be used when the response is required before the current workflow can continue.

---

# 9. Asynchronous Communication

Asynchronous communication will be introduced for events that do not require an immediate response.

Apache Kafka is planned for event-driven communication.

Example:

```text
Trip Service
     │
     │ TripCreated
     ▼
   Kafka
     │
     ├──────────────► Dispatch Service
     │
     ├──────────────► Monitoring Service
     │
     └──────────────► Other consumers
```

This allows consumers to process events independently.

---

# 10. Communication Strategy

The system will not use one communication mechanism for everything.

The choice depends on the business requirement.

### REST

Use when:

* An immediate response is required
* The caller needs the result to continue
* The operation is request/response oriented

### Kafka / Events

Use when:

* Immediate response is not required
* Multiple services need to react to an event
* Loose coupling is beneficial
* Asynchronous processing is appropriate

The exact boundaries will be refined as the services are implemented.

---

# 11. Scalability Considerations

One advantage of the microservice architecture is that individual services can scale independently.

For example, the Location Service may receive a significantly larger number of requests than the Rating Service.

Therefore:

```text
Location Service
      │
      ├── Instance 1
      ├── Instance 2
      ├── Instance 3
      └── Instance 4
```

while:

```text
Rating Service
      │
      └── Instance 1
```

may be sufficient.

The architecture allows scaling decisions to be made independently for each service.

---

# 12. Reliability Considerations

Distributed systems introduce failure scenarios.

For example:

```text
Trip Service
     │
     ▼
Pricing Service
     │
     X
  Failure
```

The Trip Service should not blindly assume that every downstream service is always available.

The project will eventually explore:

* Timeouts
* Retries
* Retry limits
* Idempotency
* Circuit breakers
* Graceful failure
* Dead-letter handling for asynchronous events

These mechanisms will be introduced when the corresponding workflows are implemented.

---

# 13. Distributed Transactions

A single database transaction cannot span all microservices in the same way a local database transaction can.

For example:

```text
Trip Service
     │
     ├── Create Trip
     │
     ├── Assign Driver
     │
     └── Update another service
```

These operations may involve multiple services and databases.

The project will eventually explore distributed transaction patterns such as:

* Saga pattern
* Compensating actions
* Eventual consistency

The implementation will be introduced only when a workflow actually requires it.

---

# 14. Current Architecture Status

Currently implemented:

```text
User Service
     │
     └── PostgreSQL

Driver Service
     │
     └── PostgreSQL

Pricing Service
     │
     └── PostgreSQL
```

The next major component is:

```text
Trip Service
```

After that, the architecture will progressively introduce:

```text
Routing
   ↓
Dispatch
   ↓
Location
   ↓
Kafka / Events
   ↓
Reliability + Observability
```

The architecture document will be updated as implementation decisions are made.
