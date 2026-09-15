# Service Boundaries

## 1. Purpose

Each microservice in the system owns a specific business capability.

The goal of defining explicit service boundaries is to avoid:

* Duplicate business logic
* Shared database access
* Excessive coupling
* Services becoming responsible for unrelated functionality
* Large services that gradually become monoliths

A service should own the data and business rules associated with its domain.

---

# 2. User Service

## Owns

User/rider identity and profile information.

## Responsibilities

* Create users
* Update users
* Retrieve users
* Validate user existence
* Manage user-specific information

## Does NOT own

* Trips
* Driver information
* Pricing
* Driver location
* Ratings

## Database

```text
user_db
```

---

# 3. Driver Service

## Owns

Driver and vehicle information.

## Responsibilities

* Driver registration
* Driver profile
* Vehicle information
* Driver status
* Driver availability information
* Driver-related validation

## Does NOT own

* Trip lifecycle
* Fare calculation
* Route calculation
* Real-time location processing
* Driver-trip matching logic

## Database

```text
driver_db
```

---

# 4. Pricing Service

## Owns

Pricing rules and fare calculation.

## Responsibilities

* Maintain vehicle-specific pricing rules
* Calculate distance-based charges
* Calculate time-based charges
* Apply surge multiplier
* Generate fare estimates
* Calculate final fares

## Does NOT own

* Trip lifecycle
* Driver assignment
* Route calculation
* User information
* Driver information

## Database

```text
pricing_db
```

### Example

The Pricing Service receives:

```json
{
  "distanceKm": 12.5,
  "durationMinutes": 30,
  "vehicleType": "SEDAN"
}
```

and returns the calculated fare.

It does not need to know which specific user requested the fare.

---

# 5. Trip Service

## Owns

The lifecycle of a ride.

This is one of the most important boundaries in the system.

## Responsibilities

* Create trip requests
* Maintain trip state
* Store pickup and destination
* Coordinate fare estimation
* Coordinate route calculation
* Track driver assignment
* Start trips
* Complete trips
* Cancel trips
* Maintain estimated fare
* Maintain final fare

## Does NOT own

### User data

Trip Service stores:

```text
userId
```

but User Service owns the actual user information.

### Driver data

Trip Service stores:

```text
driverId
```

but Driver Service owns the actual driver information.

### Pricing rules

Trip Service does not contain:

```text
baseFare
perKmRate
perMinuteRate
surgeMultiplier
```

Those belong to Pricing Service.

### Route calculation

Trip Service does not calculate:

```text
distance
ETA
route
```

Routing Service owns these responsibilities.

### Driver matching

Trip Service does not decide which driver is closest or most suitable.

Dispatch Service owns matching.

## Database

```text
trip_db
```

---

# 6. Routing Service

## Owns

Route calculation and route-related information.

## Responsibilities

* Calculate distance
* Calculate estimated duration
* Determine routes
* Integrate with external mapping/routing providers
* Normalize routing-provider responses

## Does NOT own

* Fare calculation
* Trip lifecycle
* Driver assignment
* User information
* Driver profiles

## Database

The need for persistent storage will depend on the eventual routing implementation.

---

# 7. Dispatch Service

## Owns

Driver-trip matching.

## Responsibilities

* Find candidate drivers
* Apply driver matching rules
* Consider driver availability
* Consider driver location
* Send trip requests to suitable drivers
* Handle driver acceptance/rejection
* Retry driver matching when appropriate

## Does NOT own

* Driver profiles
* Trip lifecycle
* Fare calculation
* Route calculation

Dispatch may communicate with:

```text
Driver Service
Location Service
Trip Service
```

but does not own their data.

---

# 8. Location Service

## Owns

Real-time driver location information.

## Responsibilities

* Receive driver location updates
* Maintain current driver location
* Provide nearby-driver information
* Support dispatch decisions
* Eventually support real-time location streaming

## Does NOT own

* Driver profile
* Vehicle details
* Trip lifecycle
* Fare calculation

The Driver Service knows who the driver is.

The Location Service knows where the driver currently is.

These are deliberately separate concerns.

---

# 9. Rating Service

## Owns

Ratings and reviews associated with completed trips.

