import com.android.build.api.dsl.androidLibrary

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(core.plugins.android.library)
}

val androidCompileSdk: Int by rootProject.extra
val appMinSdk: Int by rootProject.extra

android {
    namespace = "com.infomaniak.auth.multiplatform"
    compileSdk = androidCompileSdk
}

kotlin {
    @Suppress("DEPRECATION") //TODO[ik-auth]: Revert commit with this change once Android Studio Otter 3 is released.
    androidTarget {
        publishLibraryVariants("release")
    }
    iosArm64()
}
