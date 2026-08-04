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
        mavenCentral()
        // Needed for `com.github.lottiefiles:dotlottie-android` and `com.github.infomaniak:android-login`
        maven(url = "https://jitpack.io")
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
includeBuild("multiplatform-authenticator") {
    dependencySubstitution {
        substitute(module("com.infomaniak.multiplaform-authenticator:multiplatform-authenticator")).using(project(":AuthenticatorCore"))
    }
}
