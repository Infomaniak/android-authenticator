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

//TODO[ik-auth-back]: Possibly move this to common code, to share with Android.
@ExposedCopyVisibility
internal data class KeyPurposes private constructor(
    val signing: Boolean,
    val deriving: Boolean,
    val verifying: Boolean,
    val encrypting: Boolean,
    val decrypting: Boolean,
    val wrapping: Boolean,
    val unwrapping: Boolean,
) {
    companion object {
        val privateKeyDefaults = forPrivateKey()
        val publicKeyDefaults = forPublicKey()

        operator fun invoke(
            signing: Boolean = false,
            deriving: Boolean = false,
            verifying: Boolean = false,
            encrypting: Boolean = false,
            decrypting: Boolean = false,
            wrapping: Boolean = false,
            unwrapping: Boolean = false,
        ): KeyPurposes = KeyPurposes(
            signing = signing,
            deriving = deriving,
            verifying = verifying,
            encrypting = encrypting,
            decrypting = decrypting,
            wrapping = wrapping,
            unwrapping = unwrapping,
        )

        fun forPrivateKey(
            signing: Boolean = true,
            decrypting: Boolean = true,
            unwrapping: Boolean = true,
            deriving: Boolean = true,
        ): KeyPurposes = KeyPurposes(
            signing = signing,
            decrypting = decrypting,
            unwrapping = unwrapping,
            deriving = deriving,
            verifying = false,
            encrypting = false,
            wrapping = false,
        )

        fun forPublicKey(
            verifying: Boolean = true,
            encrypting: Boolean = true,
            wrapping: Boolean = true,
            deriving: Boolean = true,
        ): KeyPurposes = KeyPurposes(
            verifying = verifying,
            encrypting = encrypting,
            wrapping = wrapping,
            deriving = deriving,
            signing = false,
            decrypting = false,
            unwrapping = false,
        )
    }
}
