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

import java.security.KeyFactory
import java.security.Signature
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec

internal actual object SignUtils {

    actual fun signWithPrivateKey(privateKey: ByteArray, data: ByteArray): ByteArray {
        val keyFactory = KeyFactory.getInstance("EC")
        val keySpec = PKCS8EncodedKeySpec(privateKey)
        val key = keyFactory.generatePrivate(keySpec)

        val signature = Signature.getInstance("SHA256withECDSA")
        signature.initSign(key)
        signature.update(data)

        return signature.sign()
    }

    actual fun verifySignature(publicKey: ByteArray, data: ByteArray, signatureData: ByteArray): Boolean {
        val keyFactory = KeyFactory.getInstance("EC")
        val keySpec = X509EncodedKeySpec(publicKey)
        val key = keyFactory.generatePublic(keySpec)

        val signature = Signature.getInstance("SHA256withECDSA")
        signature.initVerify(key)
        signature.update(data)

        return signature.verify(signatureData)
    }
}
