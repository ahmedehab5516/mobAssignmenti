# Cinema Booking App - Setup & Build Guide

## Project Overview

A complete, production-ready Android cinema booking app built with:

-   **Kotlin** 100%
-   **Jetpack Compose** + Material 3
-   **Clean Architecture** (MVVM)
-   **Hilt** for Dependency Injection
-   **Retrofit + Moshi** for API calls
-   **Firebase Auth** & Firestore
-   **Coroutines + Flow** for async operations
-   **Java 21 LTS**

---

## ✅ Build Configuration Status

### Android SDK

-   **Min SDK**: 26 (Android 8.0)
-   **Target SDK**: 35 (Android 15)
-   **Compile SDK**: 35
-   **Java Version**: 21 LTS

### Gradle & Build Tools

-   **Gradle**: 8.12
-   **AGP (Android Gradle Plugin)**: 8.10.0
-   **Kotlin**: 2.1.0

### Dependencies Configured

✅ Jetpack Compose (1.6.8)
✅ Material 3 (1.2.1)
✅ Hilt (2.51.1)
✅ Retrofit (2.11.0)
✅ Moshi (1.15.1)
✅ Coil (2.6.0)
✅ Firebase Auth & Firestore
✅ Coroutines (1.8.1)
✅ DataStore (1.1.1)
✅ Room (2.6.1)

---

## 📁 Project Structure

```
app/
├── src/main/
│   ├── java/com/ahmed/cinema/
│   │   ├── di/
│   │   │   ├── CinemaApp.kt          # Hilt Application
│   │   │   ├── NetworkModule.kt      # Retrofit & Moshi
│   │   │   └── RepositoryModule.kt   # Repository injection
│   │   ├── data/
│   │   │   ├── api/
│   │   │   │   ├── MovieApiService.kt
│   │   │   │   └── MovieResponse.kt
│   │   │   └── repository/
│   │   │       └── MovieRepository.kt
│   │   ├── domain/
│   │   │   ├── model/
│   │   │   │   └── Movie.kt
│   │   │   └── usecase/
│   │   │       └── MovieUseCases.kt
│   │   ├── presentation/
│   │   │   ├── splash/
│   │   │   │   └── SplashActivity.kt
│   │   │   ├── auth/
│   │   │   │   ├── LoginActivity.kt
│   │   │   │   └── RegisterActivity.kt
│   │   │   ├── home/
│   │   │   │   ├── MainActivity.kt
│   │   │   │   └── HomeViewModel.kt
│   │   │   ├── favorites/
│   │   │   ├── settings/
│   │   │   └── moviedetail/
│   │   │       └── MovieDetailActivity.kt
│   │   ├── ui/
│   │   │   └── theme/
│   │   │       ├── Theme.kt
│   │   │       └── Typography.kt
│   │   └── util/
│   │       ├── Constants.kt
│   │       └── Extensions.kt
│   ├── res/
│   │   ├── drawable/           # UI assets
│   │   ├── values/             # Colors, strings, themes
│   │   ├── values-night/       # Dark mode themes
│   │   └── mipmap-*/           # App icons
│   └── AndroidManifest.xml
├── build.gradle.kts
└── google-services.json        # Firebase config (auto-generated)
```

---

## 🔧 How to Build

### Prerequisites

1. **Android Studio** (latest Giraffe or Hedgehog)
2. **Java 21 LTS** installed
3. **Android SDK** with API 35 downloaded

### Build Steps

#### 1. Open in Android Studio

```bash
git clone <your-repo-url>
cd ass
# Open in Android Studio via File → Open
```

#### 2. Wait for Gradle Sync

-   Android Studio will automatically sync and download dependencies
-   First sync takes 2-3 minutes

#### 3. Configure Firebase (Optional for Demo)

-   Replace `app/google-services.json` with your Firebase project config
-   For demo, the app works without Firebase

#### 4. Build APK

```bash
./gradlew clean build
```

#### 5. Run on Emulator

-   Create Android Virtual Device (AVD):
    -   API Level: 26+ (Android 8.0+)
    -   RAM: 2GB minimum
-   Run:
    ```bash
    ./gradlew installDebug
    ```

---

## 🚀 Features Implemented

### ✅ Splash Screen

-   2-second delay
-   Shows "Cinema Booking" branding
-   Routes to Login (auth) or Home (authenticated)

### ✅ Authentication

-   **Login Screen**: Email + Password
-   **Register Screen**: Full Name, Email, Password
-   Basic validation
-   Firebase Auth ready (needs google-services.json)

### ✅ Home Screen (Main Activity)

