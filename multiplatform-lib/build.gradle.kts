import com.android.build.api.dsl.androidLibrary

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(core.plugins.android.kmp.library)
}

val androidCompileSdk: Int by rootProject.extra

kotlin {
    @Suppress("UnstableApiUsage")
    androidLibrary {
        namespace = "com.infomaniak.auth.multiplatform"
        compileSdk = androidCompileSdk
    }
    iosArm64()
}

dependencies.commonMainImplementation(core.kotlinx.coroutines.core)
