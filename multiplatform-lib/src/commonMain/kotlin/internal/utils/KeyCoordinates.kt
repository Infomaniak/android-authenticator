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

import com.infomaniak.auth.lib.internal.webauthn.PublicKeyXY

internal expect fun getKeyCoordinates(key: ByteArray): PublicKeyXY

internal fun keyCoordinatesOf(uncompressedP256Key: ByteArray): PublicKeyXY {

    require(uncompressedP256Key[0] == 0x04.toByte()) {
        "Invalid key type. Expected 0x04, but found ${uncompressedP256Key[0]}."
    }
    require(uncompressedP256Key.size == 65) {
        "Invalid key length. Expected 65 bytes, but found ${uncompressedP256Key.size} bytes."
    }

    val x = uncompressedP256Key.copyOfRange(1, 33)
    val y = uncompressedP256Key.copyOfRange(fromIndex = 33, toIndex = 65)
    return PublicKeyXY(x, y)
}