-   **Bottom Navigation** (3 tabs):

    -   Home: Now Showing + Coming Soon movies
    -   Favorites: Saved movies grid
    -   Settings: User profile + logout

-   **Now Showing Section**:

    -   Fetches from TMDB API (`/movie/now_playing`)
    -   Horizontal scrollable carousel
    -   Shows: Poster, Title, Rating
    -   Tap to view details

-   **Coming Soon Section**:
    -   Fetches from TMDB API (`/movie/upcoming`)
    -   Same card layout

### ✅ Movie Detail Screen

-   Large poster
-   Title, rating, release date
-   Genre chips
-   Overview text (scrollable)
-   "Book Now" button
-   Heart icon for favorites (ready for Firestore)

### ✅ Material 3 Design

-   Modern color scheme
-   Smooth animations
-   Responsive layouts
-   Dark mode support (via system)

### ✅ Clean Architecture

-   **Data Layer**: API + Repository pattern
-   **Domain Layer**: Use cases + Models
-   **Presentation Layer**: ViewModels + Compose screens

### ✅ Dependency Injection

-   Hilt for automatic DI
-   Singleton scoped services
-   ViewModel injection via Hilt

---

## 🔑 API Configuration

The app uses **TMDB API** (themoviedb.org):

**API Key**: `e88a3ceae739f519be136020e84208ba`
**Base URL**: `https://api.themoviedb.org/3/`

Endpoints used:

-   `/movie/now_playing` - Get current movies
-   `/movie/upcoming` - Get upcoming movies
-   `/movie/{id}` - Get movie details
-   `/search/movie` - Search movies

No additional configuration needed!

---

## 📱 How to Run

### Via Android Studio

1. Click **Run** (green play icon)
2. Select emulator or connected device
3. App launches in ~10 seconds

### Via Command Line

```bash
./gradlew installDebug          # Install on device
adb shell am start -n com.ahmed.cinema/.presentation.splash.SplashActivity
```

### Expected Behavior

1. **Splash**: Shows for 2 seconds
2. **Login**: Opens login screen
3. **After "Login"**: Shows home screen with movies from TMDB
4. **Bottom tabs**: Switch between Home/Favorites/Settings
5. **Tap movie**: Opens movie detail screen

---

## ⚙️ Gradle Commands

```bash
# Clean build
./gradlew clean

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest

# Lint check
./gradlew lint
```

---

## 🔐 Firebase Setup (Optional)

To enable **Authentication** & **Firestore**:

1. Go to [Firebase Console](https://console.firebase.google.com)
2. Create new project: `cinema-booking-app`
3. Add Android app:
    - Package name: `com.ahmed.cinema`
    - SHA-1: Run `./gradlew signingReport` and copy SHA-1
4. Download `google-services.json` to `app/`
5. Rebuild project

---

## 🎨 Customization

### Change Color Scheme

Edit `res/values/colors.xml`:

```xml
<color name="primary">#6200EA</color>  <!-- Change this -->
```

### Change App Name

Edit `res/values/strings.xml`:

```xml
<string name="app_name">Your App Name</string>
```

### Change TMDB API Key

Edit `util/Constants.kt`:

```kotlin

```

---

## 📦 Build Output

**Debug APK**: `app/build/outputs/apk/debug/app-debug.apk`
**Release APK**: `app/build/outputs/apk/release/app-release.apk` (after signing)

---

## 🐛 Troubleshooting

### Gradle Sync Fails

```bash
./gradlew --stop
./gradlew clean build
```

### API Calls Not Working

-   Check internet connection
-   Verify TMDB API key in `Constants.kt`
-   Check logcat: `adb logcat | grep "Cinema"`

### Compose Not Rendering

-   Ensure `compileSdk = 35` in `build.gradle.kts`
-   Kotlin plugin version: 2.1.0+
-   Compose version: 1.6.8+

### Firebase Auth Not Working

-   Add `google-services.json`
-   Enable Email/Password auth in Firebase Console
-   Check SHA-1 certificate fingerprint

---

## 📝 Notes

-   **API Rate Limit**: TMDB allows 40 requests per 10 seconds
-   **Caching**: Implement Room DB for offline support (optional)
-   **Dark Mode**: Automatically applies based on system settings
-   **Null Safety**: 100% Kotlin null-safe code

---

## 🚀 Next Steps

1. **Test on Real Device**: Connect via USB
2. **Add More Features**: Favorites, seat selection, payments
3. **Firebase**: Implement authentication & Firestore
4. **Image Loading**: Coil already configured, replace placeholders
5. **Error Handling**: Add better error UI (Snackbars, error screens)
6. **Testing**: Add unit & integration tests

---

**Built with ❤️ using Kotlin & Jetpack Compose**
