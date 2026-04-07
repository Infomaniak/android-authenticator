import co.touchlab.skie.configuration.DefaultArgumentInterop
import com.android.build.api.dsl.androidLibrary
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
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
val javaVersion: JavaVersion by rootProject.extra

kotlin {
    @Suppress("UnstableApiUsage")
    androidLibrary {
        namespace = "com.infomaniak.auth.multiplatform"
        compileSdk = androidCompileSdk
        minSdk = androidMinSdk

        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }
        withHostTest {}
        compilerOptions {
            jvmTarget.set(JvmTarget.fromTarget(javaVersion.toString()))
        }
    }

    val xcframeworkName = "CoreAuthenticator"
    val xcf = project.XCFramework(xcframeworkName)
    listOf(
        iosArm64(),
        iosSimulatorArm64(),
        macosArm64(),
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
                implementation(project.dependencies.platform(libs.kotlincrypto.bom))
                implementation(libs.kotlincrypto.hmac.sha1)
                implementation(libs.kotlincrypto.hmac.sha2)
                implementation(libs.osmerion.kotlin.base32)
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
        appleMain {
            dependencies {
                implementation(core.ktor.client.darwin)
            }
        }

        listOf("iosArm64", "iosSimulatorArm64", "macosArm64").forEach { target ->
            getByName("${target}Main") {
                kotlin.srcDir(layout.buildDirectory.dir("generated/ksp/$target/${target}Main/kotlin"))
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
    add("kspMacosArm64", libs.androidx.room.compiler)
}

listOf("IosArm64", "IosSimulatorArm64", "MacosArm64").forEach { target ->
    tasks.named("compileKotlin$target") {
        dependsOn("kspKotlin$target")
    }
}
