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

import com.infomaniak.auth.lib.internal.MigrationAuthentication
import com.infomaniak.auth.lib.internal.db.AccountsDatabase
import com.infomaniak.auth.lib.internal.extensions.cancellable
import com.infomaniak.auth.lib.internal.extensions.firstOrElse
import com.infomaniak.auth.lib.internal.extensions.toEntity
import com.infomaniak.auth.lib.internal.models.OtpPayload
import com.infomaniak.auth.lib.internal.otp.TotpGenerator
import com.infomaniak.auth.lib.internal.otp.deleteLegacyAccount
import com.infomaniak.auth.lib.internal.otp.deleteLegacyDB
import com.infomaniak.auth.lib.internal.otp.getLegacyAccounts
import com.infomaniak.auth.lib.internal.otp.getSecretFor
import com.infomaniak.auth.lib.internal.otp.needMigration
import com.infomaniak.auth.lib.internal.repositories.WebAuthnRepository
import com.osmerion.kotlin.io.encoding.Base32
import io.ktor.utils.io.core.toByteArray
import kotlinx.io.IOException
import network.exceptions.ApiException
import org.kotlincrypto.macs.hmac.sha2.HmacSHA256
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

    /**
     * @return false if the backend returned the `access_denied`, which means a correct password is needed (in [authentication]).
     *
     * @throws IOException in case of networking or I/O issues
     * @throws ApiException in case the backend returns a non-successful response (except for "access_denied")
     * @throws IllegalStateException in case of local issues (not supposed to happen & not recoverable)
     */
    suspend fun tryMigrating(
        userId: Long,
        persistToken: suspend (token: String) -> Unit,
        authentication: MigrationAuthentication,
    ): Boolean {
        @OptIn(ExperimentalUuidApi::class)
        val deviceId = Uuid.random().toHexDashString()
        val secret = checkNotNull(getSecretFor(userId)) { "Couldn't find the secret for user $userId" }
        val migrationOptions = webAuthnRepository.getMigrationOptions(
            deviceId = deviceId,
            userId = userId,
        )
        val tokenToUse = when (authentication) {
            is MigrationAuthentication.CrossAppLogin -> authentication.derivedToken
            else -> {
                val otp = getOtp(secret = secret, timestampSeconds = migrationOptions.timestamp)
                val assertion = HmacSHA256(secret.toByteArray())
                    .doFinal("${migrationOptions.session}:${migrationOptions.timestamp}".toByteArray())
                    .toHexString()
                val password = when (authentication) {
                    is MigrationAuthentication.NoOngoingLogin -> authentication.password
                    is MigrationAuthentication.OngoingLogin -> null
                }

                runCatching {
                    webAuthnRepository.getTokenForMigration(
                        sessionId = migrationOptions.session,
                        otpPayload = OtpPayload(
                            deviceId = deviceId,
                            userId = userId,
                            code = otp,
                            assertion = assertion,
                            password = password,
                        ),
                    ).accessToken
                }.cancellable().getOrElse {
                    if (it is ApiException.ApiErrorException && it.errorCode == "access_denied") {
                        return false
                    } else {
                        throw it
                    }
                }
            }
        }

        authenticatorManager.deleteKeysFor(userId)
        authenticatorManager.registerPasskey(
            token = tokenToUse,
            userId = userId
        )
        val token = authenticatorManager.getToken(
            clientId = clientId,
            userId = userId,
        ).firstOrElse { error("Didn't find the key locally: $it") }
        persistToken(token)
        webAuthnRepository.completeMigration(token = token, sessionId = migrationOptions.session, deviceId = deviceId)
        deleteLegacyAccount(userId.toString())

        if (getLegacyAccounts().isEmpty()) deleteLegacyDB()

        return true
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
