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

import com.infomaniak.auth.lib.internal.utils.SignUtils
import kotlin.test.Test
import kotlin.test.assertTrue

abstract class SigningTestBase {

    @Test
    fun `test and verify signature with test data`() {
        getTestDataSet().forEach { dataSet ->
            val isTestDataConsistent = SignUtils.verifySignature(
                publicKey = dataSet.publicKey,
                data = dataSet.dataToSign,
                signatureData = dataSet.signature
            )
            assertTrue(isTestDataConsistent, "Verification of the signature of test data failed")
            val result = SignUtils.verifySignature(
                publicKey = dataSet.publicKey,
                data = dataSet.dataToSign,
                signatureData = SignUtils.signWithPrivateKey(dataSet.privateKey, dataSet.dataToSign)
            )
            assertTrue(result, "Failed to verify the signature of data signed on-the-fly")
        }
    }

    @Test
    fun `test and verify signature with on-the-fly generated key`() {
        val (privateKey, publicKey) = getKeyPair()
        val someData = "Hello Kotlin Multiplatform!".encodeToByteArray()
        val signature = SignUtils.signWithPrivateKey(privateKey, someData)
        val result = SignUtils.verifySignature(publicKey, someData, signature)
        assertTrue(result)
    }

    /** Private key 1st, public key 2nd. */
    protected abstract fun getKeyPair(): Pair<ByteArray, ByteArray>

    protected abstract fun getTestDataSet(): List<TestData>

    protected class TestData(
        val privateKey: ByteArray,
        val publicKey: ByteArray,
        val dataToSign: ByteArray,
        val signature: ByteArray,
    )
}
