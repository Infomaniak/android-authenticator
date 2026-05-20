# AGENTS.md - Authenticator Multiplatform Library

> For the Android app norms, see `app/AGENTS.md`. For Core library norms, see `Core/AGENTS.md`. For the composite build overview,
> see the root `AGENTS.md`.

## Module Summary

`:multiplatform-lib` is the Kotlin Multiplatform module that holds the shared business logic of the Infomaniak Authenticator. It
is consumed:

- by the Android app via `implementation(project(":multiplatform-lib"))` (see `app/build.gradle.kts`);
- by the iOS / macOS Authenticator as a static XCFramework named `CoreAuthenticator`, distributed through the root
  `Package.swift`.

It owns the OTP engine (TOTP/HOTP), the API client, the local Room database, the account/2FA repositories, the WebAuthn / passkey
logic and the migration models exchanged between platforms.

## High-Level Tech Stack

- **Kotlin Multiplatform** with targets: `androidLibrary` (namespace `com.infomaniak.auth.multiplatform`), `iosArm64`,
  `iosSimulatorArm64`, `macosArm64`.
- **SKIE** for idiomatic Swift interop (default arguments, sealed classes, suspend functions).
- **Ktor client** for HTTP (engines: OkHttp on Android, Darwin on Apple).
- **kotlinx.serialization** (`json`, `cbor`) - JSON is configured in `internal/network/ApiClientProvider.kt` with
  `coerceInputValues = true`, `ignoreUnknownKeys = true`, and `decodeEnumsCaseInsensitive`.
- **AndroidX Room** (multiplatform Room with `androidx.sqlite.bundled`) - schemas are exported to `multiplatform-lib/schemas`.
- **KotlinCrypto** (`hmac-sha1`, `hmac-sha2`) and `osmerion-kotlin-base32` for OTP computation.
- **Coroutines** (`kotlinx.coroutines.core`, `kotlinx.coroutines.test`).
- **Parcelize** plugin enabled for Android-only `@Parcelize` models.

## Context Map

```
multiplatform-lib/
├── build.gradle.kts            # KMP setup: targets, XCFramework, SKIE, Room
├── schemas/                    # Exported Room schemas (commit when DB schema changes)
└── src/
    ├── commonMain/kotlin/
    │   ├── Account.kt              # Public KMP model for an Authenticator account
    │   ├── AppStatus.kt
    │   ├── AuthenticatorFacade.kt  # Main public entry point for both Android and iOS
    │   ├── CredentialsForMigration.kt
    │   ├── Issue.kt
    │   ├── matomo/                 # Shared Matomo tracking helpers
    │   ├── models/                 # Public DTOs / domain models
    │   │   └── migration/          # Shared* models exposed to iOS (e.g. SharedApiToken, SharedUserProfile)
    │   ├── network/                # Public network types
    │   │   ├── exceptions/
    │   │   └── interfaces/
    │   ├── repository/             # Public repository interfaces
    │   ├── room/                   # Public Room entities / DAOs (DBs are constructed per platform)
    │   │   └── appsettings/
    │   └── internal/               # Everything `internal` (not exported)
    │       ├── KeyManager.kt       # `KeyPairManager()` companion delegates to expect/actual createKeyPairManager()
    │       ├── db/
    │       ├── extensions/
    │       ├── managers/           # e.g. MigrationManager (deletes legacy account + DB after migration)
    │       ├── models/
    │       ├── network/            # ApiClientProvider, request builders
    │       ├── otp/                # TOTP/HOTP code generation
    │       ├── repositories/
    │       ├── requests/
    │       ├── utils/
    │       └── webauthn/
    ├── androidMain/kotlin/
    │   ├── db/                     # Android Room DB builder
    │   └── internal/
    │       ├── KeyPairManagerImpl.android.kt   # Stores keys as files under appCtx.filesDir/passkeys
    │       ├── db/
    │       ├── network/utils/
    │       ├── otp/
    │       ├── room/
    │       │   └── legacy/         # Legacy DB used by MigrationManager
    │       └── utils/
    ├── appleMain/kotlin/
    │   ├── db/                     # Apple Room DB builder
    │   └── internal/
    │       ├── KeyGen.apple.kt     # Keychain-backed key generation (tags: "$userId-$keyId" / "$userId-$keyId.pub")
    │       ├── KeyPairManagerImpl.apple.kt
    │       ├── db/
    │       ├── extensions/
    │       ├── network/utils/
    │       ├── otp/
    │       └── utils/
    ├── iosMain/kotlin/internal/utils/
    ├── macosMain/kotlin/internal/utils/
    ├── commonTest/kotlin/internal/         # Shared tests (kotlin.test + ktor-client-mock)
    ├── androidHostTest/kotlin/internal/    # JVM unit tests for android-specific code
    ├── androidDeviceTest/kotlin/internal/  # Espresso / AndroidJUnit tests (require a device)
    └── appleTest/kotlin/internal/          # Tests for Apple targets
```

## Local Norms

### Architecture & Design

- **Public surface**: anything not marked `internal` is exported to the Android app and to Swift (via the `CoreAuthenticator`
  XCFramework). Be deliberate about visibility - default to `internal`.
