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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.infomaniak.auth.lib.Account
import com.infomaniak.core.avatar.components.Avatar
import com.infomaniak.core.avatar.getBackgroundColorResBasedOnId
import com.infomaniak.core.avatar.models.AvatarColors
import com.infomaniak.core.avatar.models.AvatarType
import com.infomaniak.core.avatar.models.AvatarUrlData
import com.infomaniak.core.coil.ImageLoaderProvider

@Composable
fun Avatar(account: Account, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val unauthenticatedImageLoader = remember(context) { ImageLoaderProvider.newImageLoader(context) }

    Avatar(
        avatarType = AvatarType.getUrlOrInitials(
            avatarUrlData = account.avatarUrl?.let { AvatarUrlData(it, unauthenticatedImageLoader) },
            initials = account.initials,
            colors = AvatarColors(
                containerColor = Color(context.getBackgroundColorResBasedOnId(account.id.toInt())),
                contentColor = Color.White,
            ),
        ),
        modifier = modifier
    )
}
