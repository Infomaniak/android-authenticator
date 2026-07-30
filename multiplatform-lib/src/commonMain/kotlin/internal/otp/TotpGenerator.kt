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
package com.infomaniak.auth.lib.internal.otp

import com.infomaniak.auth.lib.internal.models.LegacyUser
import okio.ByteString
import okio.ByteString.Companion.toByteString
import kotlin.math.pow

internal class TotpGenerator(
    secret: ByteArray,
    private val digits: Int = 6,
    private val timeStep: Long = 30L, // seconds
    private val algorithm: Algorithm = Algorithm.SHA1
) {
    private val secret = secret.toByteString()

    enum class Algorithm {
        SHA1, SHA256, SHA512
    }

    fun generate(timestamp: Long): String {
        val counter = timestamp / timeStep
        return generateOtp(counter)
    }

    fun generateOtp(counter: Long): String {
        val counterBytes = counter.toByteArrayBigEndian()
        val hash = hmac(counterBytes.toByteString())
        val offset = hash.last().toInt() and 0x0F
        val binary = ((hash[offset].toInt() and 0x7F) shl 24) or
                ((hash[offset + 1].toInt() and 0xFF) shl 16) or
                ((hash[offset + 2].toInt() and 0xFF) shl 8) or
                (hash[offset + 3].toInt() and 0xFF)
        val otp = binary % 10.0.pow(digits).toInt()
        return otp.toString().padStart(digits, '0')
    }

    private fun hmac(data: ByteString): ByteArray {
        return when (algorithm) {
            Algorithm.SHA1 -> data.hmacSha1(secret)
            Algorithm.SHA256 -> data.hmacSha256(secret)
            Algorithm.SHA512 -> data.hmacSha512(secret)
        }.toByteArray()
    }

    private fun Long.toByteArrayBigEndian(): ByteArray {
        return ByteArray(8) { i ->
            ((this shr (56 - i * 8)) and 0xFF).toByte()
        }
    }
}

internal expect suspend fun needMigration(): Boolean
internal expect suspend fun getLegacyAccounts(): List<LegacyUser>
internal expect suspend fun deleteLegacyAccount(userId: String)
internal expect suspend fun deleteLegacyDB()
internal expect suspend fun getSecretFor(userId: Long): String?
