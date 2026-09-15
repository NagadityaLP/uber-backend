# Architecture Design Decisions

This document records important architectural and technical decisions made during the development of the Uber Backend project.

The purpose is to capture not only what was implemented, but also the reasoning behind the decisions.

---

# ADR-001: Use Microservices Architecture

## Decision

The system is implemented as independently deployable microservices rather than a single monolithic application.

## Reason

A ride-hailing platform contains multiple distinct business capabilities such as:

* User management
* Driver management
* Pricing
* Routing
* Trip management
* Dispatch
* Location tracking

Separating these capabilities allows each service to have a focused responsibility.

## Benefits

* Independent deployment
* Independent scaling
* Clear ownership
* Smaller codebases
* Technology flexibility
* Better isolation of business capabilities

## Trade-off

Microservices introduce additional complexity:

* Network communication
* Distributed failures
* Data consistency
* Service discovery
* Observability
* Deployment complexity

The project therefore introduces distributed-system concepts incrementally.

---

# ADR-002: Database Per Service

## Decision

Each microservice owns its own database.

```text id="e5d4ab"
User Service       → user_db
Driver Service     → driver_db
Pricing Service    → pricing_db
Trip Service       → trip_db
```

## Reason

A service should own both its business logic and its data.

Other services must not directly access that database.

## Benefits

* Independent schema evolution
* Strong service ownership
* Reduced coupling
* Independent scaling
* Better service autonomy

## Trade-off

Cross-service workflows become more difficult because a single database transaction cannot simply cover multiple services.

This will require patterns such as:

* Events
* Eventual consistency
* Saga
* Compensating actions

where appropriate.

---

# ADR-003: Do Not Create Cross-Service JPA Relationships

## Decision

Entities belonging to another microservice will not be represented using JPA relationships.

For example, Trip Service will not have:

```java id="xj8q4a"
@ManyToOne
private User user;
```

where `User` belongs to User Service.

Instead:

```java id="8p7t4c"
private Long userId;
```

will be stored.

## Reason

JPA relationships are designed for entities within the same persistence boundary.

Creating cross-service relationships would tightly couple services and databases.

## Result

The service owns only its own domain entities.

---

# ADR-004: Each Service Owns Its Domain Model

## Decision

Domain classes such as enums and entities are not shared between microservices.

For example, Driver Service and Pricing Service may both have:

```text id="r7l1hf"
VehicleType
```

with:

```text id="z3l5ne"
SEDAN
SUV
HATCHBACK
AUTO
```

but each service maintains its own representation.

## Reason

Sharing Java classes between independently deployable services creates compile-time coupling.

A change in one service could force another service to change and redeploy.

## Communication

Services exchange values through API/event contracts:

```json id="z6kw8v"
{
  "vehicleType": "SEDAN"
}
```

This keeps the services independently deployable.

---

# ADR-005: Use REST for Synchronous Operations

## Decision

REST APIs are used when the calling service requires an immediate response.

Examples include:

```text id="8mt3a0"
Driver Service → User Service
Trip Service   → Pricing Service
Trip Service   → Routing Service
```

## Reason

These operations are request/response oriented.

For example, a rider needs a fare estimate before confirming a ride.

Therefore, the Trip Service needs a response from Pricing Service before proceeding.

## Trade-off

Synchronous calls introduce runtime dependencies.

If the downstream service is unavailable, the calling service may also be affected.

Timeouts, retries, and circuit breakers will be considered where appropriate.

---

# ADR-006: Use Kafka for Asynchronous Workflows

## Decision

Apache Kafka will be introduced for events where an immediate response is not required.

Potential events include:

```text id="1f8wks"
TripCreated
DriverAssigned
TripStarted
TripCompleted
DriverStatusChanged
```

## Reason

Events allow multiple services to react independently.

For example:

```text id="q0yrw5"
Trip Service
     │
     │ TripCompleted
     ▼
   Kafka
     │
     ├──► Rating Service
     ├──► Monitoring
     └──► Analytics
```

The Trip Service does not need to synchronously call every consumer.

## Trade-off

Event-driven systems introduce:

* Eventual consistency
* Duplicate delivery concerns
* Ordering considerations
* Consumer failure handling
* More complex debugging

These concerns will be handled when Kafka is introduced.

---

# ADR-007: Use BigDecimal for Monetary Calculations

## Decision

`BigDecimal` is used for monetary values.

Examples:

```text id="8d8zpf"
baseFare
perKmRate
perMinuteRate
surgeMultiplier
estimatedFare
finalFare
```

## Reason

Floating-point types such as `double` can introduce precision errors.

For financial calculations, deterministic decimal arithmetic is preferred.

## Example

Pricing Service calculates:

```text id="z88mnr"
distanceFare
+
timeFare
+
baseFare
=
subtotal

subtotal × surgeMultiplier
=
totalFare
```

