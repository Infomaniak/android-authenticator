# Copilot Coding Agent Onboarding — android-authenticator

> **Read `AGENTS.md` first** for architecture and conventions. This file covers build, CI, and validation.

## Overview
Infomaniak Authenticator — TOTP-based two-factor auth app. Kotlin + Jetpack Compose, Hilt DI, AndroidX Biometric, AndroidX Room (runtime). Also exports an iOS Swift Package / XCFramework for cross-platform TOTP logic (`multiplatform-lib/`). Two flavors: `standard` and `fdroid`.

## One-Time Environment Setup
```bash
git submodule update --init --recursive   # Core submodule
cp env.example.properties env.properties  # fill sentryAuthToken (dummy value OK locally)
```

## Build & Test (CI: `.github/workflows/android.yml`)
CI runs on non-draft PRs only if non-`.md`/`.github` files changed:
```bash
./gradlew assembleDebug                                         # compile check
./gradlew testStandardDebugUnitTest testFdroidDebugUnitTest sonar --info --stacktrace
```
CI also runs Android Lint via `infomaniak/.github` reusable workflow. Locally:
```bash
./gradlew lint
```

## Project Layout
```
app/                        # Android application
├── src/main/java/com/infomaniak/authenticator/
│   ├── data/               # Room DB, repositories, models
│   ├── di/                 # Hilt modules
│   ├── ui/                 # Compose screens (TOTP codes, accounts, settings)
│   └── worker/             # WorkManager workers
multiplatform-lib/          # KMP module — TOTP logic shared with iOS
Core/                       # Git submodule — Infomaniak shared library
Package.swift               # iOS Swift Package entry point
buildRelease / buildXCFramework  # Shell scripts to produce iOS XCFramework
gradle/libs.versions.toml
```

## Key Rules
- Firebase is `standardImplementation` only — fdroid builds must compile without it.
- Room is a **runtime** dependency here (unlike euria where it is kapt-only).
- After modifying KMP (`multiplatform-lib/`) public API, verify the iOS build still compiles.
- All user-visible strings in `res/values/strings.xml`.
- When adding/removing a runtime dependency, update `LICENSES.md` at the repo root.
