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

import com.infomaniak.auth.lib.internal.models.LegacyAccount
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
internal actual suspend fun getLegacyAccounts(): List<LegacyAccount> = withContext(Dispatchers.IO) {
    val userDefaults = NSUserDefaults.standardUserDefaults
    val usersData = userDefaults.objectForKey("ALL_USERS") as? List<*> ?: emptyList<Any>()

    usersData.mapNotNull { item ->
        val data = item as? NSData ?: return@mapNotNull null
        val bytes = data.bytes?.readBytes(data.length.toInt()) ?: return@mapNotNull null
        val jsonString = bytes.decodeToString()

        try {
            val json = Json.parseToJsonElement(jsonString).jsonObject

            LegacyAccount(
                userId = json["id"]?.jsonPrimitive?.int ?: return@mapNotNull null,
                email = json["email"]?.jsonPrimitive?.content ?: return@mapNotNull null,
                displayName = json["display_name"]?.jsonPrimitive?.content ?: return@mapNotNull null,
                avatar = json["avatar"]?.jsonPrimitive?.content,
                secret = json["secret"]?.jsonPrimitive?.content ?: return@mapNotNull null
            )
        } catch (_: Exception) {
            null
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
internal actual suspend fun getSecretFor(userId: String): String? {
    return getLegacyAccounts().find {
        it.userId.toString() == userId
    }?.secret
}

internal actual suspend fun needMigration(): Boolean = withContext(Dispatchers.IO) {
    val userDefaults = NSUserDefaults.standardUserDefaults
    userDefaults.objectForKey("ALL_USERS") as? List<*> != null
}
