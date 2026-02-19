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

import android.content.Context
import com.infomaniak.auth.MainApplication
import com.infomaniak.core.auth.UserAccountUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

// TODO[ik-auth]: Add better manager with auth logic when ready. It's just the minimum for crossapplogin
// TODO[CrossAppLogin]: When adding/removing users, call MainApplication.userDataCleanableList.forEach { it.resetForUser(userId) }
@Singleton
class AccountUtils @Inject constructor(
    @ApplicationContext context: Context,
) : UserAccountUtils(context, MainApplication.userDataCleanableList) {
    // suspend fun init() {
    //     currentUserFlow.collect { user ->
    //         Sentry.setUser(SentryUser().apply {
    //             id = user?.id?.toString() ?: "-1"
    //             email = user?.email
    //         })
    //     }
    // }
}
