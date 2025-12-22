package com.infomaniak.auth.migration.user

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [OTPUser::class], version = 1)
abstract class OTPUserDatabase : RoomDatabase() {

    abstract fun otpUserDao(): OTPUserDao

    companion object {

        @Volatile
        private var INSTANCE: OTPUserDatabase? = null

        fun getInstance(context: Context): OTPUserDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                OTPUserDatabase::class.java, "Infomaniak.db"
            ).build()
    }
}