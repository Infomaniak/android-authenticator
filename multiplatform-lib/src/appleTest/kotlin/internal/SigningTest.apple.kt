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
@file:OptIn(ExperimentalForeignApi::class)

package com.infomaniak.auth.lib.internal

import com.infomaniak.auth.lib.extensions.toByteArray
import com.infomaniak.auth.lib.extensions.toNSData
import com.infomaniak.auth.lib.extensions.tryIt
import com.infomaniak.auth.lib.utils.SignUtils
import io.ktor.utils.io.core.toByteArray
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Security.SecKeyCopyExternalRepresentation
import platform.Security.SecKeyCopyPublicKey
import kotlin.test.Test
import kotlin.test.fail

class SigningTest {

    @Test
    fun testSigning() {
        val privateKey = generateEcPrivateKeyInMemory(
            tag = "whatever",
            keyAccessGuard = KeyAccessGuard.Unguarded,
            accessibility = KeyAccessibility.AfterFirstUnlock.ThisDeviceOnly,
        ).firstOrElse { error -> fail("Error generating private key: $error") }

        val privateKeyData = tryIt { errorPtr ->
            SecKeyCopyExternalRepresentation(privateKey, errorPtr)
        }.firstOrElse { error -> fail("Error copying private key: $error") }.toNSData()

        println("Private key: $privateKeyData")

        val publicKey = SecKeyCopyPublicKey(privateKey) ?: fail("Failed to extract public key from private key")

        val publicKeyData = tryIt { errorPtr ->
            SecKeyCopyExternalRepresentation(publicKey, errorPtr)
        }.firstOrElse { error -> fail("Error copying public key: $error") }.toNSData()
        println("Public key: $publicKeyData")

        val someData = "LOL".toByteArray()
        println("Data to sign: $someData")
        val signature = SignUtils.signWithPrivateKey(privateKeyData.toByteArray(), someData)
        println("Signature: $signature")
        TODO("test with a hardcoded key pair and check the signature is correct")
    }
}
