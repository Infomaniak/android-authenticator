pluginManagement {
    includeBuild("buildTools")
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
    }
}

plugins {
    id("com.infomaniak.core.composite")
}

rootProject.name = "Authenticator"
include(":app")
include("multiplatform-lib")
