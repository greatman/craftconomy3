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
package com.greatmancode.craftconomy3.converter.converters;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import com.alta189.simplesave.Database;
import com.alta189.simplesave.DatabaseFactory;
import com.alta189.simplesave.exceptions.ConnectionException;
import com.alta189.simplesave.exceptions.TableRegistrationException;
import com.alta189.simplesave.mysql.MySQLConfiguration;
import com.alta189.simplesave.sqlite.SQLiteConfiguration;
import com.greatmancode.craftconomy3.Cause;
import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.account.Account;
import com.greatmancode.craftconomy3.converter.Converter;
import com.greatmancode.craftconomy3.database.tables.craftconomy2.AccountTable;
import com.greatmancode.craftconomy3.database.tables.craftconomy2.BalanceTable;
import com.greatmancode.craftconomy3.database.tables.craftconomy2.BankBalanceTable;
import com.greatmancode.craftconomy3.database.tables.craftconomy2.BankMemberTable;
import com.greatmancode.craftconomy3.database.tables.craftconomy2.BankTable;
import com.greatmancode.craftconomy3.database.tables.craftconomy2.CurrencyTable;

/**
 * Converter for Craftconomy 2.
 * @author greatman
 */
public class Craftconomy2 extends Converter {
	private Database db = null;

	public Craftconomy2() {
		getDbTypes().add("sqlite");
		getDbTypes().add("mysql");
	}

	@Override
	public List<String> getDbInfo() {
		if (getSelectedDbType().equals("sqlite")) {
			getDbInfoList().add("filename");
		} else if (getSelectedDbType().equals("mysql")) {
			getDbInfoList().add("address");
			getDbInfoList().add("port");
			getDbInfoList().add("username");
			getDbInfoList().add("password");
			getDbInfoList().add("database");
		}
		return getDbInfoList();
	}

	@Override
	public boolean connect() {
		boolean result = false;
		if (getSelectedDbType().equals("mysql")) {

			try {
				MySQLConfiguration config = new MySQLConfiguration();
				config.setHost(getDbConnectInfo().get("address"));
				config.setUser(getDbConnectInfo().get("username"));
				config.setPassword(getDbConnectInfo().get("password"));
				config.setDatabase(getDbConnectInfo().get("database"));
				config.setPort(Integer.parseInt(getDbConnectInfo().get("port")));
				db = DatabaseFactory.createNewDatabase(config);
			} catch (NumberFormatException e) {
				Common.getInstance().getLogger().severe("Illegal port!");
			}
		} else if (getSelectedDbType().equals("sqlite")) {
			SQLiteConfiguration config = new SQLiteConfiguration(Common.getInstance().getServerCaller().getDataFolder() + File.separator + getDbConnectInfo().get("filename"));
			db = DatabaseFactory.createNewDatabase(config);
		}

		if (db != null) {

			try {
				db.registerTable(AccountTable.class);
				db.registerTable(BalanceTable.class);
				db.registerTable(BankBalanceTable.class);
				db.registerTable(BankMemberTable.class);
				db.registerTable(BankTable.class);
				db.registerTable(CurrencyTable.class);
				db.connect();
				result = true;
			} catch (TableRegistrationException e) {
				Common.getInstance().getLogger().severe("Unable to register Craftconomy2 tables. Reason: " + e.getMessage());
			} catch (ConnectionException e) {
				Common.getInstance().getLogger().severe("Unable to connect to craftconomy2 database. Reason: " + e.getMessage());
			}
		}
		return result;
	}

