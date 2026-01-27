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

internal interface KeysManager {

    /**
     * Generates all the keys we can for a new registration
     * (migrating from kAuth v1 or a backup, or a fresh new login)
     *
     * Will generate keys for these:
     * - [KeyReference.BasicOperations]
     * - [KeyReference.SensitiveOperations] (up to 4 keys)
     * - [KeyReference.ActivationFromBackup]
     */
    suspend fun generateNewKeys()
}
