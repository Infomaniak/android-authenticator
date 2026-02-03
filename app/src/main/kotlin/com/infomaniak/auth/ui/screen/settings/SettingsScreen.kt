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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.infomaniak.auth.R
import com.infomaniak.auth.ui.components.DividerState
import com.infomaniak.auth.ui.components.InfomaniakAuthenticatorTopAppBar
import com.infomaniak.auth.ui.components.OptionItem
import com.infomaniak.auth.ui.components.OptionsSection
import com.infomaniak.auth.ui.theme.AuthenticatorTheme
import com.infomaniak.core.ui.compose.bottomstickybuttonscaffolds.SinglePaneScaffold
import com.infomaniak.core.ui.compose.margin.Margin
import com.infomaniak.core.ui.compose.preview.PreviewSmallWindow

@Composable
fun SettingsScreen() {
    val firstSectionItems = listOf<OptionItem>(
        OptionItem.WithCheckBox(
            stringResId = R.string.notificationsTitle,
            dividerState = DividerState(withUpperDivider = false, withLowerDivider = false)
        ),
        OptionItem.WithCheckBox(
            stringResId = R.string.unlockWithBiometrics,
            dividerState = DividerState(withUpperDivider = true, withLowerDivider = false)
        ),
    )
    val secondSectionItems = listOf<OptionItem>(
        OptionItem.WithRightIcon(
            stringResId = R.string.dataManagementTitle,
            dividerState = DividerState(withUpperDivider = false, withLowerDivider = false),
            rightIconResId = R.drawable.right_indicator
        ),
        OptionItem.WithRightIcon(
            stringResId = R.string.feedbackTitle,
            dividerState = DividerState(withUpperDivider = true, withLowerDivider = true),
            rightIconResId = R.drawable.square_arrow_up
        ),
        OptionItem.WithRightIcon(
            stringResId = R.string.contactSupportTitle,
            dividerState = DividerState(withUpperDivider = false, withLowerDivider = false),
            rightIconResId = R.drawable.right_indicator
        ),
    )
    SinglePaneScaffold(
        topBar = {
            InfomaniakAuthenticatorTopAppBar(isCentered = false, isBackgroundTransparent = true)
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AuthenticatorTheme.materialColors.inverseOnSurface)
                .padding(paddingValues)
                .padding(top = Margin.Large),
            verticalArrangement = Arrangement.spacedBy(Margin.Large)
        ) {
            OptionsSection(firstSectionItems, secondSectionItems)
        }
    }
}

@PreviewSmallWindow
@Composable
fun SettingsScreenPreview() {
    AuthenticatorTheme {
        SettingsScreen()
    }
}
