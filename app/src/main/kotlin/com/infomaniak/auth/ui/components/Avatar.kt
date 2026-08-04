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
package com.infomaniak.auth.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import coil3.SingletonImageLoader
import com.infomaniak.multiplatform_authenticator.core.Account
import com.infomaniak.core.auth.models.user.User
import com.infomaniak.core.avatar.LocalAvatarColors
import com.infomaniak.core.avatar.components.Avatar
import com.infomaniak.core.avatar.getBackgroundColorResBasedOnId
import com.infomaniak.core.avatar.models.AvatarColors
import com.infomaniak.core.avatar.models.AvatarType
import com.infomaniak.core.avatar.models.AvatarType.Companion.getUrlOrInitials
import com.infomaniak.core.avatar.models.AvatarUrlData

@Composable
fun Avatar(
    account: Account,
    user: User?,
    modifier: Modifier = Modifier
) {
    Avatar(
        avatarType = computeAvatarType(account, user),
        modifier = modifier
    )
}

@Composable
fun Avatar(
    account: Account,
    modifier: Modifier = Modifier
) {
    Avatar(
        avatarType = computeAvatarType(account, user = null),
        modifier = modifier
    )
}

@Composable
private fun computeAvatarType(
    account: Account,
    user: User?
): AvatarType {
    return if (user == null) {
        val localAvatarColors = LocalAvatarColors.current
        val avatarColors = AvatarColors(
            containerColor = getBackgroundColorResBasedOnId(account.id.toInt(), localAvatarColors.containerColors),
            contentColor = localAvatarColors.contentColor,
        )
        val avatarUrlData = account.avatarUrl
            ?.takeIf { it.isNotBlank() }
            ?.let { avatarUrl ->
                AvatarUrlData(avatarUrl, SingletonImageLoader.get(LocalContext.current))
            }
        getUrlOrInitials(
            avatarUrlData = avatarUrlData,
            initials = account.initials,
            colors = avatarColors
        )
    } else {
        AvatarType.fromUser(user)
    }
}
