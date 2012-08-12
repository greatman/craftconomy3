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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.currency.Currency;
import com.greatmancode.craftconomy3.currency.CurrencyManager;
import com.greatmancode.craftconomy3.database.tables.AccountTable;
import com.greatmancode.craftconomy3.database.tables.BalanceTable;

/**
 * Represents a economy account.
 * @author greatman
 * 
 */
public class Account {

	public static final String BANK_PREFIX = "bank:";
	private AccountTable account;
	private AccountACL acl;

	/**
	 * Load a account. Creates one if it doesn't exist.
	 * @param name The account name
	 */
	public Account(String name) {
		AccountTable result = Common.getInstance().getDatabaseManager().getDatabase().select(AccountTable.class).where().equal("name", name).execute().findOne();
		boolean create = false;
		if (result == null) {
			result = new AccountTable();
			result.name = name;
			create = true;
		}
		account = result;
		if (create) {
			Common.getInstance().getDatabaseManager().getDatabase().save(result);
			BalanceTable balance = new BalanceTable();
			balance.username_id = result.id;
			balance.currency_id = CurrencyManager.defaultCurrencyID;
			balance.worldName = getWorldPlayerCurrentlyIn();
			if (!name.contains(Account.BANK_PREFIX)) {
				balance.balance = Common.getInstance().getConfigurationManager().getHoldings();
			} else {
				balance.balance = 0.0;
			}

			Common.getInstance().getDatabaseManager().getDatabase().save(balance);
		}

		if (name.contains(Account.BANK_PREFIX)) {
			acl = new AccountACL(this, account.id);
		}
	}

	/**
	 * Returns the account database ID
	 * @return the account database ID
	 */
	public int getAccountID() {
		return account.id;
	}

	/**
	 * Returns the account name.
	 * @return The account name
	 */
	public String getAccountName() {
		return account.name;
	}

	/**
	 * Checks if this account is a bank account
	 * @return True if this account is a bank account, else false
	 */
	public boolean isBankAccount() {
		return account.name.contains(Account.BANK_PREFIX);
	}

	/**
	 * Get the account ACL. Only used with a bank account
	 * @return The account ACL if it's a bank account, else null
	 */
	public AccountACL getAccountACL() {
		AccountACL accountAcl = null;
		if (isBankAccount()) {
			accountAcl = acl;
		}
		return accountAcl;
	}

	/**
	 * Get the whole account balance
	 * @return A list of all account balance
	 */
	public List<Balance> getAllBalance() {
		List<Balance> balanceList = new ArrayList<Balance>();
		Iterator<BalanceTable> list = Common.getInstance().getDatabaseManager().getDatabase().select(BalanceTable.class).where().equal("username_id", account.id).execute().find().iterator();
		while (list.hasNext()) {
			BalanceTable table = list.next();
			balanceList.add(new Balance(table.worldName, Common.getInstance().getCurrencyManager().getCurrency(table.currency_id), table.balance));
		}
		return balanceList;
	}

	/**
	 * Get the whole account balance in a certain world
	 * @param world The world to search in
	 * @return A list of Balance
	 */
	public List<Balance> getAllWorldBalance(String world) {
		if (!Common.getInstance().getConfigurationManager().isMultiWorld()) {
			world = "any";
		}
		List<Balance> balanceList = new ArrayList<Balance>();
		Iterator<BalanceTable> list = Common.getInstance().getDatabaseManager().getDatabase().select(BalanceTable.class).where().equal("username_id", account.id).and().equal("worldName", world).execute().find().iterator();
		while (list.hasNext()) {
			BalanceTable table = list.next();
			balanceList.add(new Balance(table.worldName, Common.getInstance().getCurrencyManager().getCurrency(table.currency_id), table.balance));
		}
		return balanceList;
	}

	/**
	 * Get's the player balance. Sends double.MIN_NORMAL in case of a error
	 * @param world The world to search in
	 * @param currencyName The currency Name
	 * @return The balance
	 */
	public double getBalance(String world, String currencyName) {
		double balance = Double.MIN_NORMAL;
		if (!Common.getInstance().getConfigurationManager().isMultiWorld()) {
			world = "any";
		}
		Currency currency = Common.getInstance().getCurrencyManager().getCurrency(currencyName);
		if (currency != null) {
			BalanceTable balanceTable = Common.getInstance().getDatabaseManager().getDatabase().select(BalanceTable.class).where().equal("username_id", account.id).and().equal("currency_id", currency.getDatabaseID()).and().equal("worldName", world).execute().findOne();
			if (balanceTable != null) {
				balance = balanceTable.balance;
			}
		}
		return balance;
	}

