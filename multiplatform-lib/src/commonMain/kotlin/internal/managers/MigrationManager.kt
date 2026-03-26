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

import com.infomaniak.auth.lib.internal.db.AccountEntity.Status
import com.infomaniak.auth.lib.internal.db.AccountsDatabase
import com.infomaniak.auth.lib.internal.extensions.cancellable
import com.infomaniak.auth.lib.internal.extensions.toEntity
import com.infomaniak.auth.lib.internal.models.TokenFromOtp
import com.infomaniak.auth.lib.internal.repositories.WebAuthnRepository
import com.infomaniak.auth.lib.otp.TotpGenerator
import com.infomaniak.auth.lib.otp.getLegacyAccounts
import com.infomaniak.auth.lib.otp.needMigration
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

        getLegacyAccounts().apply {
            if (isEmpty()) return@apply

            accountsDatabase.getDao().upsert(this.map { it.toEntity(Status.ToBeMigrated) })
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    suspend fun startMigration(
        onGetToken: suspend (userId: String, token: String) -> Unit,
    ) {
        val deviceId = Uuid.random().toString()
        runCatching {
            getLegacyAccounts().apply {
                if (isEmpty()) return@apply

                forEach { legacyAccount ->
                    val migrationOptions = webAuthnRepository.getMigrationOptions(
                        deviceId = deviceId,
                        userId = legacyAccount.userId.toString(),
                    )
                    val otp = getOtp(secret = legacyAccount.secret, timestampSeconds = migrationOptions.timestamp)
                    val authResult = webAuthnRepository.getTokenForMigration(
                        sessionId = migrationOptions.session,
                        tokenFromOtp = TokenFromOtp(deviceId, legacyAccount.userId.toLong(), otp),
                    )
                    authenticatorManager.registerPasskey(
                        token = authResult.accessToken,
                        userId = legacyAccount.userId.toLong()
                    )
                    val token = authenticatorManager.getToken(
                        clientId = this@MigrationManager.clientId,
                        userId = legacyAccount.userId.toLong(),
                    ).firstOrNull() ?: return@forEach
                    onGetToken(legacyAccount.userId.toString(), token)
                    webAuthnRepository.completeMigration(token = token, deviceId = deviceId)
                }
            }
        }.cancellable().onFailure {
            println("Error: $it")
            // Send the right AppStatus for native to display the right screen
        }
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
