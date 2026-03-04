# ShareWind — Android

> Native Android app for the ShareWind peer-to-peer ride-sharing platform.

Built with **Kotlin** and **Jetpack Compose**, this app serves both **End-users** (passengers) and **Drivers** — with support for switching between roles on a single account.

---

## Related Repositories

| Repo | Stack |
|------|-------|
| [ShareWind](https://github.com/lyhuynh323/ShareWind) | Documentation & Design |
| [ShareWind-backend](https://github.com/thanhnd/ShareWind-backend) | Go — REST API Server |
| [ShareWind-sysadmin](https://github.com/thanhnd/ShareWind-sysadmin) | Next.js — Back Office Web |
| [ShareWind-iOS](https://github.com/thanhnd/ShareWind-iOS) | Swift / SwiftUI — iOS App |

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Kotlin |
| UI Framework | Jetpack Compose |
| Minimum SDK | Android 9 (API 28) |
| Target SDK | Android 15 (API 35) |
| Architecture | MVVM + Clean Architecture |
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
- Register / login via phone number or email
- OTP verification (SMS)
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
- Account switches to **Driver Mode** once a Sys-admin approves the application
- Toggle between End-user and Driver views within the same app session

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
│   │   │   │   ├── data/
│   │   │   │   │   ├── remote/         # Retrofit API interfaces & DTOs
│   │   │   │   │   ├── local/          # DataStore, local cache
│   │   │   │   │   └── repository/     # Repository implementations
│   │   │   │   ├── domain/
│   │   │   │   │   ├── model/          # Domain models
│   │   │   │   │   └── usecase/        # Business use cases
│   │   │   │   ├── ui/
│   │   │   │   │   ├── auth/           # Login, Register, OTP screens
│   │   │   │   │   ├── home/           # Ride feed (EU & Driver)
│   │   │   │   │   ├── ride/           # Create request / Create ride
│   │   │   │   │   ├── offer/          # Offer popup, accept/decline
│   │   │   │   │   ├── vehicle/        # Vehicle CRUD
│   │   │   │   │   ├── notifications/  # Notification list & detail
│   │   │   │   │   └── profile/        # Account settings, role toggle
│   │   │   │   ├── navigation/         # NavGraph, routes
│   │   │   │   └── MainActivity.kt
│   │   │   ├── res/                    # Resources (strings, themes, icons)
│   │   │   └── AndroidManifest.xml
│   │   └── test/                       # Unit tests
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

FCM is used for all push notifications. The app registers an FCM token on first launch and sends it to the backend. Supported notification types:

| Event | Recipient |
|-------|-----------|
| New Driver offer received | End-user |
| Offer accepted | Driver |
| Offer expired (10-min timeout) | Driver |
| Driver account approved by admin | Driver |
| Ride status changed | Both |

---

## Requirements

- Android 9.0+ (API 28)
- RAM ≥ 3 GB (recommended)
- Internet connection (4G / WiFi)

---

## License

Private — All rights reserved.
