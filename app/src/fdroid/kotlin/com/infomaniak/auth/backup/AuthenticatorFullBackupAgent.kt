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
package com.infomaniak.auth.backup

import android.app.backup.FullBackupDataOutput
import androidx.room.immediateTransaction
import androidx.room.useWriterConnection
import com.infomaniak.core.auth.models.user.User
import com.infomaniak.core.auth.room.UserDatabase
import com.infomaniak.core.common.backup.FullBackupAgent
import com.infomaniak.multiplatform_authenticator.core.PasskeysStorageLocation
import kotlinx.coroutines.runBlocking

class AuthenticatorFullBackupAgent : FullBackupAgent(RestorationPolicy.AllBackedUpFiles) {

    override fun onFullBackup(data: FullBackupDataOutput) {
        PasskeysStorageLocation.dir.listFiles().orEmpty().forEach { file ->
            fullBackupFile(file, data)
        }
        super.onFullBackup(data)
    }
}
