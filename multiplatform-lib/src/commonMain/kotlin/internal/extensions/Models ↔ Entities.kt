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
package com.infomaniak.auth.lib.internal.extensions

import com.infomaniak.auth.lib.Account
import com.infomaniak.auth.lib.NotConnectedAction
import com.infomaniak.auth.lib.internal.db.AccountEntity
import com.infomaniak.auth.lib.internal.db.AccountEntity.Status
import com.infomaniak.auth.lib.internal.models.LegacyUser

internal fun AccountEntity.toAccount(action: NotConnectedAction?): Account {
    return Account(
        id = id,
        fullName = fullName,
        initials = initials,
        email = email,
        avatarUrl = avatarUrl,
        status = when (status) {
            AccountEntity.Status.LoggedIn -> Account.Status.LoggedIn
            else -> Account.Status.NotConnected(action)
        }
    )
}

internal fun Account.toEntity(status: AccountEntity.Status): AccountEntity {
    return AccountEntity(
        id = id,
        fullName = fullName,
        initials = initials,
        email = email,
        avatarUrl = avatarUrl,
        status = status
    )
}

internal fun LegacyUser.toEntity(): AccountEntity {
    val initials = "${displayName.firstOrNull()?.uppercase()}" +
            "${displayName.substring(displayName.indexOf(" ") + 1).firstOrNull()?.uppercase()}"
    return AccountEntity(
        id = userID.toLong(),
        fullName = displayName,
        initials = initials,
        email = email,
        avatarUrl = avatar,
        status = Status.ToBeMigrated
    )
}
