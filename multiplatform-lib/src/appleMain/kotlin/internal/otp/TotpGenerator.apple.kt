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
package com.infomaniak.auth.lib.internal.otp

import com.infomaniak.auth.lib.internal.extensions.toByteArray
import com.infomaniak.auth.lib.internal.models.LegacyUser
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.readBytes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import platform.Foundation.NSData
import platform.Foundation.NSUserDefaults

@OptIn(BetaInteropApi::class, ExperimentalForeignApi::class)
internal actual suspend fun getLegacyAccounts(): List<LegacyUser> = withContext(Dispatchers.IO) {
    val userDefaults = NSUserDefaults.standardUserDefaults
    val usersData = userDefaults.objectForKey("ALL_USERS") as? List<*> ?: emptyList<Any>()

    val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        explicitNulls = false
    }

    usersData.mapNotNull { item ->
        val data = item as? NSData ?: return@mapNotNull null
        runCatching {
            json.decodeFromString<LegacyUser>(data.toByteArray().decodeToString())
        }.getOrNull()
    }
}

@OptIn(ExperimentalForeignApi::class)
internal actual suspend fun deleteLegacyAccount(userId: String) {
    withContext(Dispatchers.IO) {
        val userDefaults = NSUserDefaults.standardUserDefaults
        val usersData = userDefaults.objectForKey("ALL_USERS") as? MutableList<*> ?: return@withContext false

        val userIdInt = userId.toIntOrNull() ?: return@withContext false

        val updatedList = usersData.mapIndexedNotNull { index, item ->
            val data = item as? NSData ?: return@mapIndexedNotNull item
            val bytes = data.bytes?.readBytes(data.length.toInt()) ?: return@mapIndexedNotNull item
            val jsonString = bytes.decodeToString()

            try {
                val json = Json.parseToJsonElement(jsonString).jsonObject
                val id = json["id"]?.jsonPrimitive?.int

                if (id == userIdInt) null else item
            } catch (_: Exception) {
                item
            }
        }

        if (updatedList.size < usersData.size) {
            userDefaults.setObject(updatedList, "ALL_USERS")
            userDefaults.synchronize()
            true
        } else {
            false
        }
    }
}

internal actual suspend fun deleteLegacyDB() {
    withContext(Dispatchers.IO) {
        NSUserDefaults.standardUserDefaults.removeObjectForKey("ALL_USERS")
    }
}

@OptIn(ExperimentalForeignApi::class)
internal actual suspend fun getSecretFor(userId: Long): String? = withContext(Dispatchers.IO) {
    getLegacyAccounts().find {
        it.userId.toLong() == userId
    }?.secret
}

internal actual suspend fun needMigration(): Boolean = withContext(Dispatchers.IO) {
    val userDefaults = NSUserDefaults.standardUserDefaults
    userDefaults.objectForKey("ALL_USERS") as? List<*> != null
}
