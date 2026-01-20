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
package com.infomaniak.auth.lib

interface KeysManager {

    data class SecurityParameters(
//        val userPresenceRequired: Boolean,
        val biometricsRequired: Boolean,
        val devicePasswordRequired: Boolean,
    )

    /**
     * Returns true if the device has a Strongbox security chip or an available Secure Enclave.
     */
    suspend fun supportsTrustedExecutionEnvironment(): Boolean

    suspend fun hasEnrolledBiometrics(): Boolean

    /**
     * Must generate and save a private/public key pair into dedicated hardware, that is,
     * a TEE (Trusted Execution Environment) like the Secure Enclave on iOS devices, and a strongbox
     * on Android (if possible).
     *
     * The private key must not be accessible directly.
     *
     * The public key will be registered against the backend.
     *
     * They should be used for signing, and verification.
     */
    suspend fun generateSecurelyStoredKeyPairForSigning(securityParameters: SecurityParameters)

    /**
     * Typically used when the user wants to enable or disable biometrics requirement to access
     * the app.
     *
     *
     */
    suspend fun updateSecurityParameters(newParameters: SecurityParameters)

    /**
     * Must generate and save a private/public key pair that can be saved and restored
     * as part of app backup, to restore on a new device.
     */
    suspend fun createAndStoreMigrationKeys()

    /**
     * The public key, generated in [generateSecurelyStoredKeyPairForSigning], to be sent to the
     * backend, so what we sign with the private key is recognized to authenticated further backend
     * API calls.
     */
    suspend fun getPublicKey(): PublicKey

    /**
     * The public migration key, generated in [createAndStoreMigrationKeys], to be sent to the
     * backend for the future migration use-case (i.e. restoring from a backup or to another phone).
     */
    suspend fun getMostRecentMigrationPublicKey(): PublicKey
}
