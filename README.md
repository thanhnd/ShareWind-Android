# ShareWind — Android

> Native Android app for the ShareWind peer-to-peer ride-sharing platform.

Built with **Kotlin** and **Jetpack Compose**, this app serves both **End-users** (passengers) and **Drivers** — with support for switching between roles on a single account.

---

## Documentation

| Doc | Description |
|-----|-------------|
| [BRD](https://github.com/lyhuynh323/ShareWind/blob/main/docs/BRD.md) | Business requirements |
| [business-workflows](https://github.com/lyhuynh323/ShareWind/blob/main/docs/business-workflows.md) | User flows |
| [ui-mockups](https://github.com/lyhuynh323/ShareWind/blob/main/docs/ui-mockups.md) | Design system, mobile & back-office UI specs |

**Key flows (BRD v1.2):** End-user registers with phone (mandatory) + 6-digit OTP; login via phone + password after verification. Driver registration is a separate flow after login (upload driving license → pending → SA/Mod verify or auto-approve after 3 days).

---

## Related Repositories

| Repo | Stack |
|------|-------|
| [ShareWind](https://github.com/lyhuynh323/ShareWind) | Documentation & Design |
| [ShareWind-backend](https://github.com/thanhnd/ShareWind-backend) | Go — REST API Server |
| [ShareWind-sysadmin](https://github.com/thanhnd/ShareWind-sysadmin) | Next.js — Back Office Web |
| [ShareWind-iOS](https://github.com/thanhnd/ShareWind-iOS) | Swift / SwiftUI — iOS App |

---

## Design System

Aligned with [docs/ui-mockups.md](https://github.com/lyhuynh323/ShareWind/blob/main/docs/ui-mockups.md). Style: **Modern Clean + light glassmorphism**.

### Color Palette

| Token | Hex | Usage |
|-------|-----|-------|
| Primary | `#00B4D8` | App header, buttons, active states |
| Primary Hover | `#0096C7` | Pressed/hover states |
| Primary Light | `#E0FBFC` | Tint backgrounds, active filter pills |
| Secondary | `#0077B6` | Emphasized text, price display |
| Accent | `#FFB703` | Rating stars, pending/highlight chips |
| Success | `#2A9D8F` | Confirmed, completed, accept button |
| Warning | `#E9C46A` | Expiring soon, pending |
| Error | `#E76F51` | Cancelled, delete, lock |
| Neutral 900 | `#212529` | Headings, primary text |
| Neutral 600 | `#6C757D` | Body/secondary text |
| Neutral 300 | `#DEE2E6` | Borders, dividers |
| Neutral 100 | `#F8F9FA` | Card backgrounds, search bar |

### Typography

| Style | Size | Weight | Usage |
|-------|------|--------|-------|
| H1 | 24sp | Bold | Screen titles, banners |
| H2 | 18sp | SemiBold | Section headers |
| Body | 14sp | Regular | Default text |
| Caption | 12sp | Regular | Timestamps, notes |
| Button | 16sp | SemiBold | Button labels |

### Components

- **Cards:** Border radius `16dp`, soft shadow `0 4 12 rgba(0,0,0,0.05)`
- **Buttons:** Border radius `12dp` (or pill `99dp` for filter chips)
- **Bottom Nav:** Glassmorphism blur, active tab = Primary fill + caption
- **Icons:** Material Symbols Rounded, stroke 1.5dp

### Key UI Patterns

- **Role Toggle:** Dropdown/pill in header `[ 👤 Passenger ▾ ]` → "Chuyển sang Driver Mode" (haptic feedback)
- **Offer Popup:** Bottom sheet with 10-min countdown bar, Accept (Success) / Reject (Neutral) buttons, haptic + sound
- **Ride Detail (Confirmed):** Hero section with avatar, phone reveal, prominent "Gọi Ngay" button (Success green)
- **FAB:** 56dp, Primary gradient, "+" icon, shadow `0 8 16 rgba(0,180,216,0.3)`

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Kotlin |
| UI Framework | Jetpack Compose + Material 3 |
| Minimum SDK | Android 9 (API 28) |
| Target SDK | Android 15 (API 35) |
| Architecture | Clean Architecture + MVVM |
| DI | Hilt |
| Networking | Retrofit + OkHttp |
| Async | Kotlin Coroutines + Flow |
| Navigation | Compose Navigation |
| Push Notifications | Firebase Cloud Messaging (FCM) |
| Image Loading | Coil |
| Local Storage | DataStore (tokens, preferences) |
| Build System | Gradle (Kotlin DSL) |

---

## Features

### Authentication
- Register / login via **phone number** (mandatory after OTP verification)
- OTP verification (6-digit SMS)
- Secure token storage with Android Keystore / EncryptedDataStore
- Biometric authentication (fingerprint / face unlock)

### End-user Mode
- **Home feed** — browse available driver rides with multi-criteria filters (time, price, route)
- **Create ride request** — set origin, destination, waypoints, price, schedule, and notes
- **Offer flow** — receive driver offers via in-app popup + push notification; 10-minute acceptance window
- **Click-to-call** — driver's phone number revealed and dialer opened only after mutual confirmation
- **Ride history** — view past and upcoming trips
- **Notifications center** — full list of push notifications with read/unread state
- **Subscription info** — view remaining monthly quota (max 120 requests/month on free plan)

### Driver Mode
- **Home feed** — view and filter End-user ride requests (phone and detailed address hidden until confirmed)
- **Create ride** — proactively post a ride (max 30/month on free plan)
- **Send offer** — make an offer to an End-user request; receive push notification on acceptance
- **Click-to-call** — End-user phone revealed after both sides confirm
- **Vehicle management** — add, edit, delete vehicles with photos
- **Ride management** — view and update status of own rides

### Dual Role (Role Toggle)
- Any End-user can apply to become a Driver by submitting their driving license and vehicle info
- Account status is **pending** until a Sys-admin approves — UI must show "waiting for approval" state
- Account switches to **Driver Mode** once approved; toggle between End-user and Driver views within the same app session

---

## Screenshots

| Home (EU) | Offer Popup | Ride Confirmed | Create Request |
|:-:|:-:|:-:|:-:|
| ![EU Home](https://raw.githubusercontent.com/lyhuynh323/ShareWind/main/docs/images/app/mobile_eu_home_1772595819746.png) | ![Offer Popup](https://raw.githubusercontent.com/lyhuynh323/ShareWind/main/docs/images/app/mobile_offer_popup_1772595834252.png) | ![Ride Confirmed](https://raw.githubusercontent.com/lyhuynh323/ShareWind/main/docs/images/app/mobile_ride_detail_confirmed_1772595853479.png) | ![Create Request](https://raw.githubusercontent.com/lyhuynh323/ShareWind/main/docs/images/app/mobile_create_request_1772596203750.png) |

| Notifications | Profile | Auth |
|:-:|:-:|:-:|
| ![Notifications](https://raw.githubusercontent.com/lyhuynh323/ShareWind/main/docs/images/app/mobile_notifications_list_1772596393967.png) | ![Profile](https://raw.githubusercontent.com/lyhuynh323/ShareWind/main/docs/images/app/mobile_user_profile_1772596220521.png) | ![Auth](https://raw.githubusercontent.com/lyhuynh323/ShareWind/main/docs/images/app/auth_login_register_1772596187835.png) |

---

## Project Structure

```
ShareWind-Android/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/sharewind/app/
│   │   │   │   ├── di/                 # Hilt dependency injection modules
│   │   │   │   ├── data/               # Retrofit, DataStore, repository implementations
│   │   │   │   │   ├── remote/         # Retrofit API interfaces & DTOs
│   │   │   │   │   ├── local/          # DataStore, local cache
│   │   │   │   │   └── repository/     # Repository implementations
│   │   │   │   ├── domain/             # Pure Kotlin use cases, models, repository interfaces
│   │   │   │   │   ├── model/          # Domain models
│   │   │   │   │   └── usecase/        # Business use cases
│   │   │   │   ├── ui/                 # Jetpack Compose screens + ViewModels
│   │   │   │   │   ├── auth/           # Login, Register, OTP screens
│   │   │   │   │   ├── home/           # Ride feed (EU & Driver)
│   │   │   │   │   ├── ride/           # Create request / Create ride
│   │   │   │   │   ├── offer/          # Offer popup, accept/decline
│   │   │   │   │   ├── vehicle/        # Vehicle CRUD
│   │   │   │   │   ├── notifications/  # Notification list & detail
│   │   │   │   │   └── profile/        # Account settings, role toggle
│   │   │   │   ├── navigation/         # NavGraph, routes
│   │   │   │   ├── theme/              # Color, Type, Shape, Theme (design system)
│   │   │   │   └── MainActivity.kt
│   │   │   ├── res/                    # Resources (strings, themes, icons)
│   │   │   └── AndroidManifest.xml
│   │   ├── test/                       # Unit tests
│   │   └── androidTest/                # Instrumented tests
│   ├── build.gradle.kts
│   └── google-services.json            # Firebase config (not committed)
├── gradle/
│   └── libs.versions.toml              # Version catalog
├── build.gradle.kts
├── settings.gradle.kts
└── local.properties                    # SDK path (not committed)
```

---

## Getting Started

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or later
- Android SDK 28+
- JDK 17+
- A running instance of [ShareWind-backend](https://github.com/thanhnd/ShareWind-backend)

### Firebase Setup

1. Create a Firebase project at [console.firebase.google.com](https://console.firebase.google.com)
2. Add an Android app with package name `com.sharewind.app`
3. Download `google-services.json` and place it in `app/`
4. Enable **Cloud Messaging (FCM)** in the Firebase console

### Configuration

Create (or update) `app/src/main/res/values/config.xml`:

```xml
<resources>
    <string name="api_base_url">http://10.0.2.2:8080/api/v1</string>
</resources>
```

> Use `10.0.2.2` to reach `localhost` from the Android emulator.

### Build & Run

1. Open the project in Android Studio
2. Let Gradle sync complete
3. Select a device or emulator (API 28+)
4. Press **Run** (`Shift + F10`)

---

## Push Notifications

FCM is used for all push notifications. The app registers an FCM token on first launch and sends it to the backend. **FCM token must be re-registered on every login and on `onNewToken`.** Supported notification types:

| Event | Recipient |
|-------|-----------|
| New Driver offer received | End-user |
| Offer accepted | Driver |
| Offer expired (10-min timeout) | Driver |
| Driver account approved by admin | Driver |
| Driver account rejected by admin | Driver |
| Ride status changed | Both |

---

## Requirements

- Android 9.0+ (API 28)
- RAM ≥ 3 GB (recommended)
- Internet connection (4G / WiFi)

---

## License

Private — All rights reserved.
