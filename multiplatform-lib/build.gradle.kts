import com.android.build.api.dsl.androidLibrary

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(core.plugins.android.kmp.library)
}

val androidCompileSdk: Int by rootProject.extra
val androidMinSdk: Int by rootProject.extra

kotlin {
    @Suppress("UnstableApiUsage")
    androidLibrary {
        namespace = "com.infomaniak.auth.multiplatform"
        compileSdk = androidCompileSdk
        minSdk = androidMinSdk
    }
    iosArm64()

    sourceSets {
        androidMain {
            dependencies {
                implementation(core.splitties.appctx)
            }
        }
    }
}

dependencies.commonMainImplementation(core.kotlinx.coroutines.core)
