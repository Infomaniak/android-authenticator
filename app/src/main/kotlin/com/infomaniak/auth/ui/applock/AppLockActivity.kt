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
package com.infomaniak.auth.ui.applock

import androidx.compose.runtime.Composable
import com.infomaniak.auth.ui.components.ButtonStyle
import com.infomaniak.auth.ui.components.IllustrationWithHalo
import com.infomaniak.auth.ui.components.InfomaniakAuthenticatorTopAppBar
import com.infomaniak.auth.ui.images.AppImages
import com.infomaniak.auth.ui.images.illus.padlock.Padlock
import com.infomaniak.auth.ui.theme.AppValues
import com.infomaniak.auth.ui.theme.AuthenticatorTheme
import com.infomaniak.core.applock.AppLockHelper.requestCredentials
import com.infomaniak.core.applock.compose.AppLockComposeActivity
import com.infomaniak.core.applock.compose.AppLockScaffold
import com.infomaniak.core.ui.compose.preview.PreviewSmallWindow

class AppLockActivity : AppLockComposeActivity() {
    @Composable
    override fun Content() {
        AuthenticatorTheme {
            AppLockScaffold(
                topBar = { InfomaniakAuthenticatorTopAppBar() },
                illustration = { IllustrationWithHalo(AppImages.AppIllus.Padlock) },
                buttonStyle = AppValues.ButtonStyle,
                buttonColors = ButtonStyle.Primary.colors(),
                onUnlock = { requestCredentials { onCredentialsSuccessful() } }
            )
        }
    }

    @PreviewSmallWindow
    @Composable
    private fun Preview() {
        Content()
    }
}
