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
package com.infomaniak.auth.lib.internal.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * This is the model that was used for users in kAuth (version 1.X of the app).
 * It contains basic info, with the [email] being potentially outdated,
 * and the [secret] for the (OTP) one-time-password generation.
 *
 * On Android, this was saved in a SQLite database (managed by Room).
 * On iOS, this was saved in NSUserDefaults, encoded in JSON.
 *
 * This model is used only for migration of users from kAuth,
 * and the whole data is set to be deleted right after it completes.
 */
@Entity(tableName = "users")
@Serializable
internal data class LegacyUser(

    @PrimaryKey
    @ColumnInfo(name = "userid")
    @SerialName("id")
    val userId: Int,

    val email: String,

    @ColumnInfo(name = "displayname")
    @SerialName("display_name")
    val displayName: String,

    val avatar: String?,

    val secret: String,
)
