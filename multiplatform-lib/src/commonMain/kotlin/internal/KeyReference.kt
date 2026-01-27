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
package com.infomaniak.auth.lib.internal

import kotlinx.serialization.Serializable

@Serializable
//TODO[ik-auth-back]: Check if serialization works as-is, or if we need to add the annotation on all sub-declarations.
internal sealed interface KeyReference {

    sealed interface Replaceable : KeyReference

    sealed interface SensitiveOperations : KeyReference {
        sealed interface BiometricsGuarded : SensitiveOperations, Replaceable {
            data object CurrentOnly : BiometricsGuarded
            data object CurrentAndFuture : BiometricsGuarded
        }

        data object DevicePasscodeGuarded : SensitiveOperations, Replaceable
        data object UserActionGuarded : SensitiveOperations
    }

    data object BasicOperations : KeyReference

    data object ActivationFromBackup : KeyReference
}
