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

//TODO[ik-auth-back]: Possibly move this to common code, to share with Android.
internal data class KeyPurposes(
    val signing: Boolean = false,
    val deriving: Boolean = false,
    val verifying: Boolean = false,
    val encrypting: Boolean = false,
    val decrypting: Boolean = false,
    val wrapping: Boolean = false,
    val unwrapping: Boolean = false,
)
