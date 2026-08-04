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
@file:OptIn(ExperimentalCoroutinesApi::class)

package com.infomaniak.auth.ui.screen.main

import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.infomaniak.multiplatform_authenticator.core.AppStatus
import com.infomaniak.multiplatform_authenticator.core.repository.AppSettingsRepository
import com.infomaniak.multiplatform_authenticator.core.room.appsettings.Theme
import com.infomaniak.auth.ui.applock.AppLockActivity
import com.infomaniak.auth.ui.navigation.NavDestination
import com.infomaniak.auth.ui.theme.AuthenticatorTheme
import com.infomaniak.auth.utils.UsersCleaner
import com.infomaniak.core.applock.AppLockManager
import com.infomaniak.core.crossapplogin.back.CrossAppLoginFacade
import com.infomaniak.core.inappreview.reviewmanagers.InAppReviewManager
import com.infomaniak.core.inappupdate.updatemanagers.InAppUpdateManager
import com.infomaniak.core.twofactorauth.back.TwoFactorAuthManager
import com.infomaniak.core.twofactorauth.front.TwoFactorAuthApprovalAutoManagedBottomSheet
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity(), InAppServiceManager {
    private val viewModel: MainViewModel by viewModels()

    @Inject
    lateinit var usersCleaner: UsersCleaner

    @Inject
    lateinit var appSettingsRepository: AppSettingsRepository

    @Inject
    lateinit var twoFactorAuthManager: TwoFactorAuthManager

    @Inject
    lateinit var crossAppLoginFacade: CrossAppLoginFacade

    override val inAppReviewManager by lazy { InAppReviewManager(this) }
    override val inAppUpdateManager by lazy { InAppUpdateManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { viewModel.appStatus.replayCache.isEmpty() }

        enableEdgeToEdge()
        if (SDK_INT >= 29) window.isNavigationBarContrastEnforced = false

        lifecycleScope.launch {
            usersCleaner.cleanOrphanUsers()
        }

        lifecycleScope.launch {
            inAppUpdateManager.isUpdateRequired
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect { isUpdateRequired ->
                    initAppUpdateManager(isUpdateRequired)
                }
        }

        initAppReviewManager()

        AppLockManager.scheduleLockIfNeeded(
            targetActivity = this,
            lockActivityCls = AppLockActivity::class,
            isAppLockEnabled = { viewModel.isAppLocked.first() }
        )

        setContent {
            val appSettings by appSettingsRepository.getSettings().collectAsState(null)
            val appStatus by viewModel.appStatus.collectAsState(null)

            val isDarkTheme = when (appSettings?.theme) {
                Theme.Light -> false
                Theme.Dark -> true
                else -> isSystemInDarkTheme()
            }

            TwoFactorAuthApprovalAutoManagedBottomSheet(
                twoFactorAuthManager = twoFactorAuthManager,
                isInDarkTheme = isDarkTheme,
            )

            if (appStatus != null) {
                AuthenticatorTheme(isDarkTheme = isDarkTheme) {
                    MainScreen(
                        viewModel = viewModel,
                        startDestination = if (appStatus is AppStatus.SetupComplete) {
                            NavDestination.Home
                        } else {
                            NavDestination.Onboarding.Start()
                        }
                    )
                }
            }
        }
        lifecycleScope.launch {
            keepCrossAppLoginActivatedWhileNeeded(viewModel.appStatus, viewModel.accounts)
        }
    }

    private suspend fun keepCrossAppLoginActivatedWhileNeeded(
        appStatus: SharedFlow<AppStatus>,
        accounts: Flow<List<Account>>
    ) {
        val stillTryingToConnectSomeAccount: Flow<Boolean> = accounts.map { accountList ->
            accountList.any { it.status is Account.Status.NotConnected.AttemptingToConnect }
        }
        val shouldBeActivatedFlow = appStatus.transformLatest { status ->
            when (status) {
                is AppStatus.LoginRequired, is AppStatus.LoggingIn, is AppStatus.AddingAnAccount -> emit(true)
                is AppStatus.EverythingReady, is AppStatus.SetupComplete -> emitAll(stillTryingToConnectSomeAccount)
            }
        }.distinctUntilChanged()
        shouldBeActivatedFlow.collectLatest { shouldBeActivated ->
            if (shouldBeActivated) crossAppLoginFacade.activateUpdates(this)
        }
    }
}
