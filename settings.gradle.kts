pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
    includeBuild("Core/build-logic")
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral {
            content { excludeGroup("com.infomaniak.multiplatform_authenticator") }
        }
        // Needed for `com.github.lottiefiles:dotlottie-android` and `com.github.infomaniak:android-login`
        maven(url = "https://jitpack.io")
        maven {
            name = "infomaniakReposiliteRepositorySnapshots"
            url = uri("https://maven.infomaniak.app/snapshots")
            content { includeGroup("com.infomaniak.multiplatform_authenticator") }
        }
        maven {
            name = "infomaniakReposiliteRepository"
            url = uri("https://maven.infomaniak.app/releases")
            content { includeGroup("com.infomaniak.multiplatform_authenticator") }
        }
    }
    versionCatalogs {
        create("core") {
            from(files("Core/gradle/core.versions.toml"))
        }
        create("kmpAuthenticator") {
            from(files("multiplatform-authenticator/gradle/kmpAuthenticator.versions.toml"))
        }
    }
}

plugins {
    id("com.infomaniak.core.composite")
}

rootProject.name = "Authenticator"
include(":app")

// Read local.properties first (git-ignored), then fall back to gradle.properties.
// Set useAuthenticatorCoreCompositeBuild=true in local.properties to use the local multiplatform-authenticator
// submodule source via composite build instead of the published AAR/XCFramework artifacts.
val localProperties = java.util.Properties().also { props ->
    val localPropertiesFile = file("local.properties")
    if (localPropertiesFile.exists()) localPropertiesFile.reader().use { props.load(it) }
}
val useAuthenticatorCoreCompositeBuild = (localProperties.getProperty("useAuthenticatorCoreCompositeBuild")
    ?: providers.gradleProperty("useAuthenticatorCoreCompositeBuild").orNull)
    ?.toBoolean() ?: false
gradle.extra["useAuthenticatorCoreCompositeBuild"] = useAuthenticatorCoreCompositeBuild

if (useAuthenticatorCoreCompositeBuild) {
    includeBuild("multiplatform-authenticator") {
        dependencySubstitution {
            substitute(module("com.infomaniak.multiplaform-authenticator:multiplatform-authenticator")).using(project(":AuthenticatorCore"))
        }
    }
}
