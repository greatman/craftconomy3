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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.database.tables.AccessTable;
import com.greatmancode.craftconomy3.database.tables.AccountTable;
import com.greatmancode.craftconomy3.database.tables.BalanceTable;

/**
 * Provides access to a account.
 * @author greatman
 */
public class AccountManager {
	private Map<String, Account> accountList = new HashMap<String, Account>();

	/**
	 * Retrieve a account. Accounts prefixed with bank: are bank accounts.
	 * @param name The name of the account to retrieve
	 * @return A economy account
	 */
	public Account getAccount(String name) {
		String newName = name;
		if (!Common.getInstance().getConfigurationManager().getConfig().getBoolean("System.Case-sentitive")) {
			newName = name.toLowerCase();
		}
		Account account = null;
		if (accountList.containsKey(newName)) {
			account = accountList.get(newName);
		} else {
			account = new Account(newName);
			accountList.put(newName, account);
		}
		return account;
	}

	/**
	 * Check if a account exist in the database.
	 * @param name The name to check
	 */
	public boolean exist(String name) {
		String newName = name;
		if (!Common.getInstance().getConfigurationManager().getConfig().getBoolean("System.Case-sentitive")) {
			newName = name.toLowerCase();
		}
		boolean result = false;
		if (accountList.containsKey(newName)) {
			result = true;
		} else {
			result = Common.getInstance().getDatabaseManager().getDatabase().select(AccountTable.class).where().equal("name", newName).execute().findOne() != null;
		}
		return result;
	}

	/**
	 * Delete a account from the system
	 * @param name The account name
	 * @return True if the account has been deleted. Else false.
	 */
	public boolean delete(String name) {
		boolean result = false;
		if (exist(name)) {
			Account account = getAccount(name);
			AccountTable accountTable = Common.getInstance().getDatabaseManager().getDatabase().select(AccountTable.class).where().contains("name", name).execute().findOne();
			List<BalanceTable> balanceTableList = Common.getInstance().getDatabaseManager().getDatabase().select(BalanceTable.class).where().equal("username_id", accountTable.getId()).execute().find();
			if (balanceTableList != null) {
				Iterator<BalanceTable> iterator = balanceTableList.iterator();
				while (iterator.hasNext()) {
					Common.getInstance().getDatabaseManager().getDatabase().remove(iterator.next());
				}
			}
			if (account.isBankAccount()) {
				List<AccessTable> accessTableList = Common.getInstance().getDatabaseManager().getDatabase().select(AccessTable.class).where().equal("account_id", accountTable.getId()).execute().find();
				if (accessTableList != null) {
					Iterator<AccessTable> iterator = accessTableList.iterator();
					while (iterator.hasNext()) {
						Common.getInstance().getDatabaseManager().getDatabase().remove(iterator.next());
					}
				}
			}
			Common.getInstance().getDatabaseManager().getDatabase().remove(accountTable);
			accountList.remove(name);
			result = true;
		}
		return result;
	}
}