	/**
	 * Adds a certain amount of money in the account
	 * @param amount The amount of money to add
	 * @param world The World we want to add money in
	 * @param currencyName The currency we want to add money in
	 * @return The new balance
	 */
	public double deposit(double amount, String world, String currencyName) {
		BalanceTable balanceTable = null;
		if (!Common.getInstance().getConfigurationManager().isMultiWorld()) {
			world = "any";
		}
		Currency currency = Common.getInstance().getCurrencyManager().getCurrency(currencyName);
		if (currency != null) {
			balanceTable = Common.getInstance().getDatabaseManager().getDatabase().select(BalanceTable.class).where().equal("username_id", account.id).and().equal("currency_id", currency.getDatabaseID()).and().equal("worldName", world).execute().findOne();
			if (balanceTable != null) {
				balanceTable.balance += amount;

			} else {
				balanceTable = new BalanceTable();
				balanceTable.currency_id = currency.getDatabaseID();
				balanceTable.username_id = account.id;
				balanceTable.worldName = world;
				balanceTable.balance = amount;
			}
			Common.getInstance().getDatabaseManager().getDatabase().save(balanceTable);
		}
		return balanceTable.balance;
	}

	/**
	 * withdraw a certain amount of money in the account
	 * @param amount The amount of money to withdraw
	 * @param world The World we want to withdraw money from
	 * @param currencyName The currency we want to withdraw money from
	 * @return The new balance
	 */
	public double withdraw(double amount, String world, String currencyName) {
		BalanceTable balanceTable = null;
		if (!Common.getInstance().getConfigurationManager().isMultiWorld()) {
			world = "any";
		}
		Currency currency = Common.getInstance().getCurrencyManager().getCurrency(currencyName);
		if (currency != null) {
			balanceTable = Common.getInstance().getDatabaseManager().getDatabase().select(BalanceTable.class).where().equal("username_id", account.id).and().equal("currency_id", currency.getDatabaseID()).and().equal("worldName", world).execute().findOne();
			if (balanceTable != null) {
				balanceTable.balance -= amount;

			} else {
				balanceTable = new BalanceTable();
				balanceTable.currency_id = currency.getDatabaseID();
				balanceTable.username_id = account.id;
				balanceTable.worldName = world;
				balanceTable.balance = amount;
			}
			Common.getInstance().getDatabaseManager().getDatabase().save(balanceTable);
		}
		return balanceTable.balance;
	}

	/**
	 * set a certain amount of money in the account
	 * @param amount The amount of money to set
	 * @param world The World we want to set money to
	 * @param currencyName The currency we want to set money to
	 * @return The new balance
	 */
	public double set(double amount, String world, String currencyName) {
		BalanceTable balanceTable = null;
		if (!Common.getInstance().getConfigurationManager().isMultiWorld()) {
			world = "any";
		}
		Currency currency = Common.getInstance().getCurrencyManager().getCurrency(currencyName);
		if (currency != null) {
			balanceTable = Common.getInstance().getDatabaseManager().getDatabase().select(BalanceTable.class).where().equal("username_id", account.id).and().equal("currency_id", currency.getDatabaseID()).and().equal("worldName", world).execute().findOne();
			if (balanceTable != null) {
				balanceTable.balance = amount;

			} else {
				balanceTable = new BalanceTable();
				balanceTable.currency_id = currency.getDatabaseID();
				balanceTable.username_id = account.id;
				balanceTable.worldName = world;
				balanceTable.balance = amount;
			}
			Common.getInstance().getDatabaseManager().getDatabase().save(balanceTable);
		}
		return balanceTable.balance;
	}

	/**
	 * Checks if we have enough money in a certain balance
	 * @param amount The amount of money to check
	 * @param worldName The World we want to check
	 * @param currencyName The currency we want to check
	 * @return True if there's enough money. Else false
	 */
	public boolean hasEnough(double amount, String worldName, String currencyName) {
		boolean result = false;
		if (!Common.getInstance().getConfigurationManager().isMultiWorld()) {
			worldName = "any";
		}
		Currency currency = Common.getInstance().getCurrencyManager().getCurrency(currencyName);
		if (currency != null) {
			if (getBalance(worldName, currencyName) >= amount) {
				result = true;
			}
		}
		return result;
	}

	/**
	 * Returns the world that the player is currently in
	 * @return The world name that the player is currently in or any if he is not online/Multiworld system not enabled
	 */
	public String getWorldPlayerCurrentlyIn() {
		String world = "any";
		if (Common.getInstance().getServerCaller().isOnline(account.name)) {
			if (Common.getInstance().getConfigurationManager().isMultiWorld()) {
				world = Common.getInstance().getServerCaller().getPlayerWorld(account.name);
			}
		}
		return world;
	}
}
