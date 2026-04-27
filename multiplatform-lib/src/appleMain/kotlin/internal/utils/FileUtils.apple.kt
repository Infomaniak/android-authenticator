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
package com.infomaniak.auth.lib.internal.utils

import com.infomaniak.auth.lib.internal.extensions.firstOrElse
import com.infomaniak.auth.lib.internal.extensions.toNsData
import com.infomaniak.auth.lib.internal.extensions.tryIt2
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSApplicationSupportDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSURLIsExcludedFromBackupKey
import platform.Foundation.NSUserDomainMask

internal actual suspend fun checkFileExists(name: String): Boolean {
    return NSFileManager.defaultManager.fileExistsAtPath("${getApplicationSupportDirectory()}/$name")
}

@OptIn(ExperimentalForeignApi::class)
internal actual suspend fun createFile(name: String, content: String) {
    val path = "${getApplicationSupportDirectory()}/$name"
    NSFileManager.defaultManager.createFileAtPath(
        path = path,
        contents = content.toNsData(),
        attributes = null
    )
    val url = NSURL.fileURLWithPath(path)
    val _ = tryIt2 {
        url.setResourceValue(
            value = true,
            forKey = NSURLIsExcludedFromBackupKey,
            error = it
        )
    }.firstOrElse { error(it) }
}

@OptIn(ExperimentalForeignApi::class)
private fun getApplicationSupportDirectory(): String {
    val directory = tryIt2 {
        NSFileManager.defaultManager.URLForDirectory(
            directory = NSApplicationSupportDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = true,
            error = it,
        )
    }.firstOrElse { error(it) }
    return requireNotNull(directory.path) // No reason for it to be null given the code above.
}
