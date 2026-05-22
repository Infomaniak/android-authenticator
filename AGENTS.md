# AGENTS.md - Infomaniak Authenticator (Top Level)

> Navigation Guide: This file describes the composite structure. For Android app norms, see `app/AGENTS.md`. For the Kotlin
> Multiplatform library norms, see `multiplatform-lib/AGENTS.md`. For Core library norms, see `Core/AGENTS.md`.

## Repository Structure

This is a composite Gradle build that ships the Infomaniak Authenticator Android app and a Kotlin Multiplatform library shared
with the iOS/macOS Authenticator (distributed as an XCFramework + Swift Package).

```
android-authenticator/
├── Core/                    # Git submodule - shared Infomaniak Android library (see Core/AGENTS.md)
│   └── build-logic/         # Composite build - Gradle convention plugins (included via pluginManagement)
├── app/                     # Android Authenticator application (see app/AGENTS.md)
│   ├── src/main/kotlin/...  # App source code (com.infomaniak.auth)
│   ├── src/main/res/        # Android resources
│   └── src/standard/        # `standard` flavor sources (Google Play Services)
├── multiplatform-lib/       # Kotlin Multiplatform shared library (see multiplatform-lib/AGENTS.md)
│   ├── src/commonMain/      # Shared business logic (OTP, network, repositories, migration models, Room)
│   ├── src/androidMain/     # Android-specific actual implementations
│   ├── src/appleMain/       # iOS + macOS actual implementations
│   ├── src/iosMain/         # iOS-only actual implementations
│   └── src/macosMain/       # macOS-only actual implementations
├── Package.swift            # Swift Package descriptor exposing the XCFramework to iOS/macOS
├── buildXCFramework         # Helper script to assemble the `CoreAuthenticator` XCFramework
├── buildRelease             # Helper script for release builds
├── env.example.properties   # Sample env.properties (sentryAuthToken)
└── AGENTS.md                # This file (top-level overview)
```

## Quick Summary

| Component         | Location             | AGENTS.md                     | Purpose                                                                      |
|-------------------|----------------------|-------------------------------|------------------------------------------------------------------------------|
| Core              | `Core/`              | `Core/AGENTS.md`              | Reusable library shared by all Infomaniak Android apps (Git submodule)       |
| App               | `app/`               | `app/AGENTS.md`               | Android Authenticator app-specific code and norms                            |
| Multiplatform Lib | `multiplatform-lib/` | `multiplatform-lib/AGENTS.md` | Shared Kotlin Multiplatform code (Android + iOS/macOS) - OTP, API, migration |
| Root              | `./`                 | `AGENTS.md`                   | This file - composite build overview                                         |

## Composite Build Explained

- **`Core` is a Git submodule**: Changes in `Core/` are tracked separately and shared with other Infomaniak apps. Always run
  `git submodule update --init --recursive` after cloning, otherwise the Gradle build will fail to resolve
  `com.infomaniak.core.composite`.
- **`Core/build-logic` is included as a composite build** via `pluginManagement { includeBuild("Core/build-logic") }` in
  `settings.gradle.kts` - it provides Gradle convention plugins required to build all modules (e.g.
  `com.infomaniak.core.composite`, `com.infomaniak.core.compose.lint`).
- **Version catalogs**:
    - `libs` - project-local catalog (`gradle/libs.versions.toml`)
    - `core` - catalog provided by Core (`Core/gradle/core.versions.toml`), registered in `settings.gradle.kts`
- **Immediate resolution**: The app and the multiplatform module use `core.infomaniak.core.*` aliases (resolved from the Core
  submodule sources) instead of remote Maven artifacts.

## Top-Level Configuration

- `androidCompileSdk`, `appTargetSdk`, `androidMinSdk`, `javaVersion` are set in the root `build.gradle.kts` (
  `buildscript { extra { ... } }`) and consumed by all submodules via `rootProject.extra`.
- Kotlin code style is enforced repository-wide via `kotlin.code.style=official` in `gradle.properties`.
- SonarQube configuration lives at the root; project key is `Infomaniak_android-authenticator`.

## Commands (run from repo root)

```bash
# Initialize Core submodule (required for any Gradle command)
git submodule update --init --recursive

# Build the Android app (debug, standard flavor)
./gradlew :app:assembleStandardDebug

# Build the Android app (release, requires env.properties with sentryAuthToken)
./gradlew :app:assembleStandardRelease

# Lint and unit tests for the Android app
./gradlew :app:lint
./gradlew :app:testStandardDebugUnitTest

# Build the multiplatform library (Android)
./gradlew :multiplatform-lib:assemble

# Build the iOS/macOS XCFramework (CoreAuthenticator.xcframework)
./buildXCFramework

# Common multiplatform tests
./gradlew :multiplatform-lib:allTests

# Clean
./gradlew clean
```

## Product Flavors (app module)

| Flavor   | Description                                             | Source set / Dependencies |
|----------|---------------------------------------------------------|---------------------------|
| standard | Full features, Google Play Services, push notifications | `standardImplementation`  |
| fdroid   | FOSS variant, no proprietary dependencies               | `fdroidImplementation`    |

## Environment

- **`env.properties`**: required for release builds. Must define `sentryAuthToken`. See `env.example.properties`.
- **`local.properties`**: local SDK paths (auto-generated). Never commit.
- **`google-services.json`**: committed under `app/` for the `standard` flavor.

## Self-correction

- **Stale Map**: Update when you encounter new top-level files/folders not listed here.
- **New Norms**: Add user corrections to the "Learned Preferences" section of the most specific `AGENTS.md` (root vs. `app/` vs.
  `multiplatform-lib/`).
- **Reference Core**: When editing code that imports from `com.infomaniak.core.*`, check `Core/AGENTS.md` for Core-specific norms.
