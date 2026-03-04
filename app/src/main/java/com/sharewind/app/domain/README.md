# domain — Domain Layer

The domain layer contains the **pure business logic** of the app. It has zero dependencies on Android frameworks, Retrofit, or Compose. It defines the contracts (interfaces) that the data layer implements and the use cases that the UI layer calls.

---

## Structure

```
domain/
├── model/          # Pure Kotlin domain models
├── repository/     # Repository interfaces (implemented in data layer)
└── usecase/        # One class per business operation
```

---

## `model/`

Immutable Kotlin data classes — no JSON annotations, no Android imports.

| Model | Key Fields |
|-------|-----------|
| `User` | id, fullName, phone, email, role, status, avatarUrl |
| `Vehicle` | id, driverId, brand, model, plateNumber, photoUrls, isActive |
| `Ride` | id, driverId, vehicleId, origin, destination, departureTime, price, status |
| `RideRequest` | id, userId, origin, destination, scheduledTime, priceSuggestion, status |
| `Offer` | id, driverId, requestId, price, message, status, expiresAt |
| `Notification` | id, event, title, body, isRead, createdAt |
| `TokenPair` | accessToken, refreshToken |
| `Subscription` | plan, rideQuota, requestQuota, ridesUsed, requestsUsed, resetAt |

---

## `repository/`

Interfaces only — implementations live in `data/repository/`.

```kotlin
interface RideRepository {
    suspend fun listDriverRides(filter: RideFilter): Result<List<Ride>>
    suspend fun listUserRequests(filter: RideFilter): Result<List<RideRequest>>
    suspend fun createRequest(request: CreateRideRequestParams): Result<RideRequest>
    suspend fun createRide(ride: CreateRideParams): Result<Ride>
    suspend fun getRideDetail(rideId: String): Result<Ride>
    suspend fun sendOffer(requestId: String, offer: OfferParams): Result<Offer>
    suspend fun acceptOffer(offerId: String): Result<Unit>
    suspend fun rejectOffer(offerId: String): Result<Unit>
}
```

---

## `usecase/`

One use case class per distinct business operation. Each use case has a single `invoke` operator.

### Auth
| Use Case | Description |
|----------|-------------|
| `RegisterUseCase` | Validate input + call `AuthRepository.register` |
| `LoginUseCase` | Credentials validation + login |
| `VerifyOtpUseCase` | OTP verification with retry tracking |
| `LogoutUseCase` | Invalidate tokens, clear DataStore |

### Rides
| Use Case | Description |
|----------|-------------|
| `GetHomeRidesUseCase` | Fetch EU or Driver feed based on active role |
| `CreateRideRequestUseCase` | Validate quota + submit EU request |
| `CreateRideUseCase` | Validate quota + submit Driver ride |
| `SendOfferUseCase` | Driver sends offer to EU request |
| `AcceptOfferUseCase` | EU accepts offer; triggers phone reveal |
| `GetRideDetailUseCase` | Fetch full ride details including confirmed phone |

### User & Vehicles
| Use Case | Description |
|----------|-------------|
| `GetProfileUseCase` | Fetch current user profile + subscription info |
| `UpdateProfileUseCase` | Update personal info |
| `ApplyForDriverModeUseCase` | Submit Driver application (license + vehicle) |
| `ManageVehiclesUseCase` | CRUD for Driver's vehicles |

---

## Conventions

- Use cases receive and return **domain models only** — never DTOs or UI state
- All use cases are `suspend` functions returning `Result<T>`
- Use cases are injected into ViewModels via **Hilt**
- No coroutine dispatchers in use cases — dispatching is the ViewModel's responsibility
