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
package com.infomaniak.auth

import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.infomaniak.auth.lib.repository.AppSettingsRepository
import com.infomaniak.auth.lib.room.Theme
import com.infomaniak.auth.ui.screen.main.MainScreen
import com.infomaniak.auth.ui.theme.AuthenticatorTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var appSettingsRepository: AppSettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (SDK_INT >= 29) window.isNavigationBarContrastEnforced = false

        setContent {
            val isSystemInDarkTheme = isSystemInDarkTheme()
            val appSettings = appSettingsRepository.getSettings().collectAsStateWithLifecycle(initialValue = null)

            val isDarkTheme = when (appSettings.value?.theme) {
                Theme.Light -> false
                Theme.Dark -> true
                else -> isSystemInDarkTheme
            }

            AuthenticatorTheme(isDarkTheme = isDarkTheme) {
                MainScreen()
            }
        }
    }
}
