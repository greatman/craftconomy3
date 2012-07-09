package com.greatmancode.craftconomy3.account;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.currency.Currency;
import com.greatmancode.craftconomy3.currency.CurrencyManager;
import com.greatmancode.craftconomy3.database.tables.AccountTable;
import com.greatmancode.craftconomy3.database.tables.BalanceTable;

public class Account {

	private AccountTable account;

	public Account(String name) {
		AccountTable result = Common.getInstance().getDatabaseManager().getDatabase().select(AccountTable.class).where().equal("username", name).execute().findOne();
		if (result == null) {
			result = new AccountTable();
			result.name = name;
			Common.getInstance().getDatabaseManager().getDatabase().save(result);
			BalanceTable balance = new BalanceTable();
			balance.username_id = result.id;
			balance.currency_id = CurrencyManager.DefaultCurrencyID;
			balance.balance = Common.getInstance().getConfigurationManager().getConfig().getDouble("System.Default.Account.Holdings");
			Common.getInstance().getDatabaseManager().getDatabase().save(balance);
		}
		account = result;
	}

	// TODO
	public List<Balance> getAllBalance() {
		List<Balance> balanceList = new ArrayList<Balance>();
		Iterator<BalanceTable> list = Common.getInstance().getDatabaseManager().getDatabase().select(BalanceTable.class).where().equal("username_id", account.id).execute().find().iterator();
		while (list.hasNext()) {
			BalanceTable table = list.next();
			balanceList.add(new Balance(table.worldName, Common.getInstance().getCurrencyManager().getCurrency(table.currency_id), table.balance));
		}
		return balanceList;
	}

	public List<Balance> getAllWorldBalance(String world) {
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
	 * @param currencyName
	 * @return
	 */
	public double getBalance(String world, String currencyName) {
		double balance = Double.MIN_NORMAL;
		Currency currency = Common.getInstance().getCurrencyManager().getCurrency(currencyName);
		if (currency != null) {
			BalanceTable balanceTable = Common.getInstance().getDatabaseManager().getDatabase().select(BalanceTable.class).where().equal("username_id", account.id).and().equal("balance_id", currency.getDatabaseID()).execute().findOne();
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
		double balance = 0.0;
		Currency currency = Common.getInstance().getCurrencyManager().getCurrency(currencyName);
		if (currency != null) {
			BalanceTable balanceTable = Common.getInstance().getDatabaseManager().getDatabase().select(BalanceTable.class).where().equal("username_id", account.id).and().equal("balance_id", currency.getDatabaseID()).execute().findOne();
			if (balanceTable != null) {
				balance += amount;
				balanceTable.balance = balance;

			} else {
				balanceTable = new BalanceTable();
				balanceTable.currency_id = currency.getDatabaseID();
				balanceTable.username_id = account.id;
				balanceTable.worldName = world;
				balanceTable.balance = amount;
			}
			Common.getInstance().getDatabaseManager().getDatabase().save(balanceTable);
		}
		return balance;
	}

	/**
	 * withdraw a certain amount of money in the account
	 * @param amount The amount of money to withdraw
	 * @param world The World we want to withdraw money from
	 * @param currencyName The currency we want to withdraw money from
	 * @return The new balance
	 */
	public double withdraw(double amount, String world, String currencyName) {
		double balance = 0.0;
		Currency currency = Common.getInstance().getCurrencyManager().getCurrency(currencyName);
		if (currency != null) {
			BalanceTable balanceTable = Common.getInstance().getDatabaseManager().getDatabase().select(BalanceTable.class).where().equal("username_id", account.id).and().equal("balance_id", currency.getDatabaseID()).execute().findOne();
			if (balanceTable != null) {
				balance -= amount;
				balanceTable.balance = balance;

			} else {
				balanceTable = new BalanceTable();
				balanceTable.currency_id = currency.getDatabaseID();
				balanceTable.username_id = account.id;
				balanceTable.worldName = world;
				balanceTable.balance = amount;
			}
			Common.getInstance().getDatabaseManager().getDatabase().save(balanceTable);
		}
		return balance;
	}

	public boolean hasEnough(double amount, String worldName, String currencyName) {
		boolean result = false;
		Currency currency = Common.getInstance().getCurrencyManager().getCurrency(currencyName);
		if (currency != null) {
			if (getBalance(worldName, currencyName) >= amount) {
				result = true;
			}
		}
		return result;
	}
}
