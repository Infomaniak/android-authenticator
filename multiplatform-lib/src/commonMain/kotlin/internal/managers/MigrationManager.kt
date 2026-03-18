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
package com.infomaniak.auth.lib.managers

import com.infomaniak.auth.lib.internal.managers.AuthenticatorManager
import com.infomaniak.auth.lib.internal.repositories.WebAuthnRepository
import com.infomaniak.auth.lib.otp.TotpGenerator
import com.infomaniak.auth.lib.otp.currentTimeMillis
import com.infomaniak.auth.lib.otp.getLegacyAccounts
import com.infomaniak.auth.lib.otp.needMigration
import com.osmerion.kotlin.io.encoding.Base32

internal class MigrationManager(
    private val authenticatorManager: AuthenticatorManager,
    private val webAuthnRepository: WebAuthnRepository,
    private val clientId: String,
    private val deviceId: String,
) {

    // Get previous BDD => Done
    // Get UserId and secret  => Done
    // Generate OTP from secret => Done
    // getMigrationOptions  => Done
    // getTokenForMigration  => Done
    // WebAuthn flow  => Done
    // completeMigration  => Done

    suspend fun migrate(onGetToken: suspend (userId: String, token: String) -> Unit) {
        if (!needMigration()) return

        getLegacyAccounts().apply {
            if (isEmpty()) return@apply

            forEach { legacyAccount ->
                val otp = getOtp(legacyAccount.secret)
                val migrationOptions = webAuthnRepository.getMigrationOptions(
                    deviceId = deviceId,
                    userId = legacyAccount.userId.toString(),
                )
                val authResult = webAuthnRepository.getTokenForMigration(
                    migrationOptions.session,
                    deviceId = deviceId,
                    userId = legacyAccount.userId.toString(),
                    otp = otp,
                )
                authenticatorManager.registerPasskey(authResult.accessToken, legacyAccount.userId.toLong())
                val token = authenticatorManager.getToken(clientId, legacyAccount.userId.toLong()).firstOrNull() ?: return@forEach
                onGetToken(legacyAccount.userId.toString(), token)
                webAuthnRepository.completeMigration(token, deviceId)
            }

        }
    }

    private fun getOtp(secret: String): String {
        val generator = TotpGenerator(
            secret = Base32.decode(secret),
            digits = 6,
            algorithm = TotpGenerator.Algorithm.SHA1,
        )
        return generator.generate(currentTimeMillis() / 1000)
    }
}
