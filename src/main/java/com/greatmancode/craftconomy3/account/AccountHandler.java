package com.greatmancode.craftconomy3.account;

import java.util.HashMap;

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
}
