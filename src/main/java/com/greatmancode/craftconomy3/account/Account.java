/*
 * This file is part of Craftconomy3.
 *
 * Copyright (c) 2011-2013, Greatman <http://github.com/greatman/>
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

import com.greatmancode.craftconomy3.Cause;
import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.LogInfo;
import com.greatmancode.craftconomy3.currency.Currency;
import com.greatmancode.craftconomy3.database.tables.AccountTable;
import com.greatmancode.craftconomy3.database.tables.BalanceTable;

/**
 * Represents a economy account.
 * @author greatman
 */
public class Account {
	public static final String BANK_PREFIX = "bank:";
	private final AccountTable account;
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
			result.setName(name);
			create = true;
		}
		account = result;
		if (create) {
			Common.getInstance().getDatabaseManager().getDatabase().save(result);
			BalanceTable balance = new BalanceTable();
			balance.setUsernameId(result.getId());
			balance.setCurrencyId(Common.getInstance().getCurrencyManager().getDefaultCurrency().getDatabaseID());
			balance.setWorldName(getWorldGroupOfPlayerCurrentlyIn());
			if (!name.contains(Account.BANK_PREFIX)) {
				balance.setBalance(Common.getInstance().getConfigurationManager().getHoldings());
			} else {
				balance.setBalance(0.0);
			}

			Common.getInstance().getDatabaseManager().getDatabase().save(balance);
		}

		if (name.contains(Account.BANK_PREFIX)) {
			acl = new AccountACL(this, account.getId());
		}
	}

	/**
	 * Returns the account database ID
	 * @return the account database ID
	 */
	public int getAccountID() {
		return account.getId();
	}

	/**
	 * Returns the account name.
	 * @return The account name
	 */
	public String getAccountName() {
		return account.getName();
	}

	/**
	 * Checks if this account is a bank account
	 * @return True if this account is a bank account, else false
	 */
	public boolean isBankAccount() {
		return account.getName().contains(Account.BANK_PREFIX);
	}

	/**
	 * Checks if this account is a bank account
	 * @param accountName The account name
	 * @return True if this account is a bank account, else false
	 */
	public static boolean isBankAccount(String accountName) {
		return accountName.contains(Account.BANK_PREFIX);
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
		Iterator<BalanceTable> list = Common.getInstance().getDatabaseManager().getDatabase().select(BalanceTable.class).where().equal(BalanceTable.USERNAME_ID_FIELD, account.getId()).execute().find().iterator();
		while (list.hasNext()) {
			BalanceTable table = list.next();
			balanceList.add(new Balance(table.getWorldName(), Common.getInstance().getCurrencyManager().getCurrency(table.getCurrencyId()), format(table.getBalance())));
		}
		return balanceList;
	}

	/**
	 * Get the whole account balance in a certain world / world group
	 * @param world The world / world group to search in
	 * @return A list of Balance
	 */
	public List<Balance> getAllWorldBalance(String world) {
		if (!Common.getInstance().getWorldGroupManager().worldGroupExist(world)) {
			world = Common.getInstance().getWorldGroupManager().getWorldGroupName(world);
		}
		List<Balance> balanceList = new ArrayList<Balance>();
		Iterator<BalanceTable> list = Common.getInstance().getDatabaseManager().getDatabase().select(BalanceTable.class).where().equal(BalanceTable.USERNAME_ID_FIELD, account.getId()).and().equal(BalanceTable.WORLD_NAME_FIELD, world).execute().find().iterator();
		while (list.hasNext()) {
			BalanceTable table = list.next();
			balanceList.add(new Balance(table.getWorldName(), Common.getInstance().getCurrencyManager().getCurrency(table.getCurrencyId()), format(table.getBalance())));
		}
		return balanceList;
	}

	/**
	 * Get's the player balance. Sends double.MIN_NORMAL in case of a error
	 * @param world The world / world group to search in
	 * @param currencyName The currency Name
	 * @return The balance. If the account has infinite money. Double.MAX_VALUE is returned.
	 */
	public double getBalance(String world, String currencyName) {
		double balance = Double.MIN_NORMAL;
		if (!Common.getInstance().getWorldGroupManager().worldGroupExist(world)) {
			world = Common.getInstance().getWorldGroupManager().getWorldGroupName(world);
		}
		Currency currency = Common.getInstance().getCurrencyManager().getCurrency(currencyName);
		if (currency != null) {
			if (!hasInfiniteMoney()) {
				BalanceTable balanceTable = Common.getInstance().getDatabaseManager().getDatabase().select(BalanceTable.class).where().equal(BalanceTable.USERNAME_ID_FIELD, account.getId()).and().equal(BalanceTable.CURRENCY_ID_FIELD, currency.getDatabaseID()).and().equal(BalanceTable.WORLD_NAME_FIELD, world).execute().findOne();
				if (balanceTable != null) {
					balance = balanceTable.getBalance();
				}
			} else {
				balance = Double.MAX_VALUE;
			}
		}
		return format(balance);
	}

	/**
	 * Adds a certain amount of money in the account
	 * @param amount The amount of money to add
	 * @param world The World / World group we want to add money in
	 * @param currencyName The currency we want to add money in
	 * @return The new balance. If the account has infinite money. Double.MAX_VALUE is returned.
	 * @deprecated use {@link #deposit(double, String, String, com.greatmancode.craftconomy3.Cause, String)}
	 */
	@Deprecated
	public double deposit(double amount, String world, String currencyName) {
		return deposit(amount, world, currencyName, Cause.UNKNOWN, null);
	}

	/**
	 * Adds a certain amount of money in the account
	 * @param amount The amount of money to add
	 * @param world The World / World group we want to add money in
	 * @param currencyName The currency we want to add money in
	 * @param cause The cause of the change.
	 * @return The new balance. If the account has infinite money. Double.MAX_VALUE is returned.
	 */
	public double deposit(double amount, String world, String currencyName, Cause cause, String causeReason) {
		BalanceTable balanceTable;
		double result = 0;
		amount = format(amount);
		if (!Common.getInstance().getWorldGroupManager().worldGroupExist(world)) {
			world = Common.getInstance().getWorldGroupManager().getWorldGroupName(world);
		}
		Currency currency = Common.getInstance().getCurrencyManager().getCurrency(currencyName);
		if (currency != null) {
			if (!hasInfiniteMoney()) {
				balanceTable = Common.getInstance().getDatabaseManager().getDatabase().select(BalanceTable.class).where().equal(BalanceTable.USERNAME_ID_FIELD, account.getId()).and().equal(BalanceTable.CURRENCY_ID_FIELD, currency.getDatabaseID()).and().equal(BalanceTable.WORLD_NAME_FIELD, world).execute().findOne();
				if (balanceTable != null) {
					balanceTable.setBalance(balanceTable.getBalance() + amount);
				} else {
					balanceTable = new BalanceTable();
					balanceTable.setCurrencyId(currency.getDatabaseID());
					balanceTable.setUsernameId(account.getId());
					balanceTable.setWorldName(world);
					balanceTable.setBalance(amount);
				}
				Common.getInstance().getDatabaseManager().getDatabase().save(balanceTable);

				Common.getInstance().writeLog(LogInfo.DEPOSIT, cause, causeReason, this, amount, currency, world);
				result = balanceTable.getBalance();
			} else {
				result = Double.MAX_VALUE;
			}
		}

		return format(result);
	}

	/**
	 * withdraw a certain amount of money in the account
	 * @param amount The amount of money to withdraw
	 * @param world The World / World group we want to withdraw money from
	 * @param currencyName The currency we want to withdraw money from
	 * @return The new balance. If the account has infinite money. Double.MAX_VALUE is returned.
	 * @deprecated use {@link #withdraw(double, String, String, com.greatmancode.craftconomy3.Cause, String)}
	 */
	@Deprecated
	public double withdraw(double amount, String world, String currencyName) {
		return withdraw(amount, world, currencyName, Cause.UNKNOWN, null);
	}

	/**
	 * withdraw a certain amount of money in the account
	 * @param amount The amount of money to withdraw
	 * @param world The World / World group we want to withdraw money from
	 * @param currencyName The currency we want to withdraw money from
	 * @param cause The cause of the change.
	 * @return The new balance. If the account has infinite money. Double.MAX_VALUE is returned.
	 */
	public double withdraw(double amount, String world, String currencyName, Cause cause, String causeReason) {
		BalanceTable balanceTable;
		double result = 0;
		amount = format(amount);
		if (!Common.getInstance().getWorldGroupManager().worldGroupExist(world)) {
			world = Common.getInstance().getWorldGroupManager().getWorldGroupName(world);
		}
		Currency currency = Common.getInstance().getCurrencyManager().getCurrency(currencyName);
		if (currency != null) {
			if (!hasInfiniteMoney()) {
				balanceTable = Common.getInstance().getDatabaseManager().getDatabase().select(BalanceTable.class).where().equal(BalanceTable.USERNAME_ID_FIELD, account.getId()).and().equal(BalanceTable.CURRENCY_ID_FIELD, currency.getDatabaseID()).and().equal(BalanceTable.WORLD_NAME_FIELD, world).execute().findOne();
				if (balanceTable != null) {
					balanceTable.setBalance(balanceTable.getBalance() - amount);
				} else {
					balanceTable = new BalanceTable();
					balanceTable.setCurrencyId(currency.getDatabaseID());
					balanceTable.setUsernameId(account.getId());
					balanceTable.setWorldName(world);
					balanceTable.setBalance(amount);
				}
				Common.getInstance().getDatabaseManager().getDatabase().save(balanceTable);
				Common.getInstance().writeLog(LogInfo.WITHDRAW, cause, causeReason, this, amount, currency, world);
				result = balanceTable.getBalance();
			} else {
				result = Double.MAX_VALUE;
			}
		}
		return format(result);
	}

	/**
	 * set a certain amount of money in the account
	 * @param amount The amount of money to set
	 * @param world The World / World group we want to set money to
	 * @param currencyName The currency we want to set money to
	 * @return The new balance
	 * @deprecated use {@link #set(double, String, String, com.greatmancode.craftconomy3.Cause, String)}
	 */
	@Deprecated
	public double set(double amount, String world, String currencyName) {
		return set(amount, world, currencyName, Cause.UNKNOWN, null);
	}

	/**
	 * set a certain amount of money in the account
	 * @param amount The amount of money to set
	 * @param world The World / World group we want to set money to
	 * @param currencyName The currency we want to set money to
	 * @param cause The cause of the change.
	 * @return The new balance. If the account has infinite money. Double.MAX_VALUE is returned.
	 */
	public double set(double amount, String world, String currencyName, Cause cause, String causeReason) {
		BalanceTable balanceTable;
		double result = 0;
		amount = format(amount);
		String newWorld = Common.getInstance().getWorldGroupManager().getWorldGroupName(world);
		Currency currency = Common.getInstance().getCurrencyManager().getCurrency(currencyName);
		if (currency != null) {
			if (!hasInfiniteMoney()) {
				balanceTable = Common.getInstance().getDatabaseManager().getDatabase().select(BalanceTable.class).where().equal(BalanceTable.USERNAME_ID_FIELD, account.getId()).and().equal(BalanceTable.CURRENCY_ID_FIELD, currency.getDatabaseID()).and().equal(BalanceTable.WORLD_NAME_FIELD, newWorld).execute().findOne();
				if (balanceTable != null) {
					balanceTable.setBalance(amount);
				} else {
					balanceTable = new BalanceTable();
					balanceTable.setCurrencyId(currency.getDatabaseID());
					balanceTable.setUsernameId(account.getId());
					balanceTable.setWorldName(newWorld);
					balanceTable.setBalance(amount);
				}
				Common.getInstance().getDatabaseManager().getDatabase().save(balanceTable);
				Common.getInstance().writeLog(LogInfo.SET, cause, causeReason, this, amount, currency, newWorld);
				result = balanceTable.getBalance();
			}
		}
		return format(result);
	}

	/**
	 * Checks if we have enough money in a certain balance
	 * @param amount The amount of money to check
	 * @param worldName The World / World group we want to check
	 * @param currencyName The currency we want to check
	 * @return True if there's enough money. Else false
	 */
	public boolean hasEnough(double amount, String worldName, String currencyName) {
		boolean result = false;
		amount = format(amount);
		if (!Common.getInstance().getWorldGroupManager().worldGroupExist(worldName)) {
			worldName = Common.getInstance().getWorldGroupManager().getWorldGroupName(worldName);
		}
		Currency currency = Common.getInstance().getCurrencyManager().getCurrency(currencyName);
		if (currency != null && (getBalance(worldName, currencyName) >= amount || hasInfiniteMoney())) {
			result = true;
		}
		return result;
	}

	/**
	 * Returns the world that the player is currently in
	 * @return The world name that the player is currently in or any if he is not online/Multiworld system not enabled
	 * @deprecated Please use {@link com.greatmancode.craftconomy3.account.Account#getWorldGroupOfPlayerCurrentlyIn()}
	 */
	private String getWorldPlayerCurrentlyIn() {
		return Common.getInstance().getServerCaller().getPlayerWorld(account.getName());
	}

	/**
	 * Retrieve the world group of the player
	 * @return The worldGroup of the player.
	 */
	public String getWorldGroupOfPlayerCurrentlyIn() {
		return Common.getInstance().getWorldGroupManager().getWorldGroupName(getWorldPlayerCurrentlyIn());
	}

	/**
	 * Sets the account to have infinite money.
	 * @param infinite True if the account should have infinite money. Else false.
	 */
	public void setInfiniteMoney(boolean infinite) {
		account.setInfiniteMoney(infinite);
		Common.getInstance().getDatabaseManager().getDatabase().save(account);
	}

	/**
	 * Checks if the account have infinite money
	 * @return True if the account have infinite money. Else false.
	 */
	public boolean hasInfiniteMoney() {
		return account.hasInfiniteMoney();
	}

	/**
	 * Format the value to be something less problematic.
	 * Example: 50.00999999995 will become 50.00
	 * @param value The double to format
	 * @return The formatted double
	 */
	public static double format(double value) {
		long factor = (long) Math.pow(10, 2);
		value = value * factor;
		double tmp = Math.floor(value);
		return tmp / factor;
	}
}
