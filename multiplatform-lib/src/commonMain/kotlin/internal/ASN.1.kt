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

// See https://www.oss.com/asn1/resources/asn1-made-simple/asn1-quick-reference.html#Types
internal object AsnOneTypes {
    const val SEQUENCE: Byte = 0x30
    const val BIT_STRING: Byte = 0x03
    const val INTEGER: Byte = 0x02
}

fun ByteArray.encodeAsn1Integer(): ByteArray {
    val needsPadding = this[0].toInt() and 0x80 != 0
    val length = size + if (needsPadding) 1 else 0
    val result = ByteArray(2 + length)

    result[0] = AsnOneTypes.INTEGER
    result[1] = length.toByte()

    if (needsPadding) {
        result[2] = 0x00
        copyInto(result, 3)
    } else {
        copyInto(result, 2)
    }

    return result
}
