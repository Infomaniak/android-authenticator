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
package com.infomaniak.auth

import com.infomaniak.auth.lib.RegisterPasskeyBuilder
import com.infomaniak.auth.lib.internal.KeyPairManagerImpl
import com.infomaniak.auth.lib.network.models.PasskeysOptions
import com.infomaniak.auth.lib.network.models.PubKeyCredParam
import com.infomaniak.auth.lib.network.models.RelyingParty
import com.infomaniak.auth.lib.network.models.User
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.ExperimentalSerializationApi
import kotlin.test.Test

class WebAuthnTest {

    @OptIn(ExperimentalSerializationApi::class)
    @Test
    fun registerPasskeyGeneration() = runTest {
        // This is sent from [GET] /api/authenticator/passkeys/options
        val passkeysOptions = PasskeysOptions(
            challenge = "U3NkRnF6RlVwUnpKRGhVMw",
            relyingParty = RelyingParty(
                id = "infomaniak.com",
                name = "Infomaniak",
                icon = null,
            ),
            user = User(
                id = "MQ",
                name = "test@user.com",
                displayName = "Test"
            ),
            pubKeyCredParams = listOf(
                PubKeyCredParam(
                    type = "public-key",
                    algorithm = -7 // ES256
                )
            ),
            excludeCredentials = emptyList(),
        )

        // Just getting the public key to generate RegisterPasskey object
        val keyPairManager = KeyPairManagerImpl()
        keyPairManager.generateNewKey()
        val publicKeyAsByteArray = keyPairManager.retrievePublicKey().firstOrNull()!!

        // Nothing to test on the generated object for now
        RegisterPasskeyBuilder(passkeysOptions, publicKeyAsByteArray).build()
    }
}
