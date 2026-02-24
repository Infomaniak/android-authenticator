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
package com.infomaniak.auth.ui.screen.main

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.infomaniak.auth.lib.repository.AppSettingsRepository
import com.infomaniak.auth.lib.room.appsettings.Theme
import com.infomaniak.auth.ui.navigation.NavDestination
import com.infomaniak.auth.ui.theme.AuthenticatorTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    @Inject
    lateinit var appSettingsRepository: AppSettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splashscreen = installSplashScreen()
        splashscreen.setKeepOnScreenCondition { viewModel.isAppLoading.value }

        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= 29) window.isNavigationBarContrastEnforced = false

        setContent {
            val isSystemInDarkTheme = isSystemInDarkTheme()
            val appSettings by appSettingsRepository.getSettings().collectAsStateWithLifecycle(initialValue = null)

            val isLoading by viewModel.isAppLoading.collectAsStateWithLifecycle()
            val isUserConnected by viewModel.isUserConnected.collectAsStateWithLifecycle()

            val isDarkTheme = when (appSettings?.theme) {
                Theme.Light -> false
                Theme.Dark -> true
                else -> isSystemInDarkTheme
            }

            LaunchedEffect(viewModel) {
                viewModel.checkUserConnected()
            }

            if (!isLoading) {
                AuthenticatorTheme(isDarkTheme = isDarkTheme) {
                    MainScreen(startDestination = if (isUserConnected) NavDestination.Root.Home else NavDestination.Onboarding.Start)
                }
            }
        }
    }
}
