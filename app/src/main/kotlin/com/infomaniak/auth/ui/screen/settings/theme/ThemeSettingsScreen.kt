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
import com.infomaniak.auth.lib.room.Theme
import com.infomaniak.auth.ui.components.InfomaniakAuthenticatorTopAppBar
import com.infomaniak.auth.ui.components.OptionItemType
import com.infomaniak.auth.ui.components.OptionsSection
import com.infomaniak.auth.ui.theme.AuthenticatorTheme
import com.infomaniak.auth.utils.GetSetCallbacks
import com.infomaniak.core.ui.compose.bottomstickybuttonscaffolds.SinglePaneScaffold
import com.infomaniak.core.ui.compose.preview.PreviewSmallWindow

@Composable
fun ThemeSettingsScreenWrapper(onBackPressed: () -> Unit) {
    val themeViewModel: AppSettingsViewModel = hiltViewModel<AppSettingsViewModel>()
    val uiState by themeViewModel.uiState.collectAsStateWithLifecycle()
    val theme = GetSetCallbacks(get = { uiState.theme }, set = { it?.let { themeViewModel.setTheme(it) } })

    ThemeSettingsScreen(theme, onBackPressed)
}

@Composable
fun ThemeSettingsScreen(
    theme: GetSetCallbacks<Theme?>,
    onBackPressed: () -> Unit,
) {
    SinglePaneScaffold(
        modifier = Modifier.background(AuthenticatorTheme.materialColors.inverseOnSurface),
        topBar = {
            InfomaniakAuthenticatorTopAppBar(
                withTitle = false,
                isCentered = false,
                isBackgroundTransparent = true,
                onBackPressed = onBackPressed
            )
        },
    ) { paddingValues ->
        val section = listOf(
            OptionItemType.WithSelection(
                leftIconResId = R.drawable.ic_theme_light,
                stringResId = R.string.themeLight,
                isSelected = theme.get() == Theme.Light,
                onClick = { theme.set(Theme.Light) },
            ),
            OptionItemType.WithSelection(
                leftIconResId = R.drawable.ic_theme_dark,
                stringResId = R.string.themeDark,
                isSelected = theme.get() == Theme.Dark,
                onClick = { theme.set(Theme.Dark) },
            ),
            OptionItemType.WithSelection(
                leftIconResId = R.drawable.ic_theme_system,
                stringResId = R.string.themeSystem,
                isSelected = theme.get() == Theme.System,
                onClick = { theme.set(Theme.System) },
            ),
        )

        OptionsSection(
            section,
            modifier = Modifier
                .padding(paddingValues),
        )
    }
}

@PreviewSmallWindow
@Composable
fun ThemeSettingsScreenPreview() {
    AuthenticatorTheme {
        ThemeSettingsScreen(
            theme = GetSetCallbacks(get = { Theme.Light }, set = {}),
            onBackPressed = {},
        )
    }
}
