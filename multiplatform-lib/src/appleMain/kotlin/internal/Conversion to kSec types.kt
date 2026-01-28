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

import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreFoundation.CFStringRef
import platform.Security.SecAccessControlCreateFlags
import platform.Security.kSecAccessControlAnd
import platform.Security.kSecAccessControlBiometryAny
import platform.Security.kSecAccessControlBiometryCurrentSet
import platform.Security.kSecAccessControlDevicePasscode
import platform.Security.kSecAccessControlPrivateKeyUsage
import platform.Security.kSecAccessControlUserPresence
import platform.Security.kSecAttrAccessibleAfterFirstUnlock
import platform.Security.kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly
import platform.Security.kSecAttrAccessibleAlways
import platform.Security.kSecAttrAccessibleAlwaysThisDeviceOnly
import platform.Security.kSecAttrAccessibleWhenPasscodeSetThisDeviceOnly
import platform.Security.kSecAttrAccessibleWhenUnlocked
import platform.Security.kSecAttrAccessibleWhenUnlockedThisDeviceOnly

@ExperimentalForeignApi
internal fun KeyAccessibility.toKSecAttrAccessible(): CFStringRef? = when (this) {
    KeyAccessibility.WhenUnlocked.ThisDeviceOnly -> kSecAttrAccessibleWhenUnlockedThisDeviceOnly
    KeyAccessibility.WhenUnlocked -> kSecAttrAccessibleWhenUnlocked
    KeyAccessibility.AfterFirstUnlock.ThisDeviceOnly -> kSecAttrAccessibleAfterFirstUnlock
    KeyAccessibility.AfterFirstUnlock -> kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly
    KeyAccessibility.WhenPasscodeSet.ThisDeviceOnly -> kSecAttrAccessibleWhenPasscodeSetThisDeviceOnly
    KeyAccessibility.Always -> kSecAttrAccessibleAlways
    KeyAccessibility.Always.ThisDeviceOnly -> kSecAttrAccessibleAlwaysThisDeviceOnly
}

internal fun KeyAccessControl.toAccessControlFlags(): SecAccessControlCreateFlags = when (this) {
    KeyAccessControl.Biometry.CurrentSet -> kSecAccessControlBiometryCurrentSet
    KeyAccessControl.Biometry.Any -> kSecAccessControlBiometryAny
    KeyAccessControl.DevicePasscode -> kSecAccessControlDevicePasscode
    KeyAccessControl.UserPresence -> kSecAccessControlUserPresence
    KeyAccessControl.Unguarded -> 0uL
} or kSecAccessControlPrivateKeyUsage or kSecAccessControlAnd
