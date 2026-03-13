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
package com.infomaniak.auth

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.infomaniak.auth.data.preferences.SentryPreferences
import com.infomaniak.auth.manager.AccountUtils
import com.infomaniak.auth.service.DeviceInfoUpdateWorker
import com.infomaniak.core.common.AssociatedUserDataCleanable
import com.infomaniak.core.crossapplogin.back.internal.deviceinfo.DeviceInfoUpdateManager
import com.infomaniak.core.network.ApiEnvironment
import com.infomaniak.core.network.NetworkConfiguration
import com.infomaniak.core.sentry.SentryConfig.configureSentry
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class MainApplication : Application(), Configuration.Provider {
    @Inject
    lateinit var accountUtils: AccountUtils

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    private val applicationScope = CoroutineScope(Dispatchers.Default + CoroutineName("MainApplication"))
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    init {
        NetworkConfiguration.init(
            appId = BuildConfig.APPLICATION_ID,
            appVersionName = BuildConfig.VERSION_NAME,
            appVersionCode = BuildConfig.VERSION_CODE,
            apiEnvironment = ApiEnvironment.PreProd,
        )
    }

    override fun onCreate() {
        super.onCreate()

        userDataCleanableList = listOf<AssociatedUserDataCleanable>(DeviceInfoUpdateManager)
        applicationScope.launch {
            configureSentry(isDebug = BuildConfig.DEBUG, isSentryTrackingEnabled = SentryPreferences().isSentryAuthorized)
            DeviceInfoUpdateManager.scheduleWorkerOnDeviceInfoUpdate<DeviceInfoUpdateWorker>()
        }
    }

    companion object {
        @JvmStatic
        var userDataCleanableList: List<AssociatedUserDataCleanable> = emptyList()
            private set
    }
}
