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

import com.infomaniak.auth.lib.internal.KeyPairManager
import com.infomaniak.auth.lib.internal.MigrationAuthentication
import com.infomaniak.auth.lib.internal.RestoreFromBackupDetector
import com.infomaniak.auth.lib.internal.db.AccountEntity
import com.infomaniak.auth.lib.internal.db.AccountsDatabase
import com.infomaniak.auth.lib.internal.extensions.cancellable
import com.infomaniak.auth.lib.internal.extensions.toEntity
import com.infomaniak.auth.lib.internal.models.AuthResult
import com.infomaniak.auth.lib.internal.models.OtpPayload
import com.infomaniak.auth.lib.internal.otp.TotpGenerator
import com.infomaniak.auth.lib.internal.otp.getLegacyAccounts
import com.infomaniak.auth.lib.internal.otp.getSecretFor
import com.infomaniak.auth.lib.internal.otp.needMigration
import com.infomaniak.auth.lib.internal.requests.WebAuthnRequests
import com.infomaniak.auth.lib.models.migration.SharedApiToken
import com.infomaniak.auth.lib.network.exceptions.ApiException
import com.infomaniak.auth.lib.network.interfaces.CrashReportInterface
import com.osmerion.kotlin.io.encoding.Base32
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.io.IOException
import okio.ByteString.Companion.encodeUtf8
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

internal class MigrationManager(
    private val coroutineScope: CoroutineScope,
    private val crashReport: CrashReportInterface,
    private val accountsDatabase: AccountsDatabase,
    private val authenticatorManager: AuthenticatorManager,
    private val webAuthnRequests: WebAuthnRequests,
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

    suspend fun restore(account: AccountEntity, persistToken: suspend (userId: Long, token: SharedApiToken) -> Unit) {
        val restorer = AccountRestorer(
            accountsDatabase = accountsDatabase,
            authenticatorManager = authenticatorManager,
            webAuthnRequests = webAuthnRequests,
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
        authentication: MigrationAuthentication,
    ): Boolean {
        if (alreadyHasValidPasskey(userId)) return true

        @OptIn(ExperimentalUuidApi::class)
        val deviceId = Uuid.random().toHexDashString()
        val secret = checkNotNull(getSecretFor(userId)) { "Couldn't find the secret for user $userId" }
        val migrationOptions = webAuthnRequests.getMigrationOptions(
            deviceId = deviceId,
            userId = userId,
        )
        val temporaryToken = when (authentication) {
            is MigrationAuthentication.CrossAppLogin -> authentication.derivedToken
            else -> {
                val otp = getOtp(secret = secret, timestampSeconds = migrationOptions.timestamp)
                val assertion = "${migrationOptions.session}:${migrationOptions.timestamp}"
                    .encodeUtf8()
                    .hmacSha256(secret.encodeUtf8())
                    .hex()
                val password = when (authentication) {
                    is MigrationAuthentication.NoOngoingLogin -> authentication.password
                    is MigrationAuthentication.OngoingLogin -> null
                }

                runCatching {
                    webAuthnRequests.getTokenForMigration(
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
        //TODO: Figure out which state we were in with the bug, and see if we can reliably fix it up, without
        // affecting other unrelated cases.

        coroutineScope.launch { // TODO: Remove this whole block once the backend is updated to do it automatically.
            runCatching {
                webAuthnRequests.completeMigration(
                    token = temporaryToken.accessToken,
                    sessionId = migrationOptions.session,
                    deviceId = deviceId
                )
            }.cancellable().onFailure { crashReport.capture(userId = userId, "completeMigration failed", it) }
        }
        return true
    }

    private suspend fun alreadyHasValidPasskey(userId: Long): Boolean {
        val keyId = authenticatorManager.keyPairManager.findKeyIdFor(KeyPairManager.MatchOn.UserId(userId))
            ?: return false // No passkey for this user.
        return runCatching {
            val result = authenticatorManager.getToken(clientId = clientId, userId = userId, keyIdOrDefault = keyId)
            result.firstOrNull() != null // If we can get a token successfully, the passkey is valid.
        }.cancellable().getOrElse {
            when (it) {
                is ApiException.ApiErrorException if it.errorCode == "not_authorized" -> false // Invalid passkey
                else -> throw it // Other error, propagate it.
            }
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

    private fun AuthResult.toApiToken(): SharedApiToken {
        return SharedApiToken(
            accessToken = this.accessToken,
            tokenType = this.tokenType,
            userId = this.userId.toInt(),
            scope = this.scope,
        )
    }
}
