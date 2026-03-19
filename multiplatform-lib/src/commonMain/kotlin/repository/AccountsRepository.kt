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
package com.infomaniak.auth.lib.repository

import com.infomaniak.auth.lib.room.accounts.AccountEntity
import com.infomaniak.auth.lib.room.accounts.AccountsDatabase

class AccountsRepository(
    private val database: AccountsDatabase
) {
    private val dao = database.getDao()

    fun getAccounts() = dao.getAsFlow()

    suspend fun upsertAccount(account: AccountEntity) {
        dao.upsert(account)
    }

    suspend fun upsertAccounts(accounts: List<AccountEntity>) {
        dao.upsert(accounts)
    }

    suspend fun insertAccount(account: AccountEntity) {
        dao.insert(account)
    }

    suspend fun deleteAccount(id: String) {
        dao.delete(id)
    }
}
