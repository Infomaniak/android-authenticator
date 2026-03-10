import co.touchlab.skie.configuration.DefaultArgumentInterop
import com.android.build.api.dsl.androidLibrary
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(core.plugins.android.kmp.library)
    alias(core.plugins.kotlin.serialization)
    alias(libs.plugins.skie)
    alias(libs.plugins.androidx.room)
    alias(libs.plugins.ksp)
    kotlin("plugin.parcelize")
}

val androidCompileSdk: Int by rootProject.extra
val androidMinSdk: Int by rootProject.extra

kotlin {
    @Suppress("UnstableApiUsage")
    androidLibrary {
        namespace = "com.infomaniak.auth.multiplatform"
        compileSdk = androidCompileSdk
        minSdk = androidMinSdk

        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }
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
            linkerOpts.add("-lsqlite3")
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.sqlite.bundled)
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(core.kotlinx.coroutines.core)
                implementation(core.kotlinx.serialization.json)
                implementation(core.kotlinx.serialization.cbor)
                implementation(core.ktor.client.core)
                implementation(core.ktor.client.content.negociation)
                implementation(core.ktor.client.json)
                implementation(core.ktor.client.encoding)
                implementation(core.okio)
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(core.kotlinx.coroutines.test)
                implementation(core.ktor.client.mock)
            }
        }
        androidMain {
            dependencies {
                implementation(core.ktor.client.okhttp)
                implementation(core.splitties.appctx)
                implementation(core.splitties.bitflags)
            }
        }
        iosMain {
            dependencies {
                implementation(core.ktor.client.darwin)
            }
        }
        val androidDeviceTest by getting {
            dependencies {
                implementation(core.androidx.junit)
                implementation(core.androidx.espresso.core)
            }
        }
    }

    compilerOptions {
        freeCompilerArgs.add("-Xexport-kdoc") // Provide documentation with kDoc in Objective-C header
        freeCompilerArgs.add("-Xexpect-actual-classes")
        freeCompilerArgs.add("-Xreturn-value-checker=full")
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

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspIosSimulatorArm64", libs.androidx.room.compiler)
    add("kspIosArm64", libs.androidx.room.compiler)
}
