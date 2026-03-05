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
package com.infomaniak.auth.lib.managers

import com.infomaniak.auth.lib.CryptoObjectsBuilder
import com.infomaniak.auth.lib.internal.KeyPairManagerImpl
import com.infomaniak.auth.lib.network.repositories.WebAuthnRepository

class AuthenticatorManager(private val webAuthnRepository: WebAuthnRepository) {

    suspend fun registerPasskey(token: String, userId: Int) {
        val passkeysOptions = webAuthnRepository.getPasskeysOptions(token).data
        val keyPairManager = KeyPairManagerImpl()
        // TODO generate ID of key here to use it in the key file name
        keyPairManager.generateNewKey() // TODO Pass UserId to generate the key with the right name
        val publicKeyAsByteArray = keyPairManager.retrievePublicKey().firstOrNull()!!

        // Nothing to test on the generated object for now
        val cryptoObjectsBuilder = CryptoObjectsBuilder(publicKeyAsByteArray)
        val registerPasskey = cryptoObjectsBuilder.buildRegisterPasskey(passkeysOptions)

        webAuthnRepository.registerPasskey(token, registerPasskey)
    }
}
