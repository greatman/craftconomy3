package com.greatmancode.craftconomy3.account;

import java.util.HashMap;

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.database.tables.AccountTable;

public class AccountHandler {
	public HashMap<String, Account> accountList = new HashMap<String, Account>();

	public Account getAccount(String name) {
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
