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
package com.infomaniak.auth.ui.screen.securingaccount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.infomaniak.auth.lib.Account
import com.infomaniak.auth.lib.AuthenticatorFacade
import com.infomaniak.auth.lib.NotConnectedAction
import com.infomaniak.core.sentry.SentryLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SecuringAccountViewModel @Inject constructor(
    private val authenticatorFacade: AuthenticatorFacade
) : ViewModel() {
    private var resolutionJob: Job? = null

    fun needsResolution() {
        resolutionJob?.cancel()
        resolutionJob = authenticatorFacade.accounts
            .flatMapLatest { accounts ->
                accounts.mapNotNull { it.status as? Account.Status.NotConnected }.asFlow()
            }
            .onEach { status ->
                when (val action = status.action) {
                    is NotConnectedAction.ReLogin -> {
                        // TODO[Authenticator]: Display relogin screen when migration ready
                    }
                    is NotConnectedAction.Issue.Retriable -> {
                        action.proceed(true)
                    }
                    is NotConnectedAction.Issue.NonRetriable -> {
                        SentryLog.e(TAG, action.message)
                    }
                    null -> Unit
                }
            }
            .launchIn(viewModelScope)
    }

    companion object {
        private val TAG = SecuringAccountViewModel::class.java.simpleName
    }
}
