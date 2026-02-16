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
package com.infomaniak.auth.ui.screen.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.infomaniak.auth.R
import com.infomaniak.auth.ui.components.InfomaniakAuthenticatorTopAppBar
import com.infomaniak.auth.ui.components.OptionItemType
import com.infomaniak.auth.ui.components.OptionsSection
import com.infomaniak.auth.ui.theme.AuthenticatorTheme
import com.infomaniak.core.ui.compose.bottomstickybuttonscaffolds.SinglePaneScaffold
import com.infomaniak.core.ui.compose.preview.PreviewSmallWindow

@Composable
fun SettingsScreen() {
    val firstSectionItems = listOf<OptionItemType>(
        OptionItemType.WithCheckBox(
            stringResId = R.string.notificationsTitle,
        ),
        OptionItemType.WithCheckBox(
            stringResId = R.string.unlockWithBiometrics,
        ),
        OptionItemType.WithRightIcon(
            stringResId = R.string.themeTitle,
            rightIconResId = R.drawable.right_indicator
        ),
    )
    val secondSectionItems = listOf<OptionItemType>(
        OptionItemType.WithRightIcon(
            stringResId = R.string.dataManagementTitle,
            rightIconResId = R.drawable.right_indicator
        ),
        OptionItemType.WithRightIcon(
            stringResId = R.string.feedbackTitle,
            rightIconResId = R.drawable.square_arrow_up
        ),
        OptionItemType.WithRightIcon(
            stringResId = R.string.contactSupportTitle,
            rightIconResId = R.drawable.right_indicator
        ),
    )
    SinglePaneScaffold(
        modifier = Modifier.background(AuthenticatorTheme.materialColors.inverseOnSurface),
        topBar = {
            InfomaniakAuthenticatorTopAppBar(isCentered = false, isBackgroundTransparent = true)
        },
    ) { paddingValues ->

        OptionsSection(
            firstSectionItems, secondSectionItems,
            modifier = Modifier.padding(paddingValues),
        )
    }
}

@PreviewSmallWindow
@Composable
fun SettingsScreenPreview() {
    AuthenticatorTheme {
        SettingsScreen()
    }
}
