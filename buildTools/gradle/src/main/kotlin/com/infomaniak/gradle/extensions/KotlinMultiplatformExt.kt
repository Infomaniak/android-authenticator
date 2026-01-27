/*
 * Infomaniak Authenticator - Android
 * Copyright (C) 2026 Infomaniak Network SA
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.infomaniak.gradle.extensions

import com.infomaniak.gradle.plugins.MultiplatformExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

internal fun Project.configureKotlinMultiplatform(extension: KotlinMultiplatformExtension): KotlinMultiplatformExtension {
    // Create the multiplatformExtension and add it to the project
    val multiplatformExtension = extensions.create<MultiplatformExtension>(MultiplatformExtension.EXTENSION_NAME)
    return extension.also {
        it.setup(this, multiplatformExtension)
    }
}

private fun KotlinMultiplatformExtension.setup(
    project: Project,
    multiplatformExtension: MultiplatformExtension
) {
    val xcframeworkName = project.name
    val xcf = project.XCFramework()
    listOf(
        iosArm64(),
        iosSimulatorArm64(),
        macosArm64(),
    ).forEach {
        it.binaries.framework {
            baseName = "CoreAuthenticator"
            binaryOption("bundleId", "com.infomaniak.multiplatform-authenticator.${xcframeworkName}")
            xcf.add(this)
            isStatic = true

            project.afterEvaluate {
                multiplatformExtension.appleExportedProjects.forEach { stProject ->
                    export(stProject)
                }
            }
        }
    }

    compilerOptions {
        freeCompilerArgs.add("-Xexport-kdoc") // Provide documentation with kDoc in Objective-C header
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }
}
