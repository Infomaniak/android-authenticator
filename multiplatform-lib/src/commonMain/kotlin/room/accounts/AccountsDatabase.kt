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
package com.infomaniak.auth.lib.room.accounts

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

@Database(entities = [AccountEntity::class], version = 1)
@TypeConverters(StatusEntityConverter::class)
@ConstructedBy(AccountsDatabaseConstructor::class)
abstract class AccountsDatabase : RoomDatabase() {
    abstract fun getDao(): AccountsDao
}

@Suppress("KotlinNoActualForExpect")
expect object AccountsDatabaseConstructor : RoomDatabaseConstructor<AccountsDatabase> {
    override fun initialize(): AccountsDatabase
}

fun getAccountsRoomDatabase(builder: RoomDatabase.Builder<AccountsDatabase>): AccountsDatabase {
    return builder
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}

class StatusEntityConverter {

    @TypeConverter
    fun fromStatus(status: StatusEntity) = status.name

    @TypeConverter
    fun toStatus(value: String) = StatusEntity.valueOf(value)
}
