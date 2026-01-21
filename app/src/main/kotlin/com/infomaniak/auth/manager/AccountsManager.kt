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
package com.infomaniak.auth.manager

import com.infomaniak.core.auth.CredentialManager
import com.infomaniak.core.auth.models.user.User
import com.infomaniak.core.auth.room.UserDatabase
import io.sentry.Sentry
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton
import io.sentry.protocol.User as SentryUser

// TODO[ik-auth]: Add better manager with auth logic when ready. It's just the minimum for crossapplogin
// TODO[CrossAppLogin]: When adding/removing users, call MainApplication.userDataCleanableList.forEach { it.resetForUser(userId) }
@Singleton
class AccountsManager @Inject constructor(
    override val userDatabase: UserDatabase
) : CredentialManager() {
    override var currentUser: User? = null
        set(user) {
            field = user
            currentUserId = user?.id ?: DEFAULT_USER_ID
            Sentry.setUser(SentryUser().apply {
                id = currentUserId.toString()
                email = user?.email
            })
        }

    override var currentUserId: Int = currentUser?.id ?: DEFAULT_USER_ID

    fun getCurrentUserFlow(): Flow<User?> = userDatabase.userDao().getFirstFlow()

    companion object {
        private const val DEFAULT_USER_ID = -1
    }
}
