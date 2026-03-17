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
@file:Suppress("NOTHING_TO_INLINE")

package com.infomaniak.auth.lib.internal.utils

import kotlin.experimental.and
import kotlin.experimental.inv
import kotlin.experimental.or

internal inline fun Long.hasFlag(flag: Long): Boolean = flag and this == flag
internal inline fun Long.withFlag(flag: Long): Long = this or flag
internal inline fun Long.minusFlag(flag: Long): Long = this and flag.inv()

internal inline fun Int.hasFlag(flag: Int): Boolean = flag and this == flag
internal inline fun Int.withFlag(flag: Int): Int = this or flag
internal inline fun Int.minusFlag(flag: Int): Int = this and flag.inv()

internal inline fun Short.hasFlag(flag: Short): Boolean = flag and this == flag
internal inline fun Short.withFlag(flag: Short): Short = this or flag
internal inline fun Short.minusFlag(flag: Short): Short = this and flag.inv()

internal inline fun Byte.hasFlag(flag: Byte): Boolean = flag and this == flag
internal inline fun Byte.withFlag(flag: Byte): Byte = this or flag
internal inline fun Byte.minusFlag(flag: Byte): Byte = this and flag.inv()

internal inline fun ULong.hasFlag(flag: ULong): Boolean = flag and this == flag
internal inline fun ULong.withFlag(flag: ULong): ULong = this or flag
internal inline fun ULong.minusFlag(flag: ULong): ULong = this and flag.inv()

internal inline fun UInt.hasFlag(flag: UInt): Boolean = flag and this == flag
internal inline fun UInt.withFlag(flag: UInt): UInt = this or flag
internal inline fun UInt.minusFlag(flag: UInt): UInt = this and flag.inv()

internal inline fun UShort.hasFlag(flag: UShort): Boolean = flag and this == flag
internal inline fun UShort.withFlag(flag: UShort): UShort = this or flag
internal inline fun UShort.minusFlag(flag: UShort): UShort = this and flag.inv()

internal inline fun UByte.hasFlag(flag: UByte): Boolean = flag and this == flag
internal inline fun UByte.withFlag(flag: UByte): UByte = this or flag
internal inline fun UByte.minusFlag(flag: UByte): UByte = this and flag.inv()