	@Override
	public boolean importData(String sender) {
		boolean result = false;
		if (db != null) {
			// Import currency first
			boolean currencyResult = importCurrency(sender);
			if (currencyResult) {
				importAccounts(sender);
				importBanks(sender);
				result = true;
			} else {
				Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_RED}}No currency found. Are you sure everything is ok?");
			}
			try {
				db.close();
			} catch (ConnectionException e) {
				Common.getInstance().getLogger().severe("Error with Craftconomy2 database link. Message: " + e.getMessage());
			}
		}
		return result;
	}

	private boolean importCurrency(String sender) {
		boolean result = false;
		List<CurrencyTable> currencyList = db.select(CurrencyTable.class).execute().find();
		if (currencyList != null) {
			Iterator<CurrencyTable> currencyIterator = currencyList.iterator();
			Common.getInstance().getServerCaller().sendMessage(sender, "Importing currencies.");
			while (currencyIterator.hasNext()) {
				CurrencyTable entry = currencyIterator.next();
				// Check if the entry is valid
				boolean first = true;
				if (entry.getMinor() != null && entry.getMinorplural() != null && entry.getName() != null && entry.getPlural() != null) {
					Common.getInstance().getCurrencyManager().addCurrency(entry.getName(), entry.getPlural(), entry.getMinor(), entry.getMinorplural(), 0.0, "", true);
					// TODO: Need better than that...
					if (first) {
						Common.getInstance().getCurrencyManager().setDefault(Common.getInstance().getCurrencyManager().getCurrency(entry.getName()).getDatabaseID());
						first = false;
					}
				} else {
					Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_RED}}A invalid currency has been caught. Information: name: " + entry.getName() + " plural:" + entry.getPlural() + " minor:" + entry.getMinor() + " minorplural:" + entry.getMinorplural());
				}
			}
			result = true;
			Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}Currencies imported!");
		}
		return result;
	}

	private void importAccounts(String sender) {
		// Account importing
		List<AccountTable> accountList = db.select(AccountTable.class).execute().find();
		if (accountList != null) {
			int i = 0;
			Iterator<AccountTable> accountIterator = accountList.iterator();
			while (accountIterator.hasNext()) {
				AccountTable entry = accountIterator.next();
				Common.getInstance().getAccountManager().getAccount(entry.getUsername());
				List<BalanceTable> balanceList = db.select(BalanceTable.class).where().equal("username_id", entry.getId()).execute().find();
				if (balanceList != null) {
					Iterator<BalanceTable> balanceIterator = balanceList.iterator();
					while (balanceIterator.hasNext()) {
						BalanceTable balanceEntry = balanceIterator.next();
						CurrencyTable currency = db.select(CurrencyTable.class).where().equal("id", balanceEntry.getCurrencyId()).execute().findOne();
						if (currency != null) {
							Common.getInstance().getAccountManager().getAccount(entry.getUsername()).set(balanceEntry.getBalance(), balanceEntry.getWorldName(), currency.getName(), Cause.CONVERT, null);
						}
					}
				}
				if (i % ALERT_EACH_X_ACCOUNT == 0) {
					Common.getInstance().getServerCaller().sendMessage(sender, i + " of " + accountList.size() + "{{DARK_GREEN}} accounts imported.");
				}
				i++;
			}
			Common.getInstance().getServerCaller().sendMessage(sender, i + " of  " + accountList.size() + "{{DARK_GREEN}}accounts imported.");
		}
	}

	private void importBanks(String sender) {
		// Bank importing
		List<BankTable> bankList = db.select(BankTable.class).execute().find();
		if (bankList != null) {
			int i = 0;
			Iterator<BankTable> bankIterator = bankList.iterator();
			while (bankIterator.hasNext()) {
				BankTable entry = bankIterator.next();
				Common.getInstance().getAccountManager().getAccount(Account.BANK_PREFIX + entry.getName()).getAccountACL().set(entry.getOwner(), true, true, true, true, true);
				List<BankBalanceTable> bankBalanceList = db.select(BankBalanceTable.class).where().equal("bank_id", entry.getId()).execute().find();
				if (bankBalanceList != null) {
					Iterator<BankBalanceTable> bankBalanceIterator = bankBalanceList.iterator();
					while (bankBalanceIterator.hasNext()) {
						BankBalanceTable balanceEntry = bankBalanceIterator.next();
						CurrencyTable currency = db.select(CurrencyTable.class).where().equal("id", balanceEntry.getCurrencyId()).execute().findOne();
						if (currency != null) {
							Common.getInstance().getAccountManager().getAccount(Account.BANK_PREFIX + entry.getName()).set(balanceEntry.getBalance(), balanceEntry.getWorldName(), currency.getName(), Cause.CONVERT, null);
						}
					}
				}

				// Adding members
				List<BankMemberTable> bankMemberList = db.select(BankMemberTable.class).where().equal("bank_id", entry.getId()).execute().find();
				if (bankMemberList != null) {
					Iterator<BankMemberTable> bankMemberIterator = bankMemberList.iterator();
					while (bankMemberIterator.hasNext()) {
						BankMemberTable memberEntry = bankMemberIterator.next();
						Common.getInstance().getAccountManager().getAccount(Account.BANK_PREFIX + entry.getName()).getAccountACL().set(memberEntry.getPlayerName(), true, true, false, true, false);
					}
				}
				if (i % ALERT_EACH_X_ACCOUNT == 0) {
					Common.getInstance().getServerCaller().sendMessage(sender, i + " of  " + bankList.size() + "{{DARK_GREEN}} bank accounts imported.");
				}
				i++;
			}
			Common.getInstance().getServerCaller().sendMessage(sender, i + " of  " + bankList.size() + "{{DARK_GREEN}} bank accounts imported.");
		}
	}
}
