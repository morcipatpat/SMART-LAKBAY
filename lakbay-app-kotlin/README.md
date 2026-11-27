# 🌋 Smart Lakbay

> **Your AI-powered travel planning, real-time GPS navigation, and traveler community companion for Batangas and beyond.**

---

## 📱 About Smart Lakbay
**Smart Lakbay** is a premium, feature-rich Android mobile application built with **Kotlin** and **Jetpack Compose**. It provides intelligent trip planning, real-time GPS location tracking, interactive maps, traveler reviews with photo/video attachments, multi-language localization (10 languages), and a persistent local user database.

---

## ✨ Key Features & Architecture

### 1. 🤖 Lakbay AI Assistant
* **Intelligent Conversational Engine (`LakbayBrain.kt`)**: Built-in offline-first AI guide grounding answers in real local data (Batangas spots, prices, distances, and user budgets).
* **Half-Size Bottom Sheet Overlay (`ChatOverlay`)**: Accessible via the floating chat button with a semi-transparent backdrop and top-right close button (`✕`).

### 2. 🗺️ Live GPS Navigation & Maps (`MapScreen.kt`)
* **Real-Time GPS Tracking**: Powered by Google Play Services `FusedLocationProviderClient`.
* **Nearby Establishments Feed**: Browse places like Marian Orchard, LIMA Estate, S&R Lipa, and SM Lipa with live distances (`km away`), opening status (`OPEN NOW`), and instant **"Get Directions"** buttons.

### 3. 🎒 Trips & Itinerary Management (`TripsScreen.kt` & `ItineraryScreen.kt`)
* **Upcoming Trips**: Expandable full itinerary cards with stop arrival times, prices, and interactive order reordering (`▲` / `▼`).
* **Side-by-Side Actions**: Quick access to *"Edit Itinerary"* and *"Date"*, plus a top-right `✕` delete icon with a safety confirmation popup.
* **History Archives & Redo Trip**: Completed trips are archived read-only with a **"🔄 Redo Trip & Move to Upcoming"** feature.

### 4. ✍️ Traveler Reviews & Community (`ReviewsScreen.kt`)
* **Public Feed**: Accessible to all users (guests and registered users alike).
* **Media Attachments**: Attach photos and videos directly from your device gallery to your reviews.
* **Comments & Replies**: Send and read comments from fellow travelers in real time.

### 5. 💾 Persistent User Folder Database (`UserDatabaseManager.kt`)
* Every user account automatically receives a dedicated folder under `user/` in the app's internal files directory (`user/<email>/profile.json`).
* All user changes, custom names, phone numbers, profile photos, language choices, and settings are persistently stored and automatically restored upon logging back in.

### 6. 🌐 10 App Languages (`AppLanguage.kt`)
* Full localization support for **English (US), Filipino (Tagalog), Chinese (CN), Spanish (ES), Japanese (JP), Korean (KR), French (FR), German (DE), Arabic (SA), and Italian (IT)** with an expandable arrow toggle.

---

## 🚀 How to Test & See If Your App Works

You can test and verify Smart Lakbay using two methods:

### Method A: Run in Android Studio (Emulator or Physical Device)
1. Open **Android Studio**.
2. Select **File > Open** and choose your project folder: `D:\lakbay-app_kotlin`.
3. In the Gradle JDK dropdown (File > Settings > Build Tools > Gradle), select **`jbr-21`** (Java 21).
4. Click **Sync Now** (`🐘`).
5. Click the green **Run** (`▶`) button to launch the app on an Android Emulator or your connected USB Android phone.

### Method B: Install the Built APK Directly
* Locate the compiled debug APK in your project folder:
  `D:\lakbay-app_kotlin\app\build\outputs\apk\debug\app-debug.apk`
* Transfer or share this APK file to any Android device and tap it to install and test instantly.

---

## 📦 Project Structure
```text
D:\lakbay-app_kotlin\
├── app\
│   ├── src\main\
│   │   ├── AndroidManifest.xml
│   │   ├── java\com\smartlakbay\app\
│   │   │   ├── MainActivity.kt
│   │   │   ├── engine\LakbayBrain.kt
│   │   │   ├── models\
│   │   │   │   ├── AppLanguage.kt
│   │   │   │   ├── CommunityStore.kt
│   │   │   │   ├── DestinationImages.kt
│   │   │   │   ├── Models.kt
│   │   │   │   ├── TripStore.kt
│   │   │   │   └── UserDatabaseManager.kt
│   │   │   └── ui\
│   │   │       ├── AuthGateOverlay.kt
│   │   │       ├── ChatScreen.kt
│   │   │       ├── DestinationDetailScreen.kt
│   │   │       ├── IntroScreen.kt
│   │   │       ├── ItineraryScreen.kt
│   │   │       ├── LoginCard.kt
│   │   │       ├── LoginScreen.kt
│   │   │       ├── MainScreen.kt
│   │   │       ├── MapScreen.kt
│   │   │       ├── ProfileScreen.kt
│   │   │       ├── ReviewsScreen.kt
│   │   │       ├── RouteScreen.kt
│   │   │       ├── TripCustomizerScreen.kt
│   │   │       └── TripsScreen.kt
│   │   └── res\
│   └── build.gradle
├── build.gradle
└── README.md
```

---
*Generated with [Claude Code](https://claude.com/claude-code)*
