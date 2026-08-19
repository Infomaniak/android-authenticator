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
import com.infomaniak.multiplatform_authenticator.core.room.appsettings.AppSettingsDatabase
import com.infomaniak.auth.service.DeviceInfoUpdateWorker
import com.infomaniak.auth.utils.AccountUtils
import com.infomaniak.auth.utils.NotificationUtils
import com.infomaniak.core.auth.AuthConfiguration
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
import splitties.init.injectAsAppCtx
import javax.inject.Inject

@HiltAndroidApp
open class MainApplication : Application(), Configuration.Provider {
    @Inject
    lateinit var accountUtils: AccountUtils

    @Inject
    lateinit var notificationUtils: NotificationUtils

    @Inject
    lateinit var db: AppSettingsDatabase // Workaround to ensure it's initialized eagerly, before StrictMode is activated.

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    protected val applicationScope = CoroutineScope(Dispatchers.Default + CoroutineName("MainApplication"))
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    init {
        injectAsAppCtx()
        NetworkConfiguration.init(
            appId = BuildConfig.APPLICATION_ID,
            appVersionName = BuildConfig.VERSION_NAME,
            appVersionCode = BuildConfig.VERSION_CODE,
            apiEnvironment = ApiEnvironment.Prod,
        )
        AuthConfiguration.init(
            appId = BuildConfig.APPLICATION_ID,
            appVersionCode = BuildConfig.VERSION_CODE,
            appVersionName = BuildConfig.VERSION_NAME,
            clientId = BuildConfig.CLIENT_ID,
        )
        userDataCleanableList = listOf<AssociatedUserDataCleanable>(DeviceInfoUpdateManager)
    }

    override fun onCreate() {
        super.onCreate()
        setupStrictMode()
        notificationUtils.initNotificationChannel()
        applicationScope.launch {
            val sentryPreferences = SentryPreferences()
            configureSentry(isDebug = BuildConfig.DEBUG, isSentryTrackingEnabled = { sentryPreferences.isSentryAuthorized })
            DeviceInfoUpdateManager.scheduleWorkerOnDeviceInfoUpdate<DeviceInfoUpdateWorker>()
        }
    }

    open fun registerUserDeviceIfNeeded() {}

    companion object {
        @JvmStatic
        var userDataCleanableList: List<AssociatedUserDataCleanable> = emptyList()
            protected set
    }
}
