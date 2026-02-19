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
package com.infomaniak.auth.lib.db

import androidx.room.Room
import androidx.room.RoomDatabase
import com.infomaniak.auth.lib.room.appsettings.AppSettingsDatabase
import com.infomaniak.auth.lib.room.appsettings.getAppSettingsRoomDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

fun getAppSettingsDatabaseBuilder(): RoomDatabase.Builder<AppSettingsDatabase> {
    val dbFilePath = documentDirectory() + "/app_settings.db"
    return Room.databaseBuilder<AppSettingsDatabase>(
        name = dbFilePath,
    )
}

fun getAppSettingsRoomDatabase(): AppSettingsDatabase {
    return getAppSettingsRoomDatabase(getAppSettingsDatabaseBuilder())
}

@OptIn(ExperimentalForeignApi::class)
private fun documentDirectory(): String {
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )
    return requireNotNull(documentDirectory?.path)
}
