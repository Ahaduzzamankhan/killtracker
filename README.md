# ⚠️ KillTracker — No Longer Maintained

> **Maintenance has ended.** This project is no longer actively developed or supported. It is preserved for reference only.

---

# KillTracker (Fabric Client Mod)

KillTracker is a **client-side Minecraft Java mod** for **1.21 to 1.21.11** that tracks kills, deaths, KDR, and recent kill history locally using SQLite, then syncs queued events to Firebase Firestore when online.

## Features
- Kill count
- Death count
- KDR (`kills / max(1, deaths)`)
- Recent kill history
- Offline-first event storage
- Delayed background sync with retry and backoff

## Tech Stack
- Java 21
- Fabric API
- Gradle Kotlin DSL
- SQLite JDBC
- OkHttp
- Gson
- SLF4J

## Build
```bash
./gradlew build
```

## Project Status
Development has ended. The mod may not receive updates for future Minecraft, Fabric, or Firebase changes. Review all networking and authentication code before reuse.
