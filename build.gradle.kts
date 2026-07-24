// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    extra.apply {
        set("androidCompileSdk", 36)
        set("appTargetSdk", 36)
        set("androidMinSdk", 27)
        set("javaVersion", JavaVersion.VERSION_21)
    }
}

subprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        compilerOptions {
            freeCompilerArgs.add("-Xannotation-default-target=param-property")
        }
    }
}

plugins {
    alias(core.plugins.android.application) apply false
    alias(core.plugins.android.kmp.library) apply false
    alias(core.plugins.android.library) apply false
    alias(core.plugins.compose.compiler) apply false
    alias(core.plugins.dagger.hilt) apply false
    alias(core.plugins.kotlin.serialization) apply false
    alias(core.plugins.sentry.plugin) apply false

    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.skie) apply false

    alias(libs.plugins.sonarqube)
    id("com.infomaniak.core.compose.lint")
}

sonar {
    properties {
        property("sonar.scanner.skipJreProvisioning", "true")
        property("sonar.projectKey", "Infomaniak_android-authenticator")
        property("sonar.organization", "infomaniak")
    }
}
