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
internal data class ActivationPayload(
    val publicKeys: PublicKeys,
) {
    @Serializable
    data class PublicKeys(
        val sensitiveOperations: SensitiveOperations,
        val basicOperations: String,
        val backup: String,
    ) {
        @Serializable
        data class SensitiveOperations(
            val biometricsGuarded: BiometricsGuarded?,
            val devicePasscodeGuarded: String?,
            val userGuarded: String,
        ) {
            @Serializable
            data class BiometricsGuarded(
                val currentOnly: String,
                val currentAndFuture: String,
            )
        }
    }
}
