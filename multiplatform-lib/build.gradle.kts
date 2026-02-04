import co.touchlab.skie.configuration.DefaultArgumentInterop
import com.android.build.api.dsl.androidLibrary
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(core.plugins.android.kmp.library)
    alias(libs.plugins.skie)
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

    val xcframeworkName = "CoreAuthenticator"
    val xcf = project.XCFramework(xcframeworkName)
    listOf(
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach {
        it.binaries.framework {
            baseName = xcframeworkName
            binaryOption("bundleId", "com.infomaniak.multiplatform-authenticator.${xcframeworkName}")
            xcf.add(this)
            isStatic = true
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(core.kotlinx.coroutines.core)
            }
        }
    }

    compilerOptions {
        freeCompilerArgs.add("-Xexport-kdoc") // Provide documentation with kDoc in Objective-C header
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }
}

skie {
    features {
        group {
            DefaultArgumentInterop.Enabled(true)
            DefaultArgumentInterop.MaximumDefaultArgumentCount(7)
        }
    }
    build {
        produceDistributableFramework()
    }
}
