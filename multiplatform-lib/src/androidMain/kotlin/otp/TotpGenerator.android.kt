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
package com.infomaniak.auth.lib.otp

import com.infomaniak.auth.lib.room.legacy.OTPUserDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import splitties.init.appCtx

actual fun currentTimeMillis(): Long = System.currentTimeMillis()

actual suspend fun getLegacyAccounts(): List<LegacyAccount> {
    val database = OTPUserDatabase.getInstance(appCtx)
    val legacyAccounts = withContext(Dispatchers.IO) {
        database.otpUserDao().getAllUsers().map {
            LegacyAccount(
                userId = it.userID,
                email = it.email,
                displayName = it.displayName,
                avatar = it.avatar,
                secret = it.secret
            )
        }
    }
    database.close()
    return legacyAccounts
}

actual suspend fun needMigration() = withContext(Dispatchers.IO) { appCtx.getDatabasePath("Infomaniak.db").exists() }
