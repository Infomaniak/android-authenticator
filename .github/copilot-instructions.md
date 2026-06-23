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
./gradlew assembleDebug
./gradlew testStandardDebugUnitTest testFdroidDebugUnitTest sonar --info --stacktrace
./gradlew lint   # via infomaniak/.github reusable workflow
```

## Project Layout
```
app/src/main/java/com/infomaniak/authenticator/
├── data/               # Room DB, repositories, models
├── di/                 # Hilt modules
├── ui/                 # Compose screens (TOTP codes, accounts, settings)
└── worker/             # WorkManager workers
multiplatform-lib/      # KMP module — TOTP logic shared with iOS
Core/                   # Git submodule — Infomaniak shared library
```

## PR Review Instructions

- Ensure strings are localized via `strings.xml` resources.
- Ensure UI is written in Jetpack Compose using Material3 components.
- `standard` flavor only: Firebase, Google services — fdroid builds must compile without them.
- After modifying the public API of `multiplatform-lib/`, verify the iOS XCFramework build still compiles (`./buildXCFramework`).
- When adding/removing a runtime dependency, update `LICENSES.md` at the repo root.
