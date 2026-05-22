# AGENTS.md - Infomaniak Authenticator App

> For the shared Kotlin Multiplatform library, see `multiplatform-lib/AGENTS.md`. For the Core library, see `Core/AGENTS.md`. For
> the composite build overview, see the root `AGENTS.md`.

## Project Summary

Infomaniak Authenticator is an Android app built by Infomaniak Network SA that generates TOTP/HOTP codes and manages 2FA for
Infomaniak accounts. The UI is fully Jetpack Compose, persistence uses Room (via the `:multiplatform-lib` module), and
authentication / cross-app login is provided by the Infomaniak Core libraries.

## High-Level Tech Stack

- **Language**: Kotlin - JVM target set via `javaVersion` in the root `build.gradle.kts` (`JavaVersion.VERSION_17`)
- **Platform**: Android - SDK versions (`androidCompileSdk`, `appTargetSdk`, `androidMinSdk`) set in the root `build.gradle.kts`
- **Build System**: Gradle with Kotlin DSL, version catalogs (`libs`, `core`)
- **Architecture**: MVVM with Repository pattern; shared business logic lives in `:multiplatform-lib`
- **Dependency Injection**: Dagger Hilt (`@HiltAndroidApp`, `@HiltViewModel`, `@Inject constructor`)
- **Database**: Room (app settings, account, and 2FA persistence are provided via `:multiplatform-lib`)
- **UI Framework**: Jetpack Compose only (Material3, Navigation 3) - no XML layouts / ViewBinding
- **Navigation**: AndroidX Navigation 3 (`androidx.navigation3.*`) with Compose
- **Network**: Ktor (in `:multiplatform-lib`) + OkHttp; Infomaniak Core `network` / `auth` modules
- **Background work**: WorkManager + Hilt Work
- **Crash Reporting**: Sentry (auto-installed via the Sentry Gradle plugin)
- **Analytics**: Matomo

## Context Map

### Code

```
app/src/main/kotlin/com/infomaniak/auth/
├── MainApplication.kt          # @HiltAndroidApp, Configuration.Provider, configures Sentry / Matomo / AccountUtils
├── MatomoAuthenticator.kt      # Matomo wrapper
├── data/
│   └── preferences/            # Splitties `Preferences` wrappers (SentryPreferences, PermissionPreferences, ...)
├── di/                         # Hilt modules
│   ├── ApplicationModule.kt
│   └── DatabaseModule.kt
├── service/                    # WorkManager workers and cross-app services
│   ├── CrossAppLoginService.kt
│   └── DeviceInfoUpdateWorker.kt
├── ui/
│   ├── screen/                 # Compose screens grouped by feature
│   │   ├── main/               # MainActivity, root nav host
│   │   ├── onboarding/
│   │   ├── login/
│   │   ├── home/               # Account list / TOTP codes
│   │   ├── accountlist/
│   │   ├── accountdetails/
│   │   ├── securingaccount/
│   │   ├── permission/
│   │   └── settings/
│   ├── applock/                # App-lock integration (com.infomaniak.core.applock)
│   ├── components/             # Reusable @Composables
│   ├── dialog/                 # Compose dialogs (priorityevent, disconnect, ...)
│   ├── images/illus/           # Vector illustrations rendered with ImageVector
│   ├── navigation/             # Navigation 3 routes / NavKeys
│   ├── previewparameter/       # Compose `@PreviewParameter` providers
│   └── theme/                  # Material3 theme + color tokens
└── utils/                      # AccountUtils, MigrationUtils, NotificationUtils, GetSetCallbacks

app/src/main/res/
├── values/                     # strings, themes (Compose-driven, minimal XML)
├── drawable/                   # Vector drawables (most illustrations live in ui/images/illus as Compose)
└── ...

app/src/standard/               # `standard` flavor (Google Play Services, Firebase Messaging)
```

### Tests

- `app/src/test/` (does not exist yet - add JUnit unit tests here when needed)
- `app/src/androidTest/` (does not exist yet - add instrumentation tests here when needed)
- Shared/business-logic tests live in `multiplatform-lib/src/commonTest` and `multiplatform-lib/src/androidHostTest` (see
  `multiplatform-lib/AGENTS.md`).

## Local Norms

### Architecture & Design

- **MVVM**: One Compose screen + one `ViewModel` per feature. ViewModels are Hilt-injected (
  `@HiltViewModel class FooViewModel @Inject constructor(...)`).
- **Repository pattern**: Data access goes through repositories exposed by `:multiplatform-lib` (e.g. `AuthenticatorFacade`,
  account/2FA repositories). The app layer should not talk to Room/Ktor directly when a multiplatform repository exists.
- **DI**: Provide bindings in `di/ApplicationModule.kt` / `di/DatabaseModule.kt` (or new feature-scoped Hilt modules). Use
  `@Inject constructor` on classes you own.
- **Preferences**: Use the Splitties `Preferences` + `SuspendPrefsAccessor` pattern already in `data/preferences/` (e.g.
  `SentryPreferences`, `PermissionPreferences`). Each pref typically exposes a `valueFlow()`-based `*Flow` property.
- **Single responsibility**: Keep screens focused; extract sub-composables into `ui/components/` or a sibling file when a screen
  file grows large.

### Commands

```bash
# Build debug (standard flavor)
./gradlew :app:assembleStandardDebug

# Build release (requires env.properties with sentryAuthToken)
./gradlew :app:assembleStandardRelease

# Lint (uses app/lint-baseline.xml)
./gradlew :app:lint

# Unit tests (when present)
./gradlew :app:testStandardDebugUnitTest

# Instrumented tests (require a device/emulator)
./gradlew :app:connectedStandardDebugAndroidTest

# Clean
./gradlew clean
```