The result is rounded explicitly using a defined rounding mode.

---

# ADR-008: Separate Fare Estimation from Trip Lifecycle

## Decision

Pricing logic belongs to Pricing Service while trip lifecycle belongs to Trip Service.

## Reason

These are separate business responsibilities.

Pricing Service answers:

> What should this ride cost based on the provided pricing inputs?

Trip Service answers:

> What is the current state and lifecycle of this ride?

## Result

The architecture becomes:

```text id="d3lhk7"
Trip Service
     │
     ├──► Routing Service
     │
     └──► Pricing Service
```

Trip Service coordinates the workflow but does not implement the pricing algorithm itself.

---

# ADR-009: Separate Estimated Fare and Final Fare

## Decision

The system treats estimated fare and final fare as separate concepts.

## Reason

The fare displayed before booking is based on estimated information.

The final fare may depend on actual trip information.

```text id="v1zqkd"
Estimated distance
Estimated duration
       │
       ▼
Estimated Fare
       │
       ▼
Trip
       │
       ▼
Actual distance
Actual duration
       │
       ▼
Final Fare
```

This allows the pricing model to evolve without changing the fundamental trip lifecycle.

---

# ADR-010: Introduce External Routing Through an Abstraction

## Decision

The Routing Service will hide the external mapping/routing provider behind an internal abstraction.

Conceptually:

```text id="z1m8dd"
             Routing Service
                   │
             RoutingProvider
              /           \
             /             \
            ▼               ▼
     Provider A        Provider B
```

## Reason

The business services should not depend directly on a particular external mapping provider.

This allows the implementation to change without requiring changes to Trip Service.

## Future Possibilities

Potential providers may include commercial or open routing services.

The initial implementation may use a mock/local implementation for development before integrating an external provider.

---

# ADR-011: Introduce Complexity Incrementally

## Decision

Advanced distributed-system technologies will be introduced when the architecture requires them rather than from the beginning.

The project will evolve approximately as:

```text id="kq7n9c"
Basic Microservices
       ↓
REST Communication
       ↓
Trip Workflow
       ↓
Routing + Dispatch
       ↓
Kafka / Events
       ↓
Retries + Idempotency
       ↓
Saga / Eventual Consistency
       ↓
Caching
       ↓
Observability
```

## Reason

Introducing every technology at the beginning would obscure the core business workflow and make the system unnecessarily difficult to understand.

Each technology should solve an actual architectural problem.

---

# ADR-012: Keep Business Logic Inside the Owning Service

## Decision

Business rules should live in the service that owns the corresponding domain.

Examples:

```text id="2w4x90"
Pricing rules
    → Pricing Service

Driver matching
    → Dispatch Service

Trip state transitions
    → Trip Service

Route calculation
    → Routing Service

User management
    → User Service
```

## Reason

This prevents business logic from being duplicated across services.

For example, Trip Service should request a fare from Pricing Service rather than implementing its own pricing formula.

---

# ADR-013: Use DTOs at Service Boundaries

## Decision

API request and response DTOs are used instead of exposing persistence entities directly as API contracts.

The general flow is:

```text id="g3m4e1"
HTTP Request
     ↓
Request DTO
     ↓
Service Layer
     ↓
Entity / Repository
     ↓
Response DTO
     ↓
HTTP Response
```

## Reason

Persistence models and API contracts have different responsibilities.

Separating them allows the database schema to evolve without necessarily changing the public API.

---

# ADR-014: Use Docker Compose for Local Infrastructure

## Decision

PostgreSQL databases are run using Docker Compose during local development.

Current databases include:

```text id="qv9c5s"
User DB       → localhost:5433
Driver DB     → localhost:5434
Pricing DB    → localhost:5435
```

Future services will receive their own database infrastructure.

## Reason

Docker provides:

* Consistent local environments
* Easy database setup
* Service isolation
* Reproducible development environments
* Less dependency on locally installed database software

---

# ADR-015: Document Architecture Alongside Implementation

## Decision

Architecture and design decisions are maintained inside the repository.

Documentation is stored under:

```text id="8e2lcs"
docs/
```

## Reason

Architecture changes as the system evolves.

Keeping documentation alongside source code makes design decisions:

* Version controlled
* Reviewable
* Traceable
* Easier to maintain

The documentation should reflect the actual implementation rather than an idealized system that has not yet been built.

---

# 16. Decision Review

Architecture decisions are not permanently fixed.

As the system evolves, a decision may be:

* Retained
* Modified
* Superseded
* Replaced

When a significant architectural decision changes, the documentation should be updated to explain the reason.

The goal is not to predict the final architecture perfectly at the beginning.

The goal is to make deliberate, understandable decisions as the system evolves.
