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

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.infomaniak.auth.R
import com.infomaniak.auth.ui.components.InfomaniakAuthenticatorTopAppBar
import com.infomaniak.auth.ui.images.AppImages.AppIllus
import com.infomaniak.auth.ui.images.illus.dataProtection.DataProtection
import com.infomaniak.auth.ui.theme.AuthenticatorTheme
import com.infomaniak.core.privacymanagement.screencontent.PrivacyManagementHomeContent
import com.infomaniak.core.privacymanagement.tracker.Tracker
import com.infomaniak.core.ui.compose.bottomstickybuttonscaffolds.SinglePaneScaffold
import com.infomaniak.core.ui.compose.margin.Margin
import com.infomaniak.core.ui.compose.preview.PreviewSmallWindow
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList

@Composable
fun PrivacyManagementScreen(
    navigateToTrackerPage: (Tracker) -> Unit,
    modifier: Modifier = Modifier,
) {
    SinglePaneScaffold(
        modifier = modifier,
        topBar = {
            InfomaniakAuthenticatorTopAppBar(isCentered = false)
        }
    ) { paddingValues ->
        PrivacyManagementHomeContent(
            modifier = Modifier.padding(paddingValues),
            header = {
                Image(
                    imageVector = AppIllus.DataProtection.image(),
                    contentDescription = null,
                    modifier = Modifier.padding(Margin.Medium),
                )
            },
            trackerList = persistentListOf(Tracker.Sentry, Tracker.Matomo),
            divider = {
                HorizontalDivider(
                    color = AuthenticatorTheme.materialColors.outlineVariant,
                )
            },
            rightIcon = {
                Icon(
                    painter = painterResource(R.drawable.right_indicator),
                    contentDescription = null,
                )
            },
            onTrackerClick = navigateToTrackerPage
        )
    }
}

@PreviewSmallWindow
@Composable
private fun PrivacyManagementScreenPreview() {
    AuthenticatorTheme {
        PrivacyManagementScreen(
            navigateToTrackerPage = {},
        )
    }
}
