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
import com.infomaniak.auth.lib.NotConnectedAction
import kotlinx.collections.immutable.persistentListOf

class AccountPreviewParameter : PreviewParameterProvider<Account> {
    override val values: Sequence<Account> = fakeAccounts.asSequence()
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
        fullName = "John Issue",
        initials = "JS",
        email = "john.smith@ik.me",
        avatarUrl = null,
        status = Account.Status.NotConnected(action = null),
    ),
    Account(
        id = 3,
        fullName = "John Relogin",
        initials = "JS",
        email = "john.smith@ik.me",
        avatarUrl = null,
        status = Account.Status.NotConnected(action = NotConnectedAction.Issue.NonRetriable("Preview error")),
    )
)
