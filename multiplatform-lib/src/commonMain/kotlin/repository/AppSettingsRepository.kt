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
package com.infomaniak.auth.lib.repository

import com.infomaniak.auth.lib.room.appsettings.AppSettingsDatabase
import com.infomaniak.auth.lib.room.appsettings.AppSettingsEntity
import com.infomaniak.auth.lib.room.appsettings.Theme
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest

class AppSettingsRepository(database: AppSettingsDatabase) {
    private val dao = database.getDao()

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getSettings(): Flow<AppSettingsEntity?> = dao.getAsFlow().flatMapLatest { appSettings ->
        if (appSettings == null) dao.save(AppSettingsEntity())
        dao.getAsFlow()
    }

    suspend fun setIsAppLockEnabled(isAppLockEnabled: Boolean) {
        dao.setIsAppLockEnabled(isAppLockEnabled)
    }

    suspend fun setTheme(theme: Theme) {
        dao.setTheme(theme)
    }
}
