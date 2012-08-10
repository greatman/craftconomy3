/*
 * This file is part of Craftconomy3.
 *
 * Copyright (c) 2011-2012, Greatman <http://github.com/greatman/>
 *
 * Craftconomy3 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Craftconomy3 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Craftconomy3.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.greatmancode.craftconomy3.account;

import java.util.HashMap;

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.database.tables.AccountTable;

public class AccountManager {
	public HashMap<String, Account> accountList = new HashMap<String, Account>();

	public Account getAccount(String name) {
		name = name.toLowerCase();
		Account account = null;
		if (accountList.containsKey(name)) {
			account = accountList.get(name);
		} else {
			account = new Account(name);
			accountList.put(name, account);
			
		}
		return account;
	}
	
	/**
	 * Check if a account exist in the database.
	 * @param name The name to check
	 */
	public boolean exist(String name) {
		return Common.getInstance().getDatabaseManager().getDatabase().select(AccountTable.class).where().contains("name", name).execute().findOne() != null;
	}
	
	
}
