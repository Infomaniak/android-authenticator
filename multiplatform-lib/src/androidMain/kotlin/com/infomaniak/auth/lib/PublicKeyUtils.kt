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
package com.infomaniak.auth.lib

import java.security.KeyFactory
import java.security.PublicKey
import java.security.interfaces.ECPublicKey
import java.security.spec.X509EncodedKeySpec

actual object PublicKeyUtils : CommonPublicKeyUtils() {

    actual override fun getPublicKeyXY(publicKeyByteArray: ByteArray): PublicKeyXY {
        val publicKey = getPublicKeyFromByteArray(publicKeyByteArray) as ECPublicKey
        val w = publicKey.w
        val x = w.affineX.toByteArray().padTo32Bytes()
        val y = w.affineY.toByteArray().padTo32Bytes()

        return PublicKeyXY(x, y)
    }

    private fun getPublicKeyFromByteArray(bytes: ByteArray): PublicKey {
        val keySpec = X509EncodedKeySpec(bytes)
        val keyFactory = KeyFactory.getInstance("EC")
        return keyFactory.generatePublic(keySpec)
    }
}
