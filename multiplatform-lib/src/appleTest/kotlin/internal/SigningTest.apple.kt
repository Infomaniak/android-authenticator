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
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Security.SecKeyCopyExternalRepresentation
import platform.Security.SecKeyCopyPublicKey
import kotlin.test.Test
import kotlin.test.fail

class SigningTest : SigningTestBase() {

    @Test
    fun `__this is a test class with tests in the super class`() {}

    override fun getKeyPair(): Pair<ByteArray, ByteArray> {
        val privateKey = generateEcPrivateKeyInMemory(
            tag = "whatever",
            keyAccessGuard = KeyAccessGuard.Unguarded,
            accessibility = KeyAccessibility.AfterFirstUnlock.ThisDeviceOnly,
        ).firstOrElse { error -> fail("Error generating private key: $error") }

        val privateKeyData = tryIt { errorPtr ->
            SecKeyCopyExternalRepresentation(privateKey, errorPtr)
        }.firstOrElse { error -> fail("Error copying private key: $error") }.toNSData()

        val publicKey = SecKeyCopyPublicKey(privateKey) ?: fail("Failed to extract public key from private key")

        val publicKeyData = tryIt { errorPtr ->
            SecKeyCopyExternalRepresentation(publicKey, errorPtr)
        }.firstOrElse { error -> fail("Error copying public key: $error") }.toNSData()
        return privateKeyData.toByteArray() to publicKeyData.toByteArray()
    }

    override fun getTestDataSet(): List<TestData> = listOf(
        TestData(
            privateKey = "0480780672c97a37ea97b9a7e11ba1d4b8a0a55f7dcdf6f188312306c81b37c978f2d40b7f70b9213ece44606f73410ed42f7fa4d6d8ef3fc23d608b76edb2942a1ca57a786db47d4b9a50c9cc6df7b87c1ee07ce3d41c791afd6ad469917d3244".hexToByteArray(),
            publicKey = "0480780672c97a37ea97b9a7e11ba1d4b8a0a55f7dcdf6f188312306c81b37c978f2d40b7f70b9213ece44606f73410ed42f7fa4d6d8ef3fc23d608b76edb2942a".hexToByteArray(),
            dataToSign = "4c4f4c".hexToByteArray(),
            signature = "30450220205023db9fd540084a67f9439a858ebc3fd0b8b39380874d25f854868ad7bc4d022100be4669bc12a35bce32ae08d5f801e95363b58bf4502ecb916d386e8832ccf8bf".hexToByteArray(),
        )
    )
}
