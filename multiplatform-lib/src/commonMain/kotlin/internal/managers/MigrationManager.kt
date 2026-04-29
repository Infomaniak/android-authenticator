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
import com.infomaniak.auth.lib.internal.RestoreFromBackupDetector
import com.infomaniak.auth.lib.internal.db.AccountEntity
import com.infomaniak.auth.lib.internal.db.AccountsDatabase
import com.infomaniak.auth.lib.internal.extensions.cancellable
import com.infomaniak.auth.lib.internal.extensions.firstOrElse
import com.infomaniak.auth.lib.internal.extensions.toEntity
import com.infomaniak.auth.lib.internal.models.AuthResult
import com.infomaniak.auth.lib.internal.models.OtpPayload
import com.infomaniak.auth.lib.internal.otp.TotpGenerator
import com.infomaniak.auth.lib.internal.otp.deleteLegacyAccount
import com.infomaniak.auth.lib.internal.otp.deleteLegacyDB
import com.infomaniak.auth.lib.internal.otp.getLegacyAccounts
import com.infomaniak.auth.lib.internal.otp.getSecretFor
import com.infomaniak.auth.lib.internal.otp.needMigration
import com.infomaniak.auth.lib.internal.repositories.WebAuthnRepository
import com.infomaniak.auth.lib.models.migration.ApiToken
import com.infomaniak.auth.lib.network.exceptions.ApiException
import com.osmerion.kotlin.io.encoding.Base32
import io.ktor.utils.io.core.toByteArray
import kotlinx.io.IOException
import org.kotlincrypto.macs.hmac.sha2.HmacSHA256
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

internal class MigrationManager(
    private val accountsDatabase: AccountsDatabase,
    private val authenticatorManager: AuthenticatorManager,
    private val webAuthnRepository: WebAuthnRepository,
    private val clientId: String,
) {

    private val dao = accountsDatabase.getDao()

    suspend fun setBackedUpAccountsStatus() {
        RestoreFromBackupDetector.runRestoreOperationIfNeeded {
            dao.updateStatus(
                currentStatus = AccountEntity.Status.LoggedIn,
                newStatus = AccountEntity.Status.RestoringFromBackup
            )
        }
    }

    suspend fun restore(account: AccountEntity, persistToken: suspend (userId: Long, token: String) -> Unit) {
        val restorer = AccountRestorer(
            accountsDatabase = accountsDatabase,
            authenticatorManager = authenticatorManager,
            webAuthnRepository = webAuthnRepository,
            clientId = clientId
        )
        restorer.restore(account, persistToken)
    }

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
        persistUser: suspend (apiToken: ApiToken) -> Unit,
        authentication: MigrationAuthentication,
    ): Boolean {
        @OptIn(ExperimentalUuidApi::class)
        val deviceId = Uuid.random().toHexDashString()
        val secret = checkNotNull(getSecretFor(userId)) { "Couldn't find the secret for user $userId" }
        val migrationOptions = webAuthnRepository.getMigrationOptions(
            deviceId = deviceId,
            userId = userId,
        )
        val temporaryToken = when (authentication) {
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
                        )
                    ).toApiToken()
                }.cancellable().getOrElse {
                    if (it !is ApiException.ApiErrorException) throw it
                    when (it.errorCode) {
                        "access_denied", "not_authorized" -> return false
                        else -> throw it
                    }
                }
            }
        }

        authenticatorManager.deleteKeysFor(userId)
        val _ = authenticatorManager.registerPasskey(
            token = temporaryToken.accessToken,
            userId = userId
        )
        val apiTokenFromPasskey = authenticatorManager.getToken(
            clientId = clientId,
            userId = userId,
        ).firstOrElse { error("Didn't find the key locally: $it") }
        persistUser(apiTokenFromPasskey)
        webAuthnRepository.completeMigration(
            token = apiTokenFromPasskey.accessToken,
            sessionId = migrationOptions.session,
            deviceId = deviceId
        )
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

    private fun AuthResult.toApiToken(): ApiToken {
        return ApiToken(
            accessToken = this.accessToken,
            tokenType = this.tokenType,
            userId = this.userId.toInt(),
            scope = this.scope,
        )
    }
}
