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

import kotlin.test.Test

class SigningTest : SigningTestBase() {

    @Test
    fun `__this is a test class with tests in the super class`() {}

    override fun getKeyPair(): Pair<ByteArray, ByteArray> {
        val keyPair = generateEcKeyPair().getOrThrow()
        return keyPair.private.encoded to keyPair.public.encoded
    }

    override fun getTestDataSet(): List<TestData> = listOf(
        TestData(
            privateKey = "3041020100301306072a8648ce3d020106082a8648ce3d0301070427302502010104208e6a72ffa2594ad40a338d943925a8512690127a07488e839630f6cd96556cc0".hexToByteArray(),
            publicKey = "3059301306072a8648ce3d020106082a8648ce3d0301070342000403f2c76fea988e5fee5fb1c9c7097b8e4e094813043065e4f6e1a8b7170aedce9fafbc81d2acab90037f0d201a5253681a772c4e6d1bde2141c9a43ed61b8dfa".hexToByteArray(),
            dataToSign = "4c4f4c".hexToByteArray(),
            signature = "3045022100eb6c66fbc84920cc153e3c1090a397f5b1cb4aa0c190a4d0ad268e9015c1df260220320675c44cf0decc1578818682137603bf169a98676118cef8a89f9002543563".hexToByteArray(),
        )
    )
}
