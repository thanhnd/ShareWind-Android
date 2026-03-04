# data — Data Layer

The data layer is responsible for all data access: remote API calls, local caching, and repository implementations. It is the only layer that knows about Retrofit, DataStore, or any external data source.

---

## Structure

```
data/
├── remote/
│   ├── api/          # Retrofit service interfaces
│   ├── dto/          # JSON request/response data classes
│   └── interceptor/  # OkHttp interceptors (auth, logging)
├── local/
│   ├── datastore/    # DataStore (tokens, user prefs)
│   └── cache/        # In-memory or room cache (optional)
└── repository/       # Repository implementations (implement domain interfaces)
```

---

## `remote/api/`

Retrofit service interfaces, one per backend domain:

| Interface | Endpoints |
|-----------|-----------|
| `AuthApiService` | `POST /auth/register`, `/auth/login`, `/auth/otp/verify`, `/auth/refresh`, `/auth/logout` |
| `UserApiService` | `GET/PUT /users/me`, `POST /users/me/avatar`, `POST /users/driver-application` |
| `RideApiService` | `GET/POST /rides`, `GET/POST /ride-requests`, `GET /rides/{id}` |
| `OfferApiService` | `POST /offers`, `PUT /offers/{id}/accept`, `PUT /offers/{id}/reject` |
| `VehicleApiService` | `GET/POST/PUT/DELETE /vehicles`, `POST /vehicles/{id}/photos` |
| `NotificationApiService` | `GET /notifications`, `PUT /notifications/{id}/read`, `POST /device-tokens` |

---

## `remote/interceptor/`

| Interceptor | Description |
|-------------|-------------|
| `AuthInterceptor` | Reads access token from DataStore; appends `Authorization: Bearer` header |
| `TokenRefreshInterceptor` | On `401`, calls refresh endpoint, stores new tokens, retries original request |
| `LoggingInterceptor` | OkHttp logging (DEBUG builds only) |

---

## `local/datastore/`

Uses Jetpack **DataStore (Preferences)** — no SQLite for auth state.

| Key | Type | Description |
|-----|------|-------------|
| `access_token` | String | Current JWT access token |
| `refresh_token` | String | JWT refresh token |
| `user_id` | String | Logged-in user UUID |
| `user_role` | String | `end_user` / `driver` / `both` |
| `active_role` | String | Currently selected role in dual-role mode |
| `fcm_token` | String | Last registered FCM token |

Sensitive values (tokens) are stored in **EncryptedDataStore** backed by Android Keystore.

---

## `repository/`

Implements domain repository interfaces. Decides whether to fetch from remote or serve from cache.

| Implementation | Domain Interface |
|---------------|-----------------|
| `AuthRepositoryImpl` | `AuthRepository` |
| `UserRepositoryImpl` | `UserRepository` |
| `RideRepositoryImpl` | `RideRepository` |
| `VehicleRepositoryImpl` | `VehicleRepository` |
| `NotificationRepositoryImpl` | `NotificationRepository` |

All repositories are injected via **Hilt** (`@Singleton`).

---

## Conventions

- DTOs (data layer) are mapped to domain Models before leaving the repository
- Network calls use Kotlin `suspend` functions and return `Result<T>`
- Errors are wrapped in a sealed `ApiError` class before propagating to the domain layer
