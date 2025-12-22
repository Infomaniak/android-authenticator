package com.infomaniak.auth.migration.user

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query

@Dao
interface OTPUserDao {

    @Delete
    fun delete(user: OTPUser)

    @Query("SELECT * FROM users WHERE userid = :userID")
    fun findUserById(userID: Int): OTPUser?

    @Query("SELECT * FROM users")
    fun getAllUsers(): List<OTPUser>
}