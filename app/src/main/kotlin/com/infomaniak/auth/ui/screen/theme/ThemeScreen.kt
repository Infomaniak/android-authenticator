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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.infomaniak.auth.R
import com.infomaniak.auth.room.Theme
import com.infomaniak.auth.ui.components.InfomaniakAuthenticatorTopAppBar
import com.infomaniak.auth.ui.components.OptionItem
import com.infomaniak.auth.ui.components.OptionsSection
import com.infomaniak.auth.ui.theme.AuthenticatorTheme
import com.infomaniak.core.ui.compose.bottomstickybuttonscaffolds.SinglePaneScaffold
import com.infomaniak.core.ui.compose.preview.PreviewSmallWindow

@Composable
fun ThemeScreen(
    onBackPressed: () -> Unit,
    themeViewModel: AppSettingsViewModel = hiltViewModel<AppSettingsViewModel>(),
) {
    val uiState by themeViewModel.uiState.collectAsStateWithLifecycle(null)

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
            OptionItem.WithSelection(
                stringResId = R.string.themeLight,
                isSelected = uiState?.theme == Theme.LIGHT,
                onClick = { themeViewModel.setTheme(Theme.LIGHT) },
            ),
            OptionItem.WithSelection(
                stringResId = R.string.themeDark,
                isSelected = uiState?.theme == Theme.DARK,
                onClick = { themeViewModel.setTheme(Theme.DARK) },
            ),
            OptionItem.WithSelection(
                stringResId = R.string.themeSystem,
                isSelected = uiState?.theme == Theme.SYSTEM,
                onClick = { themeViewModel.setTheme(Theme.SYSTEM) },
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
