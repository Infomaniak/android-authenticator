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
package com.infomaniak.auth.ui.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.infomaniak.auth.R
import com.infomaniak.auth.ui.components.AuthenticatorFab
import com.infomaniak.auth.ui.components.InfomaniakAuthenticatorTopAppBar
import com.infomaniak.auth.ui.navigation.HomeSubDestination
import com.infomaniak.auth.ui.navigation.NavDestination
import com.infomaniak.auth.ui.navigation.homeEntryProvider
import com.infomaniak.auth.ui.navigation.tryPopLast
import com.infomaniak.auth.ui.theme.AuthenticatorTheme
import com.infomaniak.auth.ui.theme.defaultEnterAnimation
import com.infomaniak.auth.ui.theme.defaultExitAnimation
import com.infomaniak.core.ui.compose.bottomstickybuttonscaffolds.SinglePaneScaffold
import com.infomaniak.core.ui.compose.margin.Margin
import com.infomaniak.core.ui.compose.preview.PreviewSmallWindow


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    rootBackStack: NavBackStack<NavKey>,
    viewModel: HomeScreenViewModel = hiltViewModel(),
) {
    HomeScreen(
        rootBackStack = rootBackStack,
        onAddAccountClicked = viewModel::onAddAccountClicked,
    )
}

@Composable
fun HomeScreen(
    onAddAccountClicked: () -> Unit,
    rootBackStack: NavBackStack<NavKey>,
    modifier: Modifier = Modifier
) {
    val homeBackStack = rememberNavBackStack(HomeSubDestination.AccountList)
    var topAppBarTitleResId by remember { mutableIntStateOf(R.string.appCompleteName) }

    LaunchedEffect(homeBackStack.last()) {
        when (homeBackStack.last()) {
            is HomeSubDestination.AccountList -> topAppBarTitleResId = R.string.appCompleteName
            is HomeSubDestination.Settings -> topAppBarTitleResId = R.string.settingsTitle
        }
    }

    SinglePaneScaffold(
        modifier = modifier,
        topBar = {
            InfomaniakAuthenticatorTopAppBar(
                titleResId = topAppBarTitleResId,
                isCentered = false,
                isBackgroundTransparent = true,
            )
        },
        bottomBar = {
            AuthenticatorBottomBar(
                backStack = homeBackStack,
                onMyAccountsClicked = { homeBackStack.tryPopLast() },
                onSettingsClicked = {
                    if (homeBackStack.last() != HomeSubDestination.Settings) {
                        homeBackStack.add(HomeSubDestination.Settings)
                    }
                }
            )
        },
        floatingActionButton = {
            AuthenticatorFab(onClick = onAddAccountClicked)
        }
    ) { paddingValues ->
        NavDisplay(
            modifier = Modifier.padding(paddingValues),
            backStack = homeBackStack,
            entryProvider = homeEntryProvider(rootBackStack),
            transitionSpec = { defaultEnterAnimation },
            popTransitionSpec = { defaultExitAnimation },
            predictivePopTransitionSpec = { defaultExitAnimation },
        )
    }
}

@Composable
private fun AuthenticatorBottomBar(
    backStack: NavBackStack<NavKey>,
    onMyAccountsClicked: () -> Unit,
    onSettingsClicked: () -> Unit
) {
    NavigationBar {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Margin.Micro)
                .heightIn(min = 80.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            NavigationBarItem(
                selected = backStack.last() is HomeSubDestination.AccountList,
                onClick = onMyAccountsClicked,
                icon = { Icon(painterResource(R.drawable.accounts), null) },
                label = { Text(stringResource(R.string.accountsTitle)) },
            )
            NavigationBarItem(
                selected = backStack.last() is HomeSubDestination.Settings,
                onClick = onSettingsClicked,
                icon = { Icon(painterResource(R.drawable.settings), null) },
                label = { Text(stringResource(R.string.settingsTitle)) },
            )
        }
    }
}

@PreviewSmallWindow
@Composable
private fun HomeScreenPreview() {
    AuthenticatorTheme {
        HomeScreen(
            rootBackStack = rememberNavBackStack(NavDestination.Home),
            onAddAccountClicked = {},
        )
    }
}
