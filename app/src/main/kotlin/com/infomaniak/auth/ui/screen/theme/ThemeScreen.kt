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
package com.infomaniak.auth.ui.screen.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import com.infomaniak.auth.R
import com.infomaniak.auth.ui.components.InfomaniakAuthenticatorTopAppBar
import com.infomaniak.auth.ui.components.OptionItem
import com.infomaniak.auth.ui.components.OptionsSection
import com.infomaniak.auth.ui.theme.AuthenticatorTheme
import com.infomaniak.core.ui.compose.bottomstickybuttonscaffolds.SinglePaneScaffold
import com.infomaniak.core.ui.compose.preview.PreviewSmallWindow

@Composable
fun ThemeScreen(onBackPressed: () -> Unit) {
    SinglePaneScaffold(
        topBar = {
            InfomaniakAuthenticatorTopAppBar(
                withTitle = false,
                isCentered = false,
                isBackgroundTransparent = true,
                onBackPressed = { onBackPressed() }
            )
        },
    ) { paddingValues ->
        val section = listOf(
            OptionItem.WithRightIcon(
                stringResId = R.string.themeLight,
                rightIconResId = R.drawable.right_indicator,
                onClick = {},
            ),
            OptionItem.WithRightIcon(
                stringResId = R.string.themeDark,
                rightIconResId = R.drawable.right_indicator,
                onClick = {},
            ),
            OptionItem.WithRightIcon(
                stringResId = R.string.themeSystem,
                rightIconResId = R.drawable.right_indicator,
                onClick = {},
            ),
        )

        OptionsSection(
            modifier = Modifier
                .background(AuthenticatorTheme.materialColors.inverseOnSurface)
                .padding(paddingValues),
            section
        )
    }
}

@PreviewSmallWindow
@Composable
fun ThemeScreenPreview() {
    AuthenticatorTheme {
        ThemeScreen(onBackPressed = {})
    }

}
