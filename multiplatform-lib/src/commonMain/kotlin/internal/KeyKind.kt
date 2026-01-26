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

internal sealed interface KeyKind {

    sealed interface GuardedBy : KeyKind {
        sealed interface Biometrics : GuardedBy {
            data object Current : Biometrics
            data object Any : Biometrics
        }

        data object DevicePasscode : GuardedBy
        data object UserAction : GuardedBy
    }

    data object LessSensitiveOperations : KeyKind

    data object ActivationFromBackup : KeyKind
}
