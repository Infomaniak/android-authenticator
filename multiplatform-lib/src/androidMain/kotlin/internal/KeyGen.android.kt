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

import android.content.pm.PackageManager
import android.os.Build.VERSION.SDK_INT
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import splitties.bitflags.withFlag
import splitties.init.appCtx
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.spec.ECGenParameterSpec
import kotlin.time.Duration.Companion.seconds

internal const val keyStoreProvider = "AndroidKeyStore"

internal fun generateEcKeyPair(): Result<KeyPair> = runCatching {
    val keyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_EC)
    val ecGenParamSpec = ECGenParameterSpec("secp256r1")

    keyPairGenerator.initialize(ecGenParamSpec)
    keyPairGenerator.generateKeyPair()
}

internal fun generateKeyPairInTheKeystore(
    alias: String,
    privateKeyPurposes: KeyPurposes = KeyPurposes.privateKeyDefaults,
    publicKeyPurposes: KeyPurposes = KeyPurposes.publicKeyDefaults,
    keyAccessGuard: KeyAccessGuard,
    preferStrongbox: Boolean,
): Result<Unit> = runCatching {
    val keyPairGenerator = KeyPairGenerator.getInstance(
        KeyProperties.KEY_ALGORITHM_EC,
        keyStoreProvider
    )
    val parameterSpec = KeyGenParameterSpec.Builder(
        alias,
        (privateKeyPurposes + publicKeyPurposes).asFlags()
    ).also {
        it.setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
        if (preferStrongbox) if (SDK_INT >= 28) {
            if (appCtx.packageManager.hasSystemFeature(PackageManager.FEATURE_STRONGBOX_KEYSTORE)) {
                // Note: We can see the Strongbox version since API 32 (Android 12).
                // See the Javadoc of `PackageManager.FEATURE_STRONGBOX_KEYSTORE` (mentioned just above).
                it.setIsStrongBoxBacked(true)
            }
            it.setUserPresenceRequired(true)
        }
        keyAccessGuard.applyTo(it)
    }.build()

    keyPairGenerator.initialize(parameterSpec)
    val _ = keyPairGenerator.generateKeyPair()
}

private fun KeyPurposes.asFlags(): Int = 0
    .withFlag(if (signing) KeyProperties.PURPOSE_SIGN else 0)
    .withFlag(if (verifying) KeyProperties.PURPOSE_VERIFY else 0)
    .withFlag(if (encrypting) KeyProperties.PURPOSE_ENCRYPT else 0)
    .withFlag(if (decrypting) KeyProperties.PURPOSE_DECRYPT else 0)
    .withFlag(if (SDK_INT >= 28 && (wrapping || unwrapping)) KeyProperties.PURPOSE_WRAP_KEY else 0)
// PURPOSE_ATTEST_KEY and PURPOSE_AGREE_KEY left out as we don't need them at the moment.

private fun KeyAccessGuard.applyTo(builder: KeyGenParameterSpec.Builder) {
    when (this) {
        is KeyAccessGuard.Authenticated -> applyTo(builder)
        KeyAccessGuard.UserConfirmation -> {
            if (SDK_INT >= 28) builder.setUserConfirmationRequired(true)
        }
        KeyAccessGuard.Unguarded -> {}
    }
}

private fun KeyAccessGuard.Authenticated.applyTo(builder: KeyGenParameterSpec.Builder) {
    builder.setUserAuthenticationRequired(true)
    builder.setInvalidatedByBiometricEnrollment(this is KeyAccessGuard.Biometry.Current)
    if (SDK_INT >= 30) {
        val flags = when (this) {
            is KeyAccessGuard.Biometry -> KeyProperties.AUTH_BIOMETRIC_STRONG
            KeyAccessGuard.DevicePasscode -> KeyProperties.AUTH_DEVICE_CREDENTIAL
            KeyAccessGuard.DevicePasscodeOrNewBiometrics -> {
                KeyProperties.AUTH_BIOMETRIC_STRONG or KeyProperties.AUTH_DEVICE_CREDENTIAL
            }
        }
        builder.setUserAuthenticationParameters(0, flags)
    } else {
        when (this) {
            is KeyAccessGuard.DevicePasscode, is KeyAccessGuard.DevicePasscodeOrNewBiometrics -> {
                // Before API 30, setting this to a positive value is the only way to allow passcode.
                val validityDuration = 10.seconds
                @Suppress("deprecation")
                builder.setUserAuthenticationValidityDurationSeconds(validityDuration.inWholeSeconds.toInt())
            }
            else -> Unit
        }
    }
}
