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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.infomaniak.auth.BuildConfig
import com.infomaniak.auth.R
import com.infomaniak.multiplatform_authenticator.core.matomo.MatomoScreen
import com.infomaniak.auth.utils.MatomoTrackScreen
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

@Composable
fun PrivacyManagementScreen(
    navigateToTrackerPage: (Tracker) -> Unit,
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MatomoTrackScreen(MatomoScreen.PrivacyManagementScreen)

    SinglePaneScaffold(
        modifier = modifier,
        topBar = {
            InfomaniakAuthenticatorTopAppBar(
                withTitle = false,
                isCentered = false,
                onBackPressed = onBackPressed
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Margin.Medium),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(stringResource(R.string.dataManagementTitle), style = MaterialTheme.typography.titleLarge)
            }
            PrivacyManagementHomeContent(
                contentPadding = PaddingValues(horizontal = Margin.Medium),
                sourceUrl = BuildConfig.GITHUB_REPO_URL,
                trackerList = persistentListOf(Tracker.Sentry, Tracker.Matomo),
                header = {
                    Image(
                        imageVector = AppIllus.DataProtection.image(),
                        contentDescription = null,
                        modifier = Modifier.padding(Margin.Medium),
                    )
                },
                divider = {
                    HorizontalDivider(
                        color = AuthenticatorTheme.materialColors.outlineVariant,
                    )
                },
                rightIcon = {
                    Icon(
                        painter = painterResource(R.drawable.chevron_right),
                        contentDescription = null,
                    )
                },
                onTrackerClick = navigateToTrackerPage
            )
        }
    }
}

@PreviewSmallWindow
@Composable
private fun PrivacyManagementScreenPreview() {
    AuthenticatorTheme {
        PrivacyManagementScreen(
            navigateToTrackerPage = {},
            onBackPressed = {},
        )
    }
}
