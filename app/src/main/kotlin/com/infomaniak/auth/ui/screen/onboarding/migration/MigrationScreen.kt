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
package com.infomaniak.auth.ui.screen.onboarding.migration

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.infomaniak.auth.R
import com.infomaniak.auth.lib.Account
import com.infomaniak.auth.lib.matomo.MatomoScreen
import com.infomaniak.auth.utils.MatomoTrackScreen
import com.infomaniak.auth.ui.components.EmptyElement
import com.infomaniak.auth.ui.components.IllustrationWithHalo
import com.infomaniak.auth.ui.components.InfomaniakAuthenticatorTopAppBar
import com.infomaniak.auth.ui.components.LargeButton
import com.infomaniak.auth.ui.components.TitleAndDescription
import com.infomaniak.auth.ui.images.AppImages
import com.infomaniak.auth.ui.images.illus.gridTilesWithAuthenticator.GridTilesWithAuthenticator
import com.infomaniak.auth.ui.previewparameter.fakeAccountPairs
import com.infomaniak.auth.ui.screen.onboarding.migration.component.MigrationListAccounts
import com.infomaniak.auth.ui.screen.onboarding.migration.component.MigrationSelectAccounts
import com.infomaniak.auth.ui.theme.AuthenticatorTheme
import com.infomaniak.core.ui.compose.basics.bottomsheet.ThemedBottomSheetScaffold
import com.infomaniak.core.ui.compose.bottomstickybuttonscaffolds.BottomStickyButtonScaffold
import com.infomaniak.core.ui.compose.margin.Margin
import com.infomaniak.core.ui.compose.preview.PreviewSmallWindow

@Composable
fun MigrationScreen(
    viewModel: MigrationViewModel = hiltViewModel(),
) {
    val accounts by viewModel.accounts.collectAsStateWithLifecycle(emptyList())

    MatomoTrackScreen(MatomoScreen.MigrationScreen)

    MigrationScreen(
        accounts = { accounts },
        onContinue = viewModel::onContinue
    )
}

@Composable
fun MigrationScreen(
    accounts: () -> List<Account>,
    modifier: Modifier = Modifier,
    onContinue: () -> Unit,
) {
    var showAccountsBottomSheet by rememberSaveable { mutableStateOf(false) }

    BottomStickyButtonScaffold(
        modifier = modifier,
        topBar = {
            InfomaniakAuthenticatorTopAppBar()
        },
        bottomButton = { bottomModifier ->
            BottomButton(
                modifier = bottomModifier,
                accounts = accounts,
                onClick = { showAccountsBottomSheet = true },
                onContinue = onContinue
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            EmptyElement()
            IllustrationWithHalo(AppImages.AppIllus.GridTilesWithAuthenticator)
            TitleAndDescription(
                title = stringResource(R.string.onBoardingMigrationTitle),
                description = stringResource(R.string.onBoardingMigrationDescription)
            )
        }

        if (showAccountsBottomSheet) {
            AccountsBottomSheetDialog(accounts = accounts, close = { showAccountsBottomSheet = false })
        }
    }
}

@Composable
private fun BottomButton(
    accounts: () -> List<Account>,
    onClick: () -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Margin.Medium)
    ) {
        MigrationSelectAccounts(
            accounts = accounts,
            onClick = onClick,
        )
        LargeButton(
            modifier = Modifier.fillMaxWidth(),
            title = stringResource(R.string.continueButton),
            onClick = onContinue
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun AccountsBottomSheetDialog(
    accounts: () -> List<Account>,
    close: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()

    ThemedBottomSheetScaffold(sheetState = sheetState, onDismissRequest = close) {
        MigrationListAccounts(accounts = accounts)
    }
}

@PreviewSmallWindow
@Composable
private fun MigrationScreenPreview() {
    AuthenticatorTheme {
        MigrationScreen(
            accounts = { fakeAccountPairs.map { it.first } },
            onContinue = {},
        )
    }
}

