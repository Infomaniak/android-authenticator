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
package com.infomaniak.auth.lib.internal.managers

import com.infomaniak.auth.lib.internal.db.AccountsDatabase
import com.infomaniak.auth.lib.internal.extensions.firstOrElse
import com.infomaniak.auth.lib.internal.extensions.toEntity
import com.infomaniak.auth.lib.internal.models.OtpPayload
import com.infomaniak.auth.lib.internal.otp.TotpGenerator
import com.infomaniak.auth.lib.internal.otp.getLegacyAccounts
import com.infomaniak.auth.lib.internal.otp.getSecretFor
import com.infomaniak.auth.lib.internal.otp.needMigration
import com.infomaniak.auth.lib.internal.repositories.WebAuthnRepository
import com.osmerion.kotlin.io.encoding.Base32
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

internal class MigrationManager(
    private val accountsDatabase: AccountsDatabase,
    private val authenticatorManager: AuthenticatorManager,
    private val webAuthnRepository: WebAuthnRepository,
    private val clientId: String,
) {

    suspend fun addLegacyAccountsToDB() {
        if (!needMigration()) return

        val legacyAccounts = getLegacyAccounts().ifEmpty { return }
        accountsDatabase.getDao().upsert(legacyAccounts.map { it.toEntity() })
    }

    suspend fun tryMigrating(
        userId: Long,
        persistToken: suspend (token: String) -> Unit,
        temporaryToken: String?,
    ) {
        @OptIn(ExperimentalUuidApi::class)
        val deviceId = Uuid.random().toHexDashString()
        val secret = getSecretFor(userId) ?: return
        val migrationOptions = webAuthnRepository.getMigrationOptions(
            deviceId = deviceId,
            userId = userId,
        )
        val tokenToUse = temporaryToken ?: run {
            val otp = getOtp(secret = secret, timestampSeconds = migrationOptions.timestamp)
            webAuthnRepository.getTokenForMigration(
                sessionId = migrationOptions.session,
                otpPayload = OtpPayload(deviceId, userId, otp),
            ).accessToken
        }

        authenticatorManager.registerPasskey(
            token = tokenToUse,
            userId = userId
        )
        val token = authenticatorManager.getToken(
            clientId = clientId,
            userId = userId,
        ).firstOrElse { error("Didn't find the key locally: $it") }
        persistToken(token)
        webAuthnRepository.completeMigration(token = token, deviceId = deviceId)
    }

    private fun getOtp(secret: String, timestampSeconds: Long): String {
        val generator = TotpGenerator(
            secret = Base32.decode(secret),
            digits = 6,
            algorithm = TotpGenerator.Algorithm.SHA1,
        )
        return generator.generate(timestampSeconds)
    }
}
