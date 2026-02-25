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
package com.infomaniak.auth.lib.network.models

import kotlinx.serialization.Serializable
import okio.Buffer
import okio.ByteString

@Serializable
data class WebAuthnAttestationObject(
    val fmt: String,
    val attStmt: Unit,
    val authData: ByteArray,
) {
    companion object {

        fun toCborByteArray(fmt: String, authData: ByteArray): ByteString {
            val buffer = Buffer()

            // Map start
            buffer.writeByte(0xA2) // 2 items

            // "fmt": "none"
            writeText(buffer, "fmt")
            writeText(buffer, fmt)

            // "authData": <bytes>
            writeText(buffer, "authData")
            writeByteString(buffer, authData)

            return buffer.readByteString()
        }

        private fun writeText(buffer: Buffer, text: String) {
            val bytes = text.encodeToByteArray()
            buffer.writeByte(0x60 + bytes.size) // 0x60 = text string base
            buffer.write(bytes)
        }

        private fun writeByteString(buffer: Buffer, bytes: ByteArray) {
            when {
                bytes.size <= 23 -> {
                    buffer.writeByte(0x40 + bytes.size) // 0x40 = byte string base
                }
                bytes.size <= 255 -> {
                    buffer.writeByte(0x58) // byte string, 1-byte length follows
                    buffer.writeByte(bytes.size)
                }
                bytes.size <= 65535 -> {
                    buffer.writeByte(0x59) // byte string, 2-byte length follows
                    buffer.writeByte(bytes.size shr 8)
                    buffer.writeByte(bytes.size and 0xFF)
                }
                else -> {
                    buffer.writeByte(0x5A) // byte string, 4-byte length follows
                    buffer.writeInt(bytes.size)
                }
            }
            buffer.write(bytes)
        }
    }
}
