# Copilot Coding Agent Onboarding — android-authenticator

> **Read `AGENTS.md` first** for architecture and conventions. This file covers build, CI, and validation.

## Overview
Infomaniak Authenticator — TOTP-based two-factor auth app. Kotlin + Jetpack Compose, Hilt DI, Room (via `multiplatform-lib/`), and app-lock / device credentials via Infomaniak Core (`com.infomaniak.core.applock`). Also exports an iOS Swift Package / XCFramework for cross-platform logic (`multiplatform-lib/`). Two flavors: `standard` and `fdroid`.

## One-Time Environment Setup
```bash
git submodule update --init --recursive   # Core submodule
cp env.example.properties env.properties  # only required for Sentry release tasks; debug builds work without it
```

## Build & Test (CI: `.github/workflows/android.yml`)
CI runs on non-draft PRs only if relevant files changed (excluding `.md` and `.github/`, except `.github/workflows/android.yml`):
```bash
./gradlew assembleDebug
./gradlew testStandardDebugUnitTest testFdroidDebugUnitTest sonar --info --stacktrace
# Note: `sonar` requires `SONAR_TOKEN` (provided as a CI secret). Omit `sonar` locally if the token isn't available.
./gradlew lint   # via infomaniak/.github reusable workflow
```

## Project Layout
```
app/src/main/kotlin/com/infomaniak/auth/
├── data/               # Room DB, repositories, models
├── di/                 # Hilt modules
├── service/            # Android services + WorkManager workers
└── ui/                 # Compose screens (TOTP codes, accounts, settings)
multiplatform-lib/      # KMP module — TOTP logic shared with iOS
Core/                   # Git submodule — Infomaniak shared library
```

## PR Review Instructions

- Ensure strings are localized via `strings.xml` resources.
- Ensure UI is written in Jetpack Compose using Material3 components.
- `standard` flavor only: Firebase, Google services — fdroid builds must compile without them.
- After modifying the public API of `multiplatform-lib/`, verify the iOS XCFramework build still compiles (`./buildXCFramework`).
- When adding/removing a runtime dependency, update `LICENSES.md` at the repo root.