- **Entry point**: `AuthenticatorFacade.kt` is the main public facade. Prefer adding/extending facade methods over exposing
  internal classes.
- **expect/actual**: Cross-platform abstractions use either `expect`/`actual` declarations or a common abstract class with a
  per-platform `createX()` factory (see `internal/KeyManager.kt` - `KeyPairManager()` companion `invoke` delegates to an
  expect/actual `createKeyPairManager()`).
- **Internal package**: All non-API code lives under `internal/` packages and is marked `internal`. Do not move internal types
  into the public packages just to share them across modules - use a typealias or a thin public wrapper instead.
- **iOS naming**: Models exported to iOS that may collide with Swift built-ins use the `Shared*` prefix (e.g. `SharedApiToken`,
  `SharedUserProfile`, `SharedSecurity`). Follow this convention when adding new migration / interop models.
- **Migration**: `MigrationManager` is responsible for moving data from legacy storage. After a successful migration it deletes
  the legacy account and deletes the legacy DB when no legacy accounts remain - preserve this invariant when modifying it.
- **API client**: build all HTTP clients through `internal/network/ApiClientProvider.kt`. Keep its
  `Json { coerceInputValues = true; ignoreUnknownKeys = true; decodeEnumsCaseInsensitive }` configuration intact - the API
  tolerates unknown fields and case differences by design.
- **Key storage**:
    - Android: files under `appCtx.filesDir/passkeys` named `"$userId-$keyId-(public|private).key"`.
    - Apple Keychain tags: `"$userId-$keyId"` (private) and `"$userId-$keyId.pub"` (public).
      Maintain these conventions when adding key-management code; do not rename existing entries (existing users would lose their
      keys).
- **Room schemas**: When you change a Room entity, the generated schema under `multiplatform-lib/schemas/` must be committed
  alongside a migration.

### Commands (run from repo root)

```bash
# Initialize Core submodule (required for any Gradle command)
git submodule update --init --recursive

# Assemble the Android variant of the library
./gradlew :multiplatform-lib:assemble

# Compile all KMP targets
./gradlew :multiplatform-lib:build

# Common (JVM) tests
./gradlew :multiplatform-lib:commonTest
./gradlew :multiplatform-lib:androidHostTest

# All tests (host + simulator)
./gradlew :multiplatform-lib:allTests

# Apple tests (require macOS host)
./gradlew :multiplatform-lib:iosSimulatorArm64Test
./gradlew :multiplatform-lib:macosArm64Test

# Build the iOS / macOS XCFramework (CoreAuthenticator.xcframework)
./buildXCFramework
```

### Code Style

Same general Kotlin rules as the app (see `app/AGENTS.md`):

- 130-char line limit (exceptions: single-line comments, imports, hardcoded strings).
- Max 1 consecutive blank line; 1 blank line after early-return blocks.
- GPLv3 copyright header in every file. No blank line between the closing `*/` and `package`.
- Official Kotlin code style (`kotlin.code.style=official`).
- Trivial control flow: one-liner. Non-trivial: braces + newlines.

KMP-specific rules:

- Use `expect`/`actual` for platform APIs; keep the `expect` signature minimal and document any platform constraints.
- Prefer `kotlinx.coroutines.flow.Flow` / `suspend` functions in the public API - SKIE turns them into idiomatic Swift
  `AsyncSequence` / `async` functions.
- Avoid Java-only APIs in `commonMain` / `appleMain`. Use `kotlinx.io`, `okio`, or `kotlinx.datetime` instead.
- Keep `androidMain` dependencies behind `expect`/`actual` - do not add `androidx.*` imports to `commonMain`.
- For Apple-only types in public APIs, mark them with `@Throws(...)` where Swift needs to handle errors.

### Testing

- **commonTest**: pure-Kotlin tests using `kotlin.test` and `ktor-client-mock` for HTTP. Add new business-logic tests here
  whenever possible so they run on every target.
- **androidHostTest**: JVM unit tests for code that needs Android-host-side resources (no device required).
- **androidDeviceTest**: instrumented tests (`androidx.test.junit`, `espresso-core`) - require a device/emulator.
- **appleTest**: tests for the Apple actuals.

### Public API stability

- The Android app and the iOS / macOS app consume this module as a binary contract. Treat any change to non-`internal`
  declarations as a breaking change candidate: prefer adding to the API over modifying it.
- When renaming a public class/function that is consumed by iOS, also check `Package.swift` and the Swift side for impact (the
  framework name `CoreAuthenticator` and `bundleId` `com.infomaniak.multiplatform-authenticator.CoreAuthenticator` must stay in
  sync with the iOS project).

## Learned Preferences

Add KMP-specific corrections here as they occur.

- Prefer `internal` visibility by default; only widen to `public` when the symbol is part of the cross-platform API surface.
- Use the `Shared*` prefix for migration / interop models exposed to Swift to avoid name collisions.

## Self-correction

- **Stale Map**: Update when you encounter new source sets, packages, or expect/actual splits not listed.
- **Schema drift**: If `./gradlew :multiplatform-lib:build` regenerates files under `schemas/`, commit them as part of the same
  change.
- **Reference Core / app**: When editing code that is bridged into the Android app, cross-check `app/AGENTS.md`; when editing Core
  imports, check `Core/AGENTS.md`.
