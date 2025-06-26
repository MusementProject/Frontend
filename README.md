# 📱 Musement Frontend

A mobile client for the Musement concert discovery app, delivering personalized concert feeds, social interactions, and ticket management.

✨ **Key Features:**

* Spotify playlist integration for personalized concert recommendations
* Playlist upload and management: import, view, and manage
* Concert feed based on user preferences and friends’ plans
* Google OAuth2 and JWT login (via backend)
* Discussion threads on each concert event
* Ticket storage and display with media uploads
* Profile management: edit avatar and text info

  &#x20;&#x20;

---

## ⚙️ Prerequisites

Before building and running the app, ensure you have:

* Android Studio Flamingo or later
* JDK 11+ (or as specified in project `build.gradle`)
* An emulator or physical Android device (API 21+)
* Backend URL and credentials (set in `local.properties` or environment)
* Spotify API credentials (`SPOTIFY_CLIENT_ID`, `SPOTIFY_SECRET`)
* Google OAuth2 credentials (`GOOGLE_CLIENT_ID`, `GOOGLE_CLIENT_SECRET`)
* Cloudinary account (for media uploads)

---

## 🚀 Tech Stack & Libraries

* **Language:** Java 11+
* **UI:** Android SDK with XML layouts
* **Networking:** Retrofit
* **Authentication:** Google Sign-In + JWT via backend API
* **Testing:** JUnit, Mockito
