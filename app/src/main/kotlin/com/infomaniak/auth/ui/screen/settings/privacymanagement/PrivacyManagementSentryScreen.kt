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

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.infomaniak.auth.data.preferences.SentryPreferences
import com.infomaniak.core.privacymanagement.tracker.Tracker
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@Composable
fun PrivacyManagementSentryScreen(viewModel: PrivacyManagementSentryViewModel = hiltViewModel()) {
    val isTrackerEnabled by viewModel.isTrackerEnabled.collectAsStateWithLifecycle()

    PrivacyManagementTrackerScreen(
        tracker = Tracker.Sentry,
        isTrackerEnabled = { isTrackerEnabled },
        onTrackerSwitchClick = viewModel::onTrackerSwitchClick
    )
}

@HiltViewModel
class PrivacyManagementSentryViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
) : ViewModel() {
    val isTrackerEnabled: StateFlow<Boolean> = flow {
        emitAll(SentryPreferences().isSentryAuthorizedFlow)
    }.stateIn(viewModelScope, SharingStarted.Lazily, false)

    fun onTrackerSwitchClick(isCheck: Boolean) {
        viewModelScope.launch {
            SentryPreferences().isSentryAuthorized = isCheck
        }
    }
}
