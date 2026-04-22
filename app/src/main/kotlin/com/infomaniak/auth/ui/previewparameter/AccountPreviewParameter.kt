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
package com.infomaniak.auth.ui.previewparameter

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.infomaniak.auth.lib.Account
import com.infomaniak.auth.lib.Issue
import com.infomaniak.core.auth.models.user.User
import com.infomaniak.core.ui.compose.preview.previewparameter.dummyUserOf
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList

class AccountPreviewParameter : PreviewParameterProvider<Pair<Account, User>> {
    override val values: Sequence<Pair<Account, User>> = fakeAccountPairs.asSequence()
}

val fakeAccounts = persistentListOf(
    Account(
        id = 0,
        fullName = "John Smith",
        initials = "JS",
        email = "john.smith@ik.me",
        avatarUrl = null,
        status = Account.Status.LoggedIn,
    ),
    Account(
        id = 1,
        fullName = "John Issue big name to take place",
        initials = "JS",
        email = "john.smith.apple.lol.infomaniak@ik.me",
        avatarUrl = null,
        status = Account.Status.NotConnected.AttemptingToConnect,
    ),
    Account(
        id = 2,
        fullName = "John Issue ReLogin",
        initials = "JS",
        email = "john.smith.relogin@ik.me",
        avatarUrl = null,
        status = Account.Status.NotConnected.ReLogin(
            legacyAccount = Account(
                id = 2,
                fullName = "John Issue ReLogin",
                initials = "JS",
                email = "john.smith.relogin@ik.me",
                status = Account.Status.NotConnected.AttemptingToConnect,
            ),
            state = Account.Status.NotConnected.ReLogin.State.SendingCredentials
        ),
    ),
    Account(
        id = 3,
        fullName = "John Relogin",
        initials = "JS",
        email = "john.smith@ik.me",
        avatarUrl = null,
        status = Account.Status.NotConnected.LoginFailed(Issue.NonRetriable("Preview error")),
    )
)

val fakeAccountPairs = fakeAccounts
    .map {
        it to dummyUserOf(it.id.toInt(), it.fullName.split(" ")[0], it.fullName.split(" ")[1])
    }
    .toPersistentList()
