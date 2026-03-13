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

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.infomaniak.auth.ui.components.InfomaniakAuthenticatorTopAppBar
import com.infomaniak.auth.ui.theme.AuthenticatorTheme
import com.infomaniak.core.privacymanagement.screencontent.PrivacyManagementTrackerContent
import com.infomaniak.core.privacymanagement.tracker.Tracker
import com.infomaniak.core.privacymanagement.tracker.TrackerPreviewParameterProvider
import com.infomaniak.core.ui.compose.bottomstickybuttonscaffolds.SinglePaneScaffold
import com.infomaniak.core.ui.compose.preview.PreviewSmallWindow

@Composable
fun PrivacyManagementTrackerScreen(
    tracker: Tracker,
    isTrackerEnabled: () -> Boolean,
    onTrackerSwitchClick: (Boolean) -> Unit
) {
    SinglePaneScaffold(
        topBar = {
            InfomaniakAuthenticatorTopAppBar(isCentered = false)
        }
    ) { paddingValues ->
        PrivacyManagementTrackerContent(
            modifier = Modifier.padding(paddingValues),
            tracker = tracker,
            isTrackerEnabled = isTrackerEnabled,
            onTrackerSwitchClick = onTrackerSwitchClick
        )
    }
}

@Composable
@PreviewSmallWindow
private fun PrivacyManagementTrackerScreenPreview(
    @PreviewParameter(TrackerPreviewParameterProvider::class) tracker: Tracker
) {
    AuthenticatorTheme {
        PrivacyManagementTrackerScreen(
            tracker = tracker,
            isTrackerEnabled = { true },
            onTrackerSwitchClick = {},
        )
    }
}
