import org.gradle.kotlin.dsl.kotlin

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(core.plugins.android.application) apply false
    alias(core.plugins.kotlin.android) apply false
}
