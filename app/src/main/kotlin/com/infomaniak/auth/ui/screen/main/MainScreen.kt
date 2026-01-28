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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.infomaniak.auth.R
import com.infomaniak.auth.ui.components.FloatingActionButton
import com.infomaniak.auth.ui.navigation.NavDestination
import com.infomaniak.auth.ui.navigation.baseEntryProvider
import com.infomaniak.auth.ui.theme.AuthenticatorTheme
import com.infomaniak.core.ui.compose.bottomstickybuttonscaffolds.SinglePaneScaffold
import com.infomaniak.core.ui.compose.margin.Margin

@Composable
fun MainScreen() {
    // TODO: will change when back give the result of account status
    val startDestination = if (true) NavDestination.Onboarding.Start else NavDestination.Home
    val backStack = rememberNavBackStack(startDestination)

    SinglePaneScaffold(
        floatingActionButton = {
            if (backStack.last() == NavDestination.Home) FloatingActionButton(onClick = {})
        },
        bottomBar = {
            if (backStack.last() == NavDestination.Home || backStack.last() == NavDestination.Settings) {
                NavigationBar(
                    containerColor = AuthenticatorTheme.materialColors.surfaceContainerHighest,
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Margin.Micro)
                            .heightIn(min = 80.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        NavigationBarItem(
                            selected = backStack.last() == NavDestination.Home,
                            onClick = {
                                backStack.clear()
                                backStack.add(NavDestination.Home)
                            },
                            icon = { Icon(painterResource(R.drawable.home), null) },
                            label = { Text(stringResource(R.string.accountTitle)) },
                        )
                        NavigationBarItem(
                            selected = backStack.last() == NavDestination.Settings,
                            onClick = {
                                backStack.add(NavDestination.Settings)
                            },
                            icon = { Icon(painterResource(R.drawable.settings), null) },
                            label = { Text(stringResource(R.string.settingsTitle)) },
                        )
                    }
                }
            }
        }
    ) { _ ->
        NavDisplay(
            backStack = backStack,
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            entryProvider = baseEntryProvider(backStack)
        )
    }
}
