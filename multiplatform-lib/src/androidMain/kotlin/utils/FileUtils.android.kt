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
package com.infomaniak.auth.lib.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.invoke
import splitties.init.appCtx
import java.io.File

internal actual suspend fun createFolder(name: String) = Dispatchers.IO {
    val folder = File(appCtx.filesDir, name)
    if (!folder.exists()) {
        folder.mkdirs()
    }
}

internal actual suspend fun createFileIn(folder: String, name: String): Unit = Dispatchers.IO {
    val folder = File(appCtx.filesDir, folder)
    File(folder, name).createNewFile()
}

internal actual suspend fun checkFileExists(folder: String, name: String): Boolean = Dispatchers.IO {
    val folder = File(appCtx.filesDir, folder)
    File(folder, name).exists()
}

internal actual suspend fun checkFolderExists(folder: String): Boolean = Dispatchers.IO {
    File(appCtx.filesDir, folder).exists()
}
