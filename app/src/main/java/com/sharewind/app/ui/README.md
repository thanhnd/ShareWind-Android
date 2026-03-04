# ui — Presentation Layer (Jetpack Compose)

All screens, composables, and ViewModels. Built entirely with **Jetpack Compose** following the MVVM pattern. ViewModels expose `StateFlow<UiState>` and handle user events through sealed `UiEvent` classes.

---

## Structure

```
ui/
├── auth/             # Login, Register, OTP screens
├── home/             # End-user and Driver home feeds
├── ride/             # Create request/ride, ride detail, my rides
├── offer/            # Offer received popup, send offer sheet
├── vehicle/          # Vehicle CRUD + photo management
├── notifications/    # Notification list and detail
├── profile/          # Account settings, role toggle, subscription
├── navigation/       # NavGraph, routes, deep-link handling
└── theme/            # Colors, typography, shapes (Material 3)
```

---

## Screen Map

### Auth
| Screen | Route | Description |
|--------|-------|-------------|
| `LoginScreen` | `auth/login` | Email/phone + password login |
| `RegisterScreen` | `auth/register` | New account form |
| `OtpScreen` | `auth/otp` | 6-digit OTP input with timer |

### Home
| Screen | Route | Description |
|--------|-------|-------------|
| `EndUserHomeScreen` | `home/eu` | Driver ride feed with filters |
| `DriverHomeScreen` | `home/driver` | EU request feed with filters |

### Ride
| Screen | Route | Description |
|--------|-------|-------------|
| `CreateRequestScreen` | `ride/create-request` | EU ride request form |
| `CreateRideScreen` | `ride/create-ride` | Driver ride form |
| `RideDetailScreen` | `ride/{id}` | Full ride detail + click-to-call |
| `MyRidesScreen` | `ride/my-rides` | Active + past rides |
| `RequestDetailScreen` | `ride/request/{id}` | EU request detail for Driver |

### Other
| Screen | Route | Description |
|--------|-------|-------------|
| `NotificationsScreen` | `notifications` | Full notification history |
| `NotificationDetailScreen` | `notifications/{id}` | Detail with deep-link action |
| `ProfileScreen` | `profile` | Account info + subscription |
| `VehicleListScreen` | `vehicles` | Driver vehicle management |
| `VehicleFormScreen` | `vehicles/new` / `vehicles/{id}/edit` | Add / edit vehicle |

---

## ViewModel Pattern

```kotlin
// Unidirectional data flow
data class HomeUiState(
    val rides: List<Ride> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class HomeUiEvent {
    object Refresh : HomeUiEvent()
    data class ApplyFilter(val filter: RideFilter) : HomeUiEvent()
    data class OpenRide(val rideId: String) : HomeUiEvent()
}

@HiltViewModel
class EndUserHomeViewModel @Inject constructor(
    private val getHomeRidesUseCase: GetHomeRidesUseCase
) : ViewModel() {
    val uiState: StateFlow<HomeUiState>
    fun onEvent(event: HomeUiEvent)
}
```

---

## Navigation

All navigation is handled by a single `NavHost` in `MainActivity`. Deep links from push notifications are resolved in `MainActivity.onNewIntent()` and passed to the nav controller.

| Push Event | Deep Link | Destination |
|------------|-----------|-------------|
| `offer.received` | `sharewind://offers/{offerId}` | Offer popup overlay |
| `driver.approved` | `sharewind://profile` | Profile screen |
| `ride.status_changed` | `sharewind://rides/{rideId}` | Ride detail |

---

## Theme

Material 3 theming with a custom ShareWind color scheme:

| Token | Light | Dark |
|-------|-------|------|
| `primary` | Brand blue | Brand blue (lighter) |
| `surface` | White | Dark grey |
| `onPrimary` | White | Black |

Defined in `ui/theme/Theme.kt`, `Color.kt`, `Type.kt`.

---

## Screenshots

| Home | Offer | Ride Detail | Notifications | Profile |
|:-:|:-:|:-:|:-:|:-:|
| ![](https://raw.githubusercontent.com/lyhuynh323/ShareWind/main/docs/images/app/mobile_eu_home_1772595819746.png) | ![](https://raw.githubusercontent.com/lyhuynh323/ShareWind/main/docs/images/app/mobile_offer_popup_1772595834252.png) | ![](https://raw.githubusercontent.com/lyhuynh323/ShareWind/main/docs/images/app/mobile_ride_detail_confirmed_1772595853479.png) | ![](https://raw.githubusercontent.com/lyhuynh323/ShareWind/main/docs/images/app/mobile_notifications_list_1772596393967.png) | ![](https://raw.githubusercontent.com/lyhuynh323/ShareWind/main/docs/images/app/mobile_user_profile_1772596220521.png) |
