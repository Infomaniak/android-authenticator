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
package com.infomaniak.auth.lib.otp

import org.kotlincrypto.core.mac.Mac
import org.kotlincrypto.macs.hmac.sha1.HmacSHA1
import org.kotlincrypto.macs.hmac.sha2.HmacSHA256
import org.kotlincrypto.macs.hmac.sha2.HmacSHA512
import kotlin.math.pow

class TotpGenerator(
    private val secret: ByteArray,
    private val digits: Int = 6,
    private val timeStep: Long = 30L, // secondes
    private val algorithm: Algorithm = Algorithm.SHA1
) {
    enum class Algorithm {
        SHA1, SHA256, SHA512
    }

    fun generate(timestamp: Long = currentTimeMillis() / 1000): String {
        val counter = timestamp / timeStep
        return generateOtp(counter)
    }

    fun generateOtp(counter: Long): String {
        val counterBytes = counter.toByteArrayBigEndian()
        val hash = hmac(counterBytes)
        val offset = hash.last().toInt() and 0x0F
        val binary = ((hash[offset].toInt() and 0x7F) shl 24) or
                ((hash[offset + 1].toInt() and 0xFF) shl 16) or
                ((hash[offset + 2].toInt() and 0xFF) shl 8) or
                (hash[offset + 3].toInt() and 0xFF)
        val otp = binary % 10.0.pow(digits).toInt()
        return otp.toString().padStart(digits, '0')
    }

    private fun hmac(data: ByteArray): ByteArray {
        val mac: Mac = when (algorithm) {
            Algorithm.SHA1 -> HmacSHA1(secret)
            Algorithm.SHA256 -> HmacSHA256(secret)
            Algorithm.SHA512 -> HmacSHA512(secret)
        }
        return mac.doFinal(data)
    }

    private fun Long.toByteArrayBigEndian(): ByteArray {
        return ByteArray(8) { i ->
            ((this shr (56 - i * 8)) and 0xFF).toByte()
        }
    }

    companion object {
        fun decodeBase32Secret(base32Secret: String): ByteArray {
            return base32Decode(base32Secret.uppercase().replace(" ", ""))
        }
    }
}

private fun base32Decode(input: String): ByteArray {
    val base32Chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567"
    val output = mutableListOf<Byte>()
    var bits = 0
    var value = 0

    for (char in input) {
        val index = base32Chars.indexOf(char)
        if (index == -1) continue // Skip padding '=' ou espaces

        value = (value shl 5) or index
        bits += 5

        if (bits >= 8) {
            output.add(((value shr (bits - 8)) and 0xFF).toByte())
            bits -= 8
        }
    }

    return output.toByteArray()
}

expect fun currentTimeMillis(): Long
expect suspend fun needMigration(): Boolean
expect suspend fun getLegacyAccounts(): List<LegacyAccount>

data class LegacyAccount(
    var userId: Int,
    var email: String,
    var displayName: String,
    var avatar: String?,
    var secret: String
)
