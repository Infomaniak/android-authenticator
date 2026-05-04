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

import com.infomaniak.auth.lib.internal.KeyPairManager.MatchOn
import com.infomaniak.auth.lib.internal.db.AccountEntity
import com.infomaniak.auth.lib.internal.db.AccountsDatabase
import com.infomaniak.auth.lib.internal.extensions.firstOrElse
import com.infomaniak.auth.lib.internal.repositories.WebAuthnRepository
import com.infomaniak.auth.lib.models.migration.SharedApiToken

internal class AccountRestorer(
    accountsDatabase: AccountsDatabase,
    private val authenticatorManager: AuthenticatorManager,
    private val webAuthnRepository: WebAuthnRepository,
    private val clientId: String,
) {

    private val dao = accountsDatabase.getDao()

    suspend fun restore(account: AccountEntity, persistToken: suspend (userId: Long, token: SharedApiToken) -> Unit) {
        val keyPairManager = authenticatorManager.keyPairManager
        val existingKeyIds = keyPairManager.getSortedKeyIds(MatchOn.UserId(account.id))
        checkKeyCountIsOneOrTwo(existingKeyIds)
        val needsToCreateNewKey = !account.hasNewKeyAlreadyBeenRegistered()
        val oldKeyId: String?
        val newKeyId: String
        if (needsToCreateNewKey) {
            oldKeyId = existingKeyIds.first()
            val tokenFromOldPasskey = authenticatorManager.getToken(
                clientId = clientId,
                userId = account.id,
                keyIdOrDefault = oldKeyId,
            ).firstOrElse { error(it) }

            val previousRestorationAborted = existingKeyIds.size == 2
            if (previousRestorationAborted) {
                val newKeyIdToDrop = existingKeyIds.last()
                webAuthnRepository.deletePasskeyIfExists(tokenFromOldPasskey.accessToken, newKeyIdToDrop)
                val _ = keyPairManager.deleteKeysMatching(MatchOn.PasskeyId(newKeyIdToDrop))
            }
            // Register a new passkey
            newKeyId = authenticatorManager.registerPasskey(tokenFromOldPasskey.accessToken, account.id)
            dao.upsert(account.copy(status = AccountEntity.Status.DeletingOldKeyAfterRestoration))
            // The DB update above is expected to cause the cancellation & restart of this. It's handled by the else case below.
        } else {
            val stillHasOldKey = existingKeyIds.size == 2
            oldKeyId = if (stillHasOldKey) existingKeyIds.first() else null
            newKeyId = existingKeyIds.last()
        }
        // Getting a new token with the new passkey
        val tokenWithNewPassKey = authenticatorManager.getToken(
            clientId = clientId,
            userId = account.id,
            keyIdOrDefault = newKeyId,
        ).firstOrElse { error(it) }
        persistToken(account.id, tokenWithNewPassKey)
        // We can safely delete the old passkey, as the new one is working and the old token won't be valid anymore
        oldKeyId?.let { keyId ->
            webAuthnRepository.deletePasskeyIfExists(tokenWithNewPassKey.accessToken, keyId)
            val _ = keyPairManager.deleteKeysMatching(MatchOn.PasskeyId(keyId))
        }
        dao.upsert(account.copy(status = AccountEntity.Status.LoggedIn))
    }

    private fun AccountEntity.hasNewKeyAlreadyBeenRegistered() = when (status) {
        AccountEntity.Status.RestoringFromBackup -> false
        AccountEntity.Status.DeletingOldKeyAfterRestoration -> true
        AccountEntity.Status.PasswordChanged,
        AccountEntity.Status.ToBeMigrated,
        AccountEntity.Status.PasskeyRegistrationPending,
        AccountEntity.Status.FirstPasskeyAuthenticationPending,
        AccountEntity.Status.LoggedIn -> error("Unexpected status for restoration: $status")
    }

    private fun checkKeyCountIsOneOrTwo(existingKeyIds: List<String>) {
        val keyCount = existingKeyIds.size
        check(keyCount in 1..2) {
            when (keyCount) {
                0 -> "No key found to restore with."
                else -> "We should never have more than 2 keys during restoration, yet, $keyCount keys were found."
            }
        }
    }
}
