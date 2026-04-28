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

import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.infomaniak.auth.MatomoAuthenticator.trackSettingsEvent
import com.infomaniak.auth.R
import com.infomaniak.auth.lib.matomo.MatomoName
import com.infomaniak.auth.ui.components.OptionItemType
import com.infomaniak.auth.ui.components.OptionsSection
import com.infomaniak.auth.ui.screen.settings.theme.AppSettingsViewModel
import com.infomaniak.auth.ui.screen.settings.theme.SettingsUiState
import com.infomaniak.auth.ui.theme.AuthenticatorTheme
import com.infomaniak.auth.utils.GetSetCallbacks
import com.infomaniak.core.applock.AppLockHelper.requestCredentials
import com.infomaniak.core.applock.AppLockManager
import com.infomaniak.core.common.extensions.openUrl
import com.infomaniak.core.network.SUPPORT_URL
import com.infomaniak.core.ui.compose.preview.PreviewSmallWindow
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList

@Composable
fun SettingsScreen(
    onThemeClicked: () -> Unit,
    onPrivacyManagementClicked: () -> Unit,
    appSettingsViewModel: AppSettingsViewModel = hiltViewModel(),
) {
    val uiState by appSettingsViewModel.uiState.collectAsStateWithLifecycle(SettingsUiState())
    val hasBiometrics = remember { AppLockManager.hasBiometrics() }
    val appLocked = GetSetCallbacks(
        get = { uiState.isAppLocked },
        set = { appSettingsViewModel.setIsAppLockEnabled(it) }
    )

    SettingsScreen(appLocked, onThemeClicked, onPrivacyManagementClicked, hasBiometrics)
}

@Composable
private fun SettingsScreen(
    appLocked: GetSetCallbacks<Boolean>,
    onThemeClicked: () -> Unit,
    onPrivacyManagementClicked: () -> Unit,
    hasBiometrics: Boolean,
    modifier: Modifier = Modifier,
) {
    val fragmentActivity = LocalActivity.current as? FragmentActivity

    val firstSectionItems = buildList {
        add(
            OptionItemType.WithRightIcon(
                stringResId = R.string.manageNotifications,
                rightIconResId = R.drawable.square_arrow_up,
                onClick = {
                    fragmentActivity?.let {
                        Intent()
                            .apply {
                                action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                                putExtra(Settings.EXTRA_APP_PACKAGE, fragmentActivity.packageName)
                            }
                            .also(fragmentActivity::startActivity)
                    }
                }
            )
        )
        if (hasBiometrics) {
            add(
                OptionItemType.WithCheckBox(
                    stringResId = R.string.unlockWithBiometrics,
                    isChecked = appLocked.get(),
                    onCheckedChange = { newValue ->
                        trackSettingsEvent(MatomoName.ToggleBiometry)
                        fragmentActivity?.requestCredentials {
                            appLocked.set(newValue)
                            if (newValue) AppLockManager.unlock()
                        }
                    }
                )
            )
        }
        add(
            OptionItemType.WithRightIcon(
                stringResId = R.string.themeTitle,
                rightIconResId = R.drawable.chevron_right,
                onClick = { onThemeClicked() },
            )
        )
    }.toPersistentList()

    val secondSectionItems = persistentListOf(
        OptionItemType.WithRightIcon(
            stringResId = R.string.dataManagementTitle,
            rightIconResId = R.drawable.chevron_right,
            onClick = onPrivacyManagementClicked,
        ),
        OptionItemType.WithRightIcon(
            stringResId = R.string.feedbackTitle,
            rightIconResId = R.drawable.square_arrow_up,
            onClick = {
                fragmentActivity?.getString(R.string.urlUserReport)?.let { fragmentActivity.openUrl(it) }
            },
        ),
        OptionItemType.WithRightIcon(
            stringResId = R.string.contactSupportTitle,
            rightIconResId = R.drawable.square_arrow_up,
            onClick = {
                fragmentActivity?.openUrl(SUPPORT_URL)
            },
        ),
    )
    OptionsSection(
        modifier = modifier,
        sections = persistentListOf(firstSectionItems, secondSectionItems),
    )
}

@PreviewSmallWindow
@Composable
private fun SettingsScreenPreview() {
    AuthenticatorTheme {
        Scaffold { paddingValues ->
            SettingsScreen(
                modifier = Modifier.padding(paddingValues),
                appLocked = GetSetCallbacks(get = { true }, set = {}),
                onThemeClicked = {},
                onPrivacyManagementClicked = {},
                hasBiometrics = true,
            )
        }
    }
}
