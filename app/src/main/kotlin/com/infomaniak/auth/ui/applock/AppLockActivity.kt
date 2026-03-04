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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.infomaniak.auth.ui.components.ButtonStyle
import com.infomaniak.auth.ui.components.IllustrationWithHalo
import com.infomaniak.auth.ui.components.InfomaniakAuthenticatorTopAppBar
import com.infomaniak.auth.ui.components.LargeButton
import com.infomaniak.auth.ui.images.AppImages
import com.infomaniak.auth.ui.images.illus.padlock.Padlock
import com.infomaniak.auth.ui.theme.AuthenticatorTheme
import com.infomaniak.core.applock.AppLockHelper.requestCredentials
import com.infomaniak.core.applock.compose.AppLockComposeActivity
import com.infomaniak.core.ui.compose.bottomstickybuttonscaffolds.BottomStickyButtonScaffold
import com.infomaniak.core.ui.compose.margin.Margin
import com.infomaniak.core.ui.compose.preview.PreviewSmallWindow
import com.infomaniak.core.applock.R as RAppLock

class AppLockActivity : AppLockComposeActivity() {
    @Composable
    override fun Content() {
        AppLockScreenContent(
            onUnlockClick = {
                requestCredentials { onCredentialsSuccessful() }
            }
        )
    }

    @Composable
    private fun AppLockScreenContent(onUnlockClick: () -> Unit = {}) {
        AuthenticatorTheme {
            BottomStickyButtonScaffold(topBar = {
                InfomaniakAuthenticatorTopAppBar()
            }, bottomButton = {
                LargeButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Margin.Medium)
                        .padding(bottom = Margin.Medium),
                    title = stringResource(RAppLock.string.buttonUnlock),
                    style = ButtonStyle.Primary,
                    onClick = onUnlockClick
                )
            }) {
                Column(
                    modifier = Modifier.fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    IllustrationWithHalo(
                        modifier = Modifier.weight(1f), themedImage = AppImages.AppIllus.Padlock
                    )
                }
            }
        }
    }

    @PreviewSmallWindow
    @Composable
    private fun AppLockContentPreview() {
        AppLockScreenContent()
    }
}
