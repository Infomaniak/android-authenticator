package com.infomaniak.auth.migration.user

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class OTPUser(
    @PrimaryKey
    @ColumnInfo(name = "userid")
    var userID: Int,
    var email: String,
    @ColumnInfo(name = "displayname")
    var displayName: String,
    var avatar: String?,
    var secret: String
)