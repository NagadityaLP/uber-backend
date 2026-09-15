# Service Communication

## 1. Overview

The system uses a combination of **synchronous REST communication** and **asynchronous event-driven communication**.

The communication mechanism is selected based on the nature of the interaction.

The initial implementation focuses on REST-based synchronous communication. Apache Kafka will be introduced later for workflows that benefit from asynchronous processing and loose coupling.

---

# 2. Communication Principles

Services should communicate through well-defined contracts.

A service should not:

* Access another service's database directly
* Depend on another service's internal Java classes
* Assume that another service is always available
* Share persistence entities across services

Instead, services communicate through:

```text id="w0njt8"
REST APIs
   or
Events
```

---

# 3. Synchronous Communication

Synchronous communication is used when the caller needs an immediate response.

The caller sends a request and waits for the response.

```text id="yl1ih2"
Service A
   │
   │ Request
   ▼
Service B
   │
   │ Response
   ▼
Service A
```

REST APIs will be the primary synchronous communication mechanism.

---

# 4. Example: Driver Service → User Service

When a driver is registered, Driver Service needs to verify that the referenced user exists.

The interaction is:

```text id="7s3l1h"
Driver Service
      │
      │ userId
      ▼
User Service
      │
      ▼
Does user exist?
      │
      ▼
   Response
```

Driver Service does not query `user_db` directly.

The User Service remains responsible for user data.

---

# 5. Example: Trip Service → Routing Service

When a rider provides pickup and destination information, the Trip Service requires route information.

The flow is:

```text id="w3r2ut"
Trip Service
      │
      │ pickup + destination
      ▼
Routing Service
      │
      ├── distance
      ├── estimated duration
      └── route information
      │
      ▼
Trip Service
```

This is initially a synchronous interaction because the Trip Service requires the route information to proceed with fare estimation.

---

# 6. Example: Trip Service → Pricing Service

Once route information is available, the Trip Service requests a fare estimate.

```text id="d3fjvn"
Trip Service
      │
      │ distance
      │ duration
      │ vehicle type
      ▼
Pricing Service
      │
      ▼
Fare Estimate
      │
      ▼
Trip Service
```

The Pricing Service remains responsible for pricing calculations.

The Trip Service only consumes the result.

---

# 7. Complete Fare Estimation Flow

The complete synchronous workflow is:

```text id="3o1xq8"
             Rider
               │
               │ pickup + destination
               ▼
        ┌───────────────┐
        │ Trip Service  │
        └───────┬───────┘
                │
                │ Route Request
                ▼
        ┌───────────────┐
        │ Routing       │
        │ Service       │
        └───────┬───────┘
                │
                │ distance + ETA
                ▼
        ┌───────────────┐
        │ Trip Service  │
        └───────┬───────┘
                │
                │ Fare Request
                ▼
        ┌───────────────┐
        │ Pricing       │
        │ Service       │
        └───────┬───────┘
                │
                │ fare estimate
                ▼
        ┌───────────────┐
        │ Trip Service  │
        └───────┬───────┘
                │
                ▼
          Rider receives
          fare estimate
```

---

# 8. Why REST Is Appropriate Here

The fare estimation workflow is request/response oriented.

The rider expects an answer before deciding whether to proceed.

Therefore:

```text id="a6fqkq"
Trip Service
     │
     └── REST → Routing Service
                    │
                    └── response

Trip Service
     │
     └── REST → Pricing Service
                    │
                    └── response
```

Using an asynchronous event for this particular interaction would unnecessarily complicate the workflow.

---

# 9. Asynchronous Communication

Asynchronous communication allows a service to publish an event without waiting for every consumer to finish processing it.

A message broker such as Apache Kafka can be used.

```text id="i4r1tt"
Producer
   │
   │ Event
   ▼
 Kafka
   │
   ├────────► Consumer A
   │
   ├────────► Consumer B
   │
   └────────► Consumer C
```

The producer does not need to know exactly how each consumer processes the event.

---

# 10. When Kafka Will Be Useful

Kafka is planned for operations such as:

* Trip lifecycle events
* Driver assignment events
* Driver status changes
* Location-related events
* Notifications
* Monitoring
* Analytics
* Audit processing

For example:

```text id="5v7n3h"
Trip Service
     │
     │ TripCreated
     ▼
   Kafka
     │
     ├────────► Dispatch Service
     │
     ├────────► Notification Service
     │
     ├────────► Monitoring
     │
     └────────► Analytics
```

This avoids creating a synchronous dependency on every downstream consumer.

---

# 11. Synchronous vs Asynchronous Decision

The following general rule will be used:

| Requirement                                  | Communication |
| -------------------------------------------- | ------------- |
| Immediate response required                  | REST          |
| Request/response operation                   | REST          |
| Caller cannot continue without result        | REST          |
| Multiple services need to react to something | Kafka/Event   |
| Processing can happen later                  | Kafka/Event   |
| Notification                                 | Event         |
| Analytics                                    | Event         |
| Audit processing                             | Event         |
| Independent downstream processing            | Event         |

The actual decision will depend on the business workflow.

---

# 12. Trip Creation and Driver Dispatch

The intended architecture is:

```text id="y5j8gp"
                     Trip Service
                          │
                          │
                    Trip Created
                          │
                          ▼
                        Kafka
                          │
                          ▼
                  Dispatch Service
                          │
             ┌────────────┴────────────┐
             │                         │
             ▼                         ▼
      Driver Service           Location Service
             │                         │
             │                         │
             └────────────┬────────────┘
                          │
                          ▼
                   Driver Selected
                          │
                          ▼
                     Trip Service
```

The exact event flow will be finalized when Dispatch Service is implemented.

---

# 13. REST vs Kafka Is Not an Either/Or Decision

The architecture can use both mechanisms in the same workflow.

For example:

```text id="o1l1qm"
Trip Service
     │
     │ REST
     ▼
Pricing Service
     │
     └── Fare Estimate

Trip Service
     │
     │ Event
     ▼
Kafka
     │
     ▼
Dispatch Service
```

This is often more appropriate than trying to force all service communication into one mechanism.

---

# 14. API Contracts

REST APIs should expose explicit request and response contracts.

For example, Pricing Service exposes:

```text id="fd6q9u"
POST /api/pricing/estimate
```

Request:

```json id="yq4a7p"
{
  "distanceKm": 12.5,
  "durationMinutes": 30,
  "vehicleType": "SEDAN"
}
```

Response:

```json id="y3q9c1"
{
  "vehicleType": "SEDAN",
  "distanceKm": 12.5,
  "durationMinutes": 30,
  "baseFare": 50.00,
  "distanceFare": 187.50,
  "timeFare": 60.00,
  "surgeMultiplier": 1.00,
  "totalFare": 297.50
}
```

The API contract is separate from the Pricing Service's internal database model.

---

# 15. Event Contracts

Events should also have explicit contracts.

A conceptual event might look like:

```json id="pq0f4u"
{
  "eventId": "unique-event-id",
  "eventType": "TRIP_CREATED",
  "timestamp": "2026-09-15T12:30:00Z",
  "tripId": 1001,
  "userId": 501,
  "vehicleType": "SEDAN"
}
```

The exact event schema will be defined when Kafka integration is implemented.

---

# 16. Idempotency

Asynchronous communication introduces the possibility of receiving the same event more than once.

For example:

```text id="eq3c5m"
Kafka
  │
  ├── TripCreated
  │
  └── TripCreated   ← duplicate delivery
```

Consumers should therefore be designed to safely handle duplicate events where necessary.

An event identifier can be used to detect already-processed events.

```text id="6sy4pg"
eventId
   │
   ▼
Has this event already been processed?
   │
   ├── Yes → Ignore safely
   │
   └── No  → Process
```

Detailed idempotency strategies will be documented when the event-driven workflows are implemented.

---

# 17. Failure Handling

Network communication between services can fail.

For example:

```text id="8zy1dh"
Trip Service
     │
     │ REST
     ▼
Pricing Service
     X
   Timeout
```

The calling service should account for:

* Connection failures
* Timeouts
* Temporary service unavailability
* Invalid responses
* Partial failures

Future reliability mechanisms include:

* Timeouts
* Retries
* Exponential backoff
* Circuit breakers
* Fallback strategies
* Dead-letter topics for failed events

These mechanisms will be introduced based on actual requirements rather than added unnecessarily.

---

# 18. Avoiding Distributed Synchronous Chains

Long synchronous chains can increase latency and create cascading failures.

For example:

```text id="x8e0n4"
Client
  ↓
Trip
  ↓
Dispatch
  ↓
Driver
  ↓
Location
  ↓
Another Service
```

If every operation waits synchronously for the next service, a failure in one service can affect the entire request.

Therefore, asynchronous events will be introduced where the business workflow does not require an immediate response.

---

# 19. Current Communication Strategy

Currently implemented synchronous communication includes:

```text id="w4k4y7"
Driver Service
      │
      └── REST → User Service
```

Pricing Service currently exposes its own REST API:

```text id="08w4z2"
POST /api/pricing/estimate
```

The Trip → Routing → Pricing workflow will be implemented as Trip Service is developed.

Kafka-based communication is planned for later stages.

---

# 20. Communication Evolution

The communication architecture will evolve approximately as follows:

### Stage 1

```text id="6r2a0k"
REST
 │
 ├── Driver → User
 └── Trip → Pricing
```

### Stage 2

```text id="2u9v9v"
REST
 │
 ├── Trip → Routing
 └── Trip → Pricing

Events
 │
 └── Trip → Dispatch
```

### Stage 3

```text id="h42k7p"
REST + Kafka
        │
        ├── Synchronous business queries
        ├── Trip lifecycle events
        ├── Driver events
        ├── Location events
        └── Monitoring/analytics events
```

The final communication architecture will be documented as the implementation progresses.
