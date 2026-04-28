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
package com.infomaniak.auth.lib.internal

import com.infomaniak.auth.lib.internal.utils.BackupExclusionGotcha
import com.infomaniak.auth.lib.internal.utils.checkFileExists
import com.infomaniak.auth.lib.internal.utils.createBackupExcludedFile
import kotlin.random.Random

internal object RestoreFromBackupDetector {

    private val restorationHandledMarkerFileName: String = "51756f69203f".hexToByteArray().decodeToString()

    suspend inline fun runRestoreOperationIfNeeded(block: () -> Unit) {
        val restorationAlreadyHandled = doesRestoreHandledFileExist()
        if (restorationAlreadyHandled) return
        block()
        markRestorationAsHandled()
    }

    private suspend fun doesRestoreHandledFileExist(): Boolean {
        return checkFileExists(restorationHandledMarkerFileName)
    }

    private suspend fun markRestorationAsHandled() {
        @OptIn(BackupExclusionGotcha::class)
        createBackupExcludedFile(name = restorationHandledMarkerFileName, content = generateFileContent())
    }

    private fun generateFileContent(): String {
        val oldEnough = Random.nextBoolean()
        val encodedContent = if (oldEnough) "466575722021" else "51756f69636f75626568"
        return encodedContent.hexToByteArray().decodeToString()
    }
}
