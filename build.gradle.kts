// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    extra.apply {
        set("androidCompileSdk", 36)
        set("appTargetSdk", 36)
        set("appMinSdk", 27)
        set("javaVersion", JavaVersion.VERSION_17)
    }
}

plugins {
    alias(core.plugins.android.application) apply false
    alias(core.plugins.android.kmp.library) apply false
    alias(core.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(core.plugins.kotlin.serialization) apply false
    alias(core.plugins.compose.compiler) apply false
    alias(core.plugins.sentry.plugin) apply false
}
