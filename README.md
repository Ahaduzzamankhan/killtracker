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

## Project Structure
```
src/main/java/tracker/
  KillTracker.java
  adapters/
  config/
  database/
  events/
  firebase/
  models/
  parser/
  services/
  sync/
  utils/
```

## Build
```bash
./gradlew build
```

## Local Run
```bash
./gradlew runClient
```

## Configuration
Config file: `config/killtracker.json`

Options:
- `syncIntervalSeconds`
- `firebaseEndpoint`
- `firebaseProjectId`
- `authToken`
- `parserRules`
- `debugLogging`
- `offlineCacheSize`
- `databasePath`

## Firebase Setup
1. Create a Firebase project.
2. Enable Firestore in Native mode.
3. Use a secure auth flow to provide a short-lived user token to the client.
4. Set `firebaseProjectId` and `authToken` in `killtracker.json`.

### Recommended Firestore Collections
- `players/`
- `kills/`

### Example Firestore Rules (Starter)
```txt
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /players/{playerId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null && request.auth.uid == playerId;
    }
    match /kills/{killId} {
      allow read: if request.auth != null;
      allow create: if request.auth != null;
      allow update, delete: if false;
    }
  }
}
```

## Database Schema
`player_stats`
- uuid
- username
- kills
- deaths
- kdr
- updated_at

`kill_history`
- id
- killer
- victim
- server
- timestamp
- synced

`pending_sync_queue`
- id
- payload
- type
- created_at
- synced

## Next.js / React Compatibility
The sync payload is JSON-based and queue-driven, making it straightforward to ingest in Next.js API routes and React/TypeScript dashboards.

## Production Deployment Notes
- Never embed Firebase admin credentials in the client.
- Use token issuance from your trusted backend.
- Keep Firestore rules strict and identity-scoped.
- Rotate auth tokens and enforce expiration.

## Troubleshooting
- No sync occurring: verify network and `authToken`.
- No events parsed: adjust `parserRules` for your server message format.
- DB issues: ensure write permissions for `config/killtracker.db`.

## Security
- No admin secrets stored in mod.
- No cheat, packet bypass, or automation features included.
