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
import com.infomaniak.auth.service.DeviceInfoUpdateWorker
import com.infomaniak.core.common.AssociatedUserDataCleanable
import com.infomaniak.core.crossapplogin.back.internal.deviceinfo.DeviceInfoUpdateManager
import com.infomaniak.core.network.NetworkConfiguration
import com.infomaniak.core.sentry.SentryConfig.configureSentry
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltAndroidApp
class MainApplication : Application() {
    val applicationScope = CoroutineScope(Dispatchers.Default + CoroutineName("MainApplication"))

    override fun onCreate() {
        super.onCreate()

        NetworkConfiguration.init(
            appId = BuildConfig.APPLICATION_ID,
            appVersionName = BuildConfig.VERSION_NAME,
            appVersionCode = BuildConfig.VERSION_CODE,
        )

        configureSentry(isDebug = BuildConfig.DEBUG, isSentryTrackingEnabled = true)

        userDataCleanableList = listOf<AssociatedUserDataCleanable>(DeviceInfoUpdateManager)
        applicationScope.launch {
            DeviceInfoUpdateManager.scheduleWorkerOnDeviceInfoUpdate<DeviceInfoUpdateWorker>()
        }
    }

    companion object {
        @JvmStatic
        var userDataCleanableList: List<AssociatedUserDataCleanable> = emptyList()
            protected set
    }
}
