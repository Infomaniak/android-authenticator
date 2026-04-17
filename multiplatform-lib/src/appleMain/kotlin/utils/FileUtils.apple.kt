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

import com.infomaniak.auth.lib.internal.utils.getApplicationSupportDirectory
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSFileManager
import platform.Foundation.NSNumber
import platform.Foundation.NSURL
import platform.Foundation.NSURLIsExcludedFromBackupKey
import platform.Foundation.numberWithBool


@OptIn(ExperimentalForeignApi::class)
actual suspend fun createFolder(name: String) {
    val basePath = getApplicationSupportDirectory()
    val folderPath = "$basePath/$name"

    // Create the folder if it doesn't exist
    NSFileManager.defaultManager.createDirectoryAtPath(
        path = folderPath,
        withIntermediateDirectories = true,
        attributes = null,
        error = null,
    )

    // Exclude folder from iCloud backup
    val folderUrl = NSURL.fileURLWithPath(folderPath)
    folderUrl.setResourceValue(
        value = NSNumber.numberWithBool(true),
        forKey = NSURLIsExcludedFromBackupKey,
        error = null
    )
}

@OptIn(ExperimentalForeignApi::class)
actual suspend fun createFileIn(folder: String, name: String) {
    val basePath = getApplicationSupportDirectory()
    val folderPath = "$basePath/$folder"

    NSFileManager.defaultManager.createDirectoryAtPath(
        path = folderPath,
        withIntermediateDirectories = true,
        attributes = null,
        error = null
    )

    // Create empty file
    NSFileManager.defaultManager.createFileAtPath(
        path = "$folderPath/$name",
        contents = null,
        attributes = null
    )
}

actual suspend fun checkFileExists(folder: String, name: String): Boolean {
    val basePath = getApplicationSupportDirectory()
    val filePath = "$basePath/$folder/$name"

    return NSFileManager.defaultManager.fileExistsAtPath(filePath)
}

@OptIn(ExperimentalForeignApi::class)
actual suspend fun checkFolderExists(folder: String): Boolean {
    val basePath = getApplicationSupportDirectory()
    val filePath = "$basePath/$folder"

    return NSFileManager.defaultManager.fileExistsAtPath(filePath)
}
