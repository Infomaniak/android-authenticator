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
package com.infomaniak.auth.ui.screen.settings.privacymanagement

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.infomaniak.auth.MatomoAuthenticator
import com.infomaniak.core.privacymanagement.tracker.Tracker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@Composable
fun PrivacyManagementMatomoScreen(viewModel: PrivacyManagementMatomoViewModel = hiltViewModel()) {
    val isTrackerEnabled by viewModel.isTrackerEnabled.collectAsStateWithLifecycle()

    PrivacyManagementTrackerScreen(
        tracker = Tracker.Matomo,
        isTrackerEnabled = { isTrackerEnabled },
        onTrackerSwitchClick = viewModel::onTrackerSwitchClick
    )
}

@HiltViewModel
class PrivacyManagementMatomoViewModel @Inject constructor() : ViewModel() {
    private val _isTrackerEnabled = MutableStateFlow(!MatomoAuthenticator.tracker.isOptOut)
    val isTrackerEnabled: StateFlow<Boolean> = _isTrackerEnabled.asStateFlow()

    fun onTrackerSwitchClick(isCheck: Boolean) {
        MatomoAuthenticator.tracker.isOptOut = !isCheck
        _isTrackerEnabled.value = isCheck
    }
}
