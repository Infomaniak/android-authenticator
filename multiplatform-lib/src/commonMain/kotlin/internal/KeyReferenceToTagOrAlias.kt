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

internal fun KeyReference.HardwareSecured.toKeyTagOrAlias(): String = when (this) {
    KeyReference.BasicOperations -> "basic-ops"
    KeyReference.SensitiveOperations.BiometricsGuarded.CurrentOnly -> "sensitive-ops-biometrics-current-only"
    KeyReference.SensitiveOperations.BiometricsGuarded.CurrentAndFuture -> "sensitive-ops-biometrics-current-and-future"
    KeyReference.SensitiveOperations.DevicePasscodeGuarded -> "sensitive-ops-device-passcode"
    KeyReference.SensitiveOperations.UserActionGuarded -> "sensitive-ops-user-action"
}
