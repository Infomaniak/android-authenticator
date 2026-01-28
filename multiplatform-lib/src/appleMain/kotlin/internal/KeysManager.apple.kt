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

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Security.SecKeyRef

class KeysManagerImpl : KeysManager {

    override suspend fun generateNewKeys() {
        generateKey(whichOne = KeyReference.BasicOperations)
        generateKey(whichOne = KeyReference.SensitiveOperations.UserActionGuarded)
        generateKey(whichOne = KeyReference.SensitiveOperations.DevicePasscodeGuarded)
        generateKey(whichOne = KeyReference.SensitiveOperations.BiometricsGuarded.CurrentAndFuture)
        generateKey(whichOne = KeyReference.SensitiveOperations.BiometricsGuarded.CurrentOnly)
        TODO("Generate the KeyReference.ActivationFromBackup key")
    }

    //TODO[iOS-KeyChain]: Find keys with https://developer.apple.com/documentation/security/secitemcopymatching(_:_:)?language=objc
    //TODO[iOS-Secure-Enclave]: https://developer.apple.com/documentation/security/protecting-keys-with-the-secure-enclave?language=objc

}

@Throws(Exception::class)
private fun generateKey(whichOne: KeyReference.HardwareSecured): SecKeyRef {
    val result = generatePrivateKeyInTheSecureEnclave(
        tag = whichOne.toKeyTagOrAlias(),
        privateKeyPurposes = KeyPurposes.privateKeyDefaults, //TODO[ik-auth-back]: Potentially restrict it to signing only.
        publicKeyPurposes = KeyPurposes.publicKeyDefaults, ///TODO[ik-auth-back]: Potentially restrict it to verifying only.
        accessibility = accessibilityFor(whichOne),
        accessControl = accessControlFor(whichOne)
    )
    when (result) {
        is Xor.First -> return result.value
        is Xor.Second -> {
            val errorMessage = result.value.let { it.description ?: it.localizedDescription }
            throw Exception("Error generating $whichOne key: $errorMessage")
        }
    }
}

private fun accessibilityFor(
    reference: KeyReference.HardwareSecured
): KeyAccessibility.SecureEnclaveCompatible = when (reference) {
    KeyReference.BasicOperations -> KeyAccessibility.AfterFirstUnlock.ThisDeviceOnly
    is KeyReference.SensitiveOperations -> KeyAccessibility.WhenUnlocked.ThisDeviceOnly
}

private fun accessControlFor(
    reference: KeyReference.HardwareSecured
): KeyAccessControl = when (reference) {
    KeyReference.BasicOperations -> KeyAccessControl.Unguarded
    KeyReference.SensitiveOperations.BiometricsGuarded.CurrentAndFuture -> KeyAccessControl.Biometry.Any
    KeyReference.SensitiveOperations.BiometricsGuarded.CurrentOnly -> KeyAccessControl.Biometry.CurrentSet
    KeyReference.SensitiveOperations.DevicePasscodeGuarded -> KeyAccessControl.UserPresence // Also allow new biometrics.
    KeyReference.SensitiveOperations.UserActionGuarded -> KeyAccessControl.Unguarded // Hardware user action not available on iOS.
}
