import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(core.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

val androidCompileSdk: Int by rootProject.extra
val appTargetSdk: Int by rootProject.extra
val appMinSdk: Int by rootProject.extra
val javaVersion: JavaVersion by rootProject.extra

android {
    namespace = "com.infomaniak.auth"
    compileSdk {
        version = release(androidCompileSdk)
    }

    defaultConfig {
        applicationId = "com.infomaniak.auth"
        minSdk = appMinSdk
        targetSdk = appTargetSdk
        versionCode = 2_00_000_00
        versionName = "2.0.0-DEV"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        setProperty("archivesBaseName", "infomaniak-authenticator-$versionName ($versionCode)")
    }


    buildTypes {
        release {
            isMinifyEnabled = false
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
    }
}

dependencies {
    implementation(core.androidx.core.ktx)
    // Compose
    implementation(platform(core.compose.bom))
    implementation(core.compose.ui)
    implementation(core.compose.ui.graphics)
    implementation(core.compose.ui.tooling.preview)
    implementation(core.compose.material3)
}