## Responsibilities

* Submit ratings
* Store ratings
* Retrieve ratings
* Calculate aggregate ratings

## Does NOT own

* Trip lifecycle
* User profile
* Driver profile
* Pricing

## Database

```text
rating_db
```

---

# 10. Monitoring / Observability

## Owns

System observability rather than business-domain data.

Potential responsibilities include:

* Application metrics
* Service health
* Logs
* Distributed tracing
* Operational monitoring
* Alerts

Monitoring should observe other services without becoming responsible for their business logic.

---

# 11. Cross-Service Relationships

Services communicate through APIs or events rather than accessing each other's databases.

### Example: Driver creation

```text
Driver Service
      │
      │ userId
      ▼
User Service
      │
      ▼
Validate user
```

Driver Service does not directly query:

```text
user_db
```

---

# 12. Trip Creation

A simplified trip creation workflow looks like:

```text
                 Rider
                   │
                   ▼
             Trip Service
                   │
          ┌────────┴────────┐
          │                 │
          ▼                 ▼
   Routing Service    Pricing Service
          │                 │
          │                 │
     Distance + ETA    Fare Estimate
          │                 │
          └────────┬────────┘
                   ▼
             Trip Service
                   │
                   ▼
          Fare shown to rider
```

Trip Service coordinates the workflow, but does not implement the specialized business logic itself.

---

# 13. Driver Assignment

After a trip is confirmed:

```text
Trip Service
      │
      ▼
Dispatch Service
      │
      ├──────────────► Location Service
      │
      └──────────────► Driver Service
      │
      ▼
Selected Driver
      │
      ▼
Trip Service
```

The Dispatch Service decides which driver should be selected.

The Trip Service records the resulting assignment.

---

# 14. Domain Ownership

The following table summarizes ownership.

| Domain                    | Owner                    |
| ------------------------- | ------------------------ |
| User profile              | User Service             |
| Driver profile            | Driver Service           |
| Vehicle information       | Driver Service           |
| Driver status             | Driver Service           |
| Pricing rules             | Pricing Service          |
| Fare calculation          | Pricing Service          |
| Route                     | Routing Service          |
| Distance                  | Routing Service          |
| ETA                       | Routing Service          |
| Trip lifecycle            | Trip Service             |
| Driver-trip matching      | Dispatch Service         |
| Real-time driver location | Location Service         |
| Ratings                   | Rating Service           |
| System observability      | Monitoring/Observability |

---

# 15. Important Design Rule

A service should not become a general-purpose data provider for another service.

For example, Trip Service should not repeatedly depend on Driver Service for every piece of driver information if the information is not required for the workflow.

Instead, services should exchange only the data necessary for the operation.

This helps maintain loose coupling.

---

# 16. Shared Data vs Shared Code

The services may have concepts with identical values without sharing domain classes.

For example, both Driver Service and Pricing Service may have:

```text
VehicleType
```

with values such as:

```text
SEDAN
SUV
HATCHBACK
AUTO
```

However, Pricing Service should not import the `VehicleType` Java class from Driver Service.

Each service owns its own domain model.

Communication should use a stable contract such as:

```json
{
  "vehicleType": "SEDAN"
}
```

This prevents compile-time coupling between independently deployable services.

---

# 17. Boundary Validation

Each service is responsible for validating the data relevant to its own domain.

For example:

```text
Driver Service
    → Is this a valid driver?
    → Is the license number valid?
    → Is the vehicle information valid?

Pricing Service
    → Is the vehicle type supported?
    → Is distance valid?
    → Is duration valid?

Trip Service
    → Does the user exist?
    → Is the trip state transition valid?
    → Can this trip be cancelled?
```

Cross-domain validation should happen through service communication rather than direct database access.

---

# 18. Why These Boundaries Matter

The architecture is intentionally designed so that each service can evolve independently.

For example, the pricing algorithm could change from:

```text
Base + Distance + Time
```

to a much more sophisticated dynamic-pricing model without requiring Trip Service to understand the pricing algorithm.

Similarly, the routing provider could change without requiring Trip Service to know which external mapping provider is being used.

This is the primary benefit of maintaining clear service boundaries.
