@file:OptIn(ExperimentalForeignApi::class)

import com.infomaniak.auth.lib.internal.KeyAccessControl
import com.infomaniak.auth.lib.internal.KeyAccessibility
import com.infomaniak.auth.lib.internal.KeyPurposes
import com.infomaniak.auth.lib.internal.Xor
import com.infomaniak.auth.lib.internal.generatePrivateKeyInTheSecureEnclave
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSError
import kotlin.test.Test
import kotlin.test.assertTrue

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

@OptIn(ExperimentalForeignApi::class)
actual fun lol() {
    generatePrivateKeyInTheSecureEnclave(
        tag = "tag",
        accessibility = KeyAccessibility.Always.ThisDeviceOnly,
        accessControl = KeyAccessControl.Unguarded
    )
}

private fun Xor<*, NSError>.assertSucceeded() = assertTrue(this is Xor.First)
private fun Xor<*, NSError>.assertFailed() = assertTrue(this is Xor.Second)

class Lol {

    @Test
    fun alwaysFails() {
        generatePrivateKeyInTheSecureEnclave(
            tag = "com.infomaniak.test",
            publicKeyPurposes = KeyPurposes(verifying = true),
            accessibility = KeyAccessibility.WhenUnlocked.ThisDeviceOnly,
            accessControl = KeyAccessControl.Unguarded
        ).also {
            if (it is Xor.Second) println(it.value)
        }.assertSucceeded()

    }
}
