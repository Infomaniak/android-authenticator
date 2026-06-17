import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(core.plugins.android.application)
    alias(core.plugins.compose.compiler)
    alias(core.plugins.dagger.hilt)
    alias(libs.plugins.kotlin.android)
    alias(core.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(core.plugins.sentry.plugin)
    alias(core.plugins.google.services)
}

val androidCompileSdk: Int by rootProject.extra
val appTargetSdk: Int by rootProject.extra
val androidMinSdk: Int by rootProject.extra
val javaVersion: JavaVersion by rootProject.extra

android {
    namespace = "com.infomaniak.auth"
    compileSdk {
        version = release(androidCompileSdk)
    }

    defaultConfig {
        applicationId = "com.infomaniak.auth"
        minSdk = androidMinSdk
        targetSdk = appTargetSdk
        versionCode = 2_00_000_18
        versionName = "2.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        setProperty("archivesBaseName", "infomaniak-authenticator-$versionName ($versionCode)")

        buildConfigField("String", "GITHUB_REPO_URL", "\"https://github.com/Infomaniak/android-authenticator\"")
        buildConfigField("String", "CLIENT_ID", "\"A7B265CD-C9DB-4E6B-8236-2DFF60F146FC\"")

        androidResources {
            localeFilters += listOf("en", "da", "de", "el", "es", "fi", "fr", "it", "nb", "nl", "pl", "pt", "sv")
            generateLocaleConfig = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.fromTarget(javaVersion.toString()))
            freeCompilerArgs.add("-Xannotation-default-target=param-property")
        }
    }

    flavorDimensions += "distribution"

    productFlavors {
        create("standard") {
            dimension = "distribution"
            isDefault = true
        }
        create("fdroid") {
            dimension = "distribution"
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    androidResources {
        generateLocaleConfig = true
    }

    lint {
        baseline = file("lint-baseline.xml")
    }
}

val isRelease = gradle.startParameter.taskNames.any { it.contains("release", ignoreCase = true) }

val envProperties = rootProject.file("env.properties")
    .takeIf { it.exists() }
    ?.let { file -> Properties().also { props -> file.reader().use(props::load) } }

val sentryAuthToken = envProperties?.getProperty("sentryAuthToken")
    .takeUnless { it.isNullOrBlank() }
    ?: if (isRelease) error("The `sentryAuthToken` property in `env.properties` must be specified (see `env.example.properties`).") else ""

configurations.configureEach {
    // The Matomo SDK logs network issues to Timber, and the Sentry plugin detects the Timber dependency,
    // and adds its integration, which generates noise.
    // Since we're not using Timber for anything else, it's safe to completely disabled it,
    // as specified in Sentry's documentation: https://docs.sentry.io/platforms/android/integrations/timber/#disable
    exclude(group = "io.sentry", module = "sentry-android-timber")
}

sentry {
    autoInstallation.sentryVersion.set(core.versions.sentry)
    org = "sentry"
    projectName = "authenticator-android"
    authToken = sentryAuthToken
    url = "https://sentry-mobile.infomaniak.com"
    includeDependenciesReport = false
    includeSourceContext = isRelease

    // Enables or disables the automatic upload of mapping files during a build.
    // If you disable this, you'll need to manually upload the mapping files with sentry-cli when you do a release.
    // Default is enabled.
    autoUploadProguardMapping = true

    // Disables or enables the automatic configuration of Native Symbols for Sentry.
    // This executes sentry-cli automatically so you don't need to do it manually.
    // Default is disabled.
    uploadNativeSymbols = isRelease

    // Does or doesn't include the source code of native code for Sentry.
    // This executes sentry-cli with the --include-sources param. automatically so you don't need to do it manually.
    // Default is disabled.
    includeNativeSources = isRelease
}

dependencies {
    implementation(project(":multiplatform-lib"))

    implementation(core.infomaniak.core.applock)
    implementation(core.infomaniak.core.auth)
    implementation(core.infomaniak.core.avatar)
    implementation(core.infomaniak.core.coil)
    implementation(core.infomaniak.core.common)
    implementation(core.infomaniak.core.crossapplogin.front)
    implementation(core.infomaniak.core.inappreview)
    implementation(core.infomaniak.core.inappupdate)
    implementation(core.infomaniak.core.matomo)
    implementation(core.infomaniak.core.network)
    implementation(core.infomaniak.core.onboarding)
    implementation(core.infomaniak.core.privacymanagement)
    implementation(core.infomaniak.core.sentry)
    implementation(core.infomaniak.core.twofactorauth.back.withuserdb)
    implementation(core.infomaniak.core.twofactorauth.front)
    implementation(core.infomaniak.core.ui.compose.basics)
    implementation(core.infomaniak.core.ui.compose.basicbutton)
    implementation(core.infomaniak.core.ui.compose.bottomstickybuttonscaffolds)
    implementation(core.infomaniak.core.ui.compose.margin)
    implementation(core.infomaniak.core.ui.compose.preview)
    implementation(core.infomaniak.core.ui.compose.theme)
    implementation(core.infomaniak.core.ui.view)
    implementation(core.infomaniak.core.webview)

    implementation(core.androidx.core.splashscreen)
    implementation(core.androidx.work.runtime.ktx)

    implementation(core.kotlinx.collections.immutable)
    implementation(core.kotlinx.serialization.json)
    implementation(core.okhttp)

    implementation(core.androidx.adaptive)
    implementation(core.splitties.preferences)
    implementation(libs.accompanist.permissions)

    // Navigation 3
    implementation(core.androidx.lifecycle.viewmodel.navigation3)
    implementation(core.androidx.navigation3.runtime)
    implementation(core.androidx.navigation3.ui)

    // Notification
    "standardImplementation"(core.infomaniak.core.notifications.registration)
    "standardImplementation"(core.firebase.messaging.ktx)

    // Room
    implementation(core.room.ktx)
    implementation(core.room.runtime)
    ksp(core.room.compiler)

    // Hilt
    implementation(core.hilt.navigation.compose)
    implementation(core.hilt.android)
    implementation(core.hilt.work)
    ksp(core.hilt.compiler)
    ksp(core.hilt.androidx.compiler)
    // Workaround for Hilt + Kotlin 2.3.0 metadata compatibility
    // https://github.com/google/dagger/issues/5001
    annotationProcessor("org.jetbrains.kotlin:kotlin-metadata-jvm:2.3.0")

    // Compose
    implementation(platform(core.compose.bom))
    implementation(core.compose.ui)
    implementation(core.compose.ui.graphics)
    implementation(core.compose.ui.tooling.preview)
    implementation(core.compose.material3)
    implementation(core.activity.compose)
}
