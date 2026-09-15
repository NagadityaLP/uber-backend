# Data Model

## 1. Overview

The system follows a **database-per-service** architecture.

Each microservice owns the data required for its business capability.

Services communicate using APIs and events rather than directly accessing another service's database.

```text
User Service       → user_db
Driver Service     → driver_db
Pricing Service    → pricing_db
Trip Service       → trip_db
Rating Service     → rating_db
```

This allows each service to evolve its schema independently.

---

# 2. User Service

## User

The User Service owns rider information.

### Entity

```text
User
├── id
├── name
├── email
├── phoneNo
└── createdAt
```

### Database

```text
user_db
└── users
```

The User Service is the source of truth for user information.

Other services should store the user's identifier rather than duplicating the complete User entity.

---

# 3. Driver Service

## Driver

The Driver Service owns driver and vehicle information.

### Entity

```text
Driver
├── id
├── userId
├── licenseNumber
├── status
├── vehicleNumber
├── vehicleType
├── vehicleModel
├── createdAt
└── updatedAt
```

### Database

```text
driver_db
└── drivers
```

### Important relationship

`userId` identifies the user associated with the driver.

However, this is **not a database foreign key to `user_db`**.

```text
Driver Service
      │
      └── userId
             │
             │ API
             ▼
        User Service
```

This preserves database ownership between services.

---

# 4. Pricing Service

## PricingRule

The Pricing Service owns vehicle-specific pricing rules.

### Entity

```text
PricingRule
├── id
├── vehicleType
├── baseFare
├── perKmRate
├── perMinuteRate
├── surgeMultiplier
├── createdAt
└── updatedAt
```

### Database

```text
pricing_db
└── pricing_rules
```

### Vehicle Type

The Pricing Service maintains its own `VehicleType` domain enum.

```text
VehicleType
├── SEDAN
├── SUV
├── HATCHBACK
└── AUTO
```

The same conceptual vehicle types may exist in Driver Service, but the Java enum is not shared between services.

---

# 5. Fare Estimate

Fare estimation is represented as an API request/response rather than as a persistent entity.

### Request

```text
FareEstimateRequest
├── distanceKm
├── durationMinutes
└── vehicleType
```

### Response

```text
FareEstimateResponse
├── vehicleType
├── distanceKm
├── durationMinutes
├── baseFare
├── distanceFare
├── timeFare
├── surgeMultiplier
└── totalFare
```

The Pricing Service calculates these values using the applicable `PricingRule`.

---

# 6. Trip Service

The Trip Service will own the ride itself.

The initial conceptual model is:

```text
Trip
├── id
├── userId
├── driverId
├── pickupLatitude
├── pickupLongitude
├── destinationLatitude
├── destinationLongitude
├── distanceKm
├── estimatedDurationMinutes
├── estimatedFare
├── finalFare
├── status
├── createdAt
├── startedAt
└── completedAt
```

### Database

```text
trip_db
└── trips
```

The exact schema will be finalized before implementing Trip Service.

---

# 7. Trip References to Other Services

A Trip contains identifiers belonging to other domains.

For example:

```text
Trip
├── userId
└── driverId
```

These identifiers are references at the application level, not database relationships.

### Incorrect approach

```java
@ManyToOne
private User user;
```

where `User` belongs to User Service.

### Preferred approach

```java
private Long userId;
```

The same principle applies to `driverId`.

---

# 8. Why We Don't Use Cross-Service Foreign Keys

Consider:

```text
trip_db
     │
     └── FOREIGN KEY → user_db
```

This creates a dependency between two independently owned databases.

It creates problems such as:

* Coupled schema changes
* Difficult independent deployment
* Database-level dependency between services
* Increased operational complexity
* Reduced service autonomy

Instead:

```text
trip_db
   │
   └── userId

Trip Service
   │
   │ REST/Event
   ▼
User Service
```

The service boundary remains intact.

---

# 9. Data Duplication

Database-per-service does not mean that data can never be duplicated.

Some data may eventually be copied into another service when doing so improves performance or availability.

For example, Dispatch Service may eventually maintain a local representation of:

```text
Driver ID
Driver status
Current location
Vehicle type
```

However, the authoritative driver information remains owned by Driver Service.

This is an intentional form of controlled data replication rather than shared database access.

---

# 10. Real-Time Location Data

Location data has different characteristics from relatively static driver profile data.

The Location Service will eventually manage information such as:

```text
DriverLocation
├── driverId
├── latitude
├── longitude
└── timestamp
```

Depending on the final implementation, this data may be stored using a fast-access data store rather than PostgreSQL.

The design will be finalized when the Location Service is implemented.

---

# 11. Rating Data

The Rating Service will eventually own rating information.

A conceptual model is:

```text
Rating
├── id
├── tripId
├── fromUserId
├── toUserId
├── rating
├── comment
└── createdAt
```

The exact fields and relationships will be finalized when the Rating Service is implemented.

The Rating Service should reference users, drivers, and trips using identifiers rather than cross-database relationships.

---

# 12. Domain Ownership

The overall ownership can be summarized as:

```text
User Service
    └── User

Driver Service
    └── Driver
         └── Vehicle information

Pricing Service
    └── PricingRule

Trip Service
    └── Trip

Routing Service
    └── Route information

Location Service
    └── DriverLocation

Rating Service
    └──
```