> Reminder: all Gradle commands require the `Core/` submodule to be initialised - run `git submodule update --init --recursive`
> first.

### Code Style

**Line Length**

- Maximum 130 characters per line for Kotlin files.
- Exceptions: single-line comments, import statements, hardcoded strings.

**Blank Lines**

- Never use more than 1 consecutive blank line.
- Always add 1 blank line after early `return` statements/blocks.

**Copyright Headers**

- Required in ALL files (Kotlin, XML resources, scripts, property files).
- Format: `Copyright (C) YYYY Infomaniak Network SA` or `Copyright (C) startYear-endYear Infomaniak Network SA`.
- Use the GPLv3 header already present in existing files (see `app/src/main/kotlin/com/infomaniak/auth/MainApplication.kt`).
- No blank line between the closing `*/` and the `package` declaration.

**Naming**

- Classes / Composables: `PascalCase` (`MainActivity`, `AccountListScreen`, `AccountDetailsViewModel`).
- Functions / Properties: `camelCase` (`getAccounts()`, `isLoading`).
- Packages: lowercase, dot-separated (`com.infomaniak.auth.ui.screen.home`).
- New enums: `PascalCase` entries (`Active`, `Inactive`).
- Old enums / serialized values: DO NOT rename (stored in `SharedPreferences` / Room - renaming would break upgrades).

**Control Flow**

```kotlin
// Trivial statements: prefer one-line (under 130 chars)
if (condition) return result

// Trivial if/else: prefer one-line
val color = if (isDark) darkColor else lightColor

// Non-trivial: always use braces + newlines
if (condition) {
    doSomething()
    doAnotherThing()
}
```

**Jetpack Compose**

```kotlin
// One parameter: one line (if under 130 chars)
@Composable
fun MyButton(onClick: () -> Unit) {
    Text("Click")
}

// Multiple parameters: standard formatting with proper indent
@Composable
fun MyButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // implementation
}
```

**Resources (XML)**

- Remove `fillColor="#00000000"` (invisible colors auto-added by Figma exports).
- Follow Android Studio formatting conventions on `drawable/` files (use Android Studio's Reformat Code).

**Kotlin Files**

- Follow official Kotlin code style (`kotlin.code.style=official` in `gradle.properties`).
- GPLv3 license headers are required.

### UI Development

- **Compose-only**: All new screens are Jetpack Compose with Material3 components. There are no XML layouts or `ViewBinding` in
  this module.
- **Navigation**: Use AndroidX Navigation 3 (`androidx.navigation3.runtime` / `.ui`) with typed routes / `NavKey`s in
  `ui/navigation/`.
- **Theming**: Use the project theme from `ui/theme/` (Material3). Reuse the Core Compose theme helpers (
  `core.infomaniak.core.ui.compose.theme`).
- **Reusable components**: Put shared `@Composable`s in `ui/components/`. Prefer Core composables (
  `core.infomaniak.core.ui.compose.*`) when one already exists.
- **Previews**: Provide `@Preview` functions with `@PreviewParameter` providers from `ui/previewparameter/`.
- **Illustrations**: Prefer `ImageVector`s under `ui/images/illus/` over XML drawables.

### Testing

- Unit tests (when added): `app/src/test/java/` or `app/src/test/kotlin/`, JUnit 4 + MockK.
- UI tests (when added): `app/src/androidTest/`, Compose UI Testing (`androidx.compose.ui.test.*`).
- Business-logic tests should live in `multiplatform-lib/src/commonTest` whenever the code under test is multiplatform.

### Product Flavors

| Flavor   | Description                                             | Dependencies                                                                     |
|----------|---------------------------------------------------------|----------------------------------------------------------------------------------|
| standard | Full features, Google Play Services, push notifications | `standardImplementation` (Firebase Messaging, `core.notifications.registration`) |
| fdroid   | FOSS variant, no proprietary dependencies               | `fdroidImplementation`                                                           |

When adding a feature that depends on Google services, gate it behind the `standard` source set (`app/src/standard/...`).

### Environment

- `env.properties`: required for release builds (`sentryAuthToken`). See `env.example.properties`.
- `local.properties`: local SDK paths (auto-generated).
- `google-services.json`: committed under `app/`.
- Never commit `env.properties` or `local.properties` (see root `.gitignore`).

### Lint

- The module uses a lint baseline at `app/lint-baseline.xml` (`android { lint { baseline = file("lint-baseline.xml") } }`).
- Do not blanket-regenerate the baseline. If your change introduces a new lint issue, fix it; only update the baseline for issues
  that are unrelated to your change and already accepted.

## Learned Preferences

Add project-specific corrections here as they occur.

**Kotlin Control Flow**

- Prefer one-line `if`/`else` for trivial statements under 130 chars.
- Always use braces + newlines for non-trivial statements.

**Jetpack Compose**

- One-line composables with a single parameter (within the line limit).

**Resources (XML)**

- Remove `fillColor="#00000000"` (invisible colors from Figma imports).
- Follow Android Studio formatting on PRs if formatting is off.

## Self-correction

- **Stale Map**: Update when you encounter new files/folders not listed.
- **New Norms**: Add user corrections to "Learned Preferences" immediately.
- **Reference Core / multiplatform-lib**: When editing imports from `com.infomaniak.core.*` or from the shared module, check
  `Core/AGENTS.md` and `multiplatform-lib/AGENTS.md` respectively for their specific norms.
