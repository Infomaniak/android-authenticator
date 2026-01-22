import android.content.pm.PackageManager
import android.os.Build.VERSION.SDK_INT
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import com.infomaniak.auth.lib.KeysManager
import com.infomaniak.auth.lib.PublicKey
import splitties.init.appCtx
import java.security.KeyPairGenerator
import java.security.KeyStore
import kotlin.reflect.KParameter

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
class KeysManagerImpl : KeysManager {
    private val keyStoreProvider = "AndroidKeyStore"

    override suspend fun supportsTrustedExecutionEnvironment(): Boolean {
        TODO()
    }

    override suspend fun hasEnrolledBiometrics(): Boolean {
        TODO()
    }

    override suspend fun updateSecurityParameters(newParameters: KeysManager.SecurityParameters) {
        TODO()
    }

    //TODO[android-biometrics]: See how to access stuff via https://developer.android.com/identity/sign-in/biometric-auth
    //TODO[android-keystore]: See https://developer.android.com/reference/android/security/keystore/KeyGenParameterSpec
    //TODO[android-keystore]: See the doc of the KeyInfo class

    override suspend fun generateSecurelyStoredKeyPairForSigning(
        securityParameters: KeysManager.SecurityParameters
    ) {
        val keyPairGenerator = KeyPairGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_EC,
            keyStoreProvider
        )
        val parameterSpec = KeyGenParameterSpec.Builder(
            "alias",
            KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY
        ).also {
            it.setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
            if (SDK_INT >= 28) {
                if (appCtx.packageManager.hasSystemFeature(PackageManager.FEATURE_STRONGBOX_KEYSTORE)) {
                    it.setIsStrongBoxBacked(true)
                    //TODO: Check if it requires biometrics
                }
                it.setUserPresenceRequired(true)
            }
            if (securityParameters.biometricsRequired) {
                it.setUserAuthenticationRequired(true)
                it.setInvalidatedByBiometricEnrollment(false)
            } else {
                if (SDK_INT >= 28) it.setUserConfirmationRequired(true)
            }
        }.build()

        keyPairGenerator.initialize(parameterSpec)
        val keyPair = keyPairGenerator.generateKeyPair()
        keyPair.private.encoded
        keyPair.public.encoded

        val ks = KeyStore.getInstance(keyStoreProvider).also {
            it.load(null)
        }
        ks.aliases()

        TODO()
    }

    override suspend fun createAndStoreMigrationKeys() {
        TODO()
    }

    override suspend fun getPublicKey(): PublicKey {
        TODO()
    }

    override suspend fun getMostRecentMigrationPublicKey(): PublicKey {
        TODO()
    }
}
