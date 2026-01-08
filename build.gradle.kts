// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    extra.apply {
        set("appCompileSdk", 36)
        set("appTargetSdk", 36)
        set("appMinSdk", 27)
        set("javaVersion", JavaVersion.VERSION_17)
    }
}

plugins {
    alias(core.plugins.android.application) apply false
    alias(core.plugins.kotlin.android) apply false
}
