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

import com.infomaniak.auth.lib.internal.utils.Xor
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertNull
import kotlin.test.fail

class KeyPairManagerTest {

    @Test
    fun testKeyPairManager() {
        val keyPairManager = KeyPairManager()

        runTest {
            val userId = 12345L
            val keyId = "keyId"
            //NOTE: The default KeyChain is not available on headless simulators,
            // so we need to remove this test, or update it to use in-memory keys instead.
            // Right now, it just fails.
            val error = keyPairManager.generateNewKey(userId, keyId)
            assertNull(error)

            val publicKey = keyPairManager.retrievePublicKey(userId, keyId)
            when (publicKey) {
                is Xor.First -> Unit // OK
                is Xor.Second -> fail("Couldn't generate the key")
            }
        }
    }
}
