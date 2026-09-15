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
package com.infomaniak.auth.backup

import android.app.backup.FullBackupDataOutput
import androidx.room.immediateTransaction
import androidx.room.useWriterConnection
import com.infomaniak.core.auth.models.user.User
import com.infomaniak.core.auth.room.UserDatabase
import com.infomaniak.core.common.backup.FullBackupAgent
import kotlinx.coroutines.runBlocking

class AuthenticatorFullBackupAgent : FullBackupAgent(RestorationPolicy.AllBackedUpFiles) {

    override fun onFullBackup(data: FullBackupDataOutput) {
        val blockStoreBackupSucceeded = runBlocking { BlockStoreBackup.backupPasskeys() }
        if (!blockStoreBackupSucceeded) return // Don't backup anything if we can't save the passkeys.

        val db = UserDatabase()
        // We don't want to keep tokens in the db for backup, so we remove them temporarily.
        // Note that the app can perfectly recover from this state if the backup process is aborted, here's why:
        // Authenticated API calls with an empty token will result in a 401 http status code,
        // which will lead to the token being refreshed using the passkey.
        val usersWithTokens = runBlocking { db.getUsersAndRemoveTokens() }
        try {
            super.onFullBackup(data)
        } finally {
            runBlocking { db.putTokensBack(usersWithTokens) }
        }
    }

    private suspend fun UserDatabase.getUsersAndRemoveTokens(): List<User> = useWriterConnection { transactor ->
        transactor.immediateTransaction {
            userDao().allUsers().also { users ->
                users.forEach { user ->
                    userDao().update(user = user.copy(apiToken = user.apiToken.copy(accessToken = "", refreshToken = null)))
                }
            }
        }
    }

    private suspend fun UserDatabase.putTokensBack(usersWithTokens: List<User>) {
        useWriterConnection { transactor ->
            transactor.immediateTransaction {
                usersWithTokens.forEach { user ->
                    // We don't need the refreshToken even if it's there because we're using passkeys instead.
                    userDao().updateUserToken(user.id, user.apiToken.accessToken)
                }
            }
        }
    }
}
