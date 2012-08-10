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
package com.greatmancode.craftconomy3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.alta189.simplesave.Database;
import com.alta189.simplesave.DatabaseFactory;
import com.alta189.simplesave.exceptions.ConnectionException;
import com.alta189.simplesave.exceptions.TableRegistrationException;
import com.alta189.simplesave.mysql.MySQLConfiguration;
import com.alta189.simplesave.sqlite.SQLiteConfiguration;
import com.greatmancode.craftconomy3.account.AccountManager;
import com.greatmancode.craftconomy3.commands.CommandManager;
import com.greatmancode.craftconomy3.configuration.ConfigurationManager;
import com.greatmancode.craftconomy3.currency.Currency;
import com.greatmancode.craftconomy3.currency.CurrencyManager;
import com.greatmancode.craftconomy3.database.DatabaseManager;
import com.greatmancode.craftconomy3.database.tables.AccessTable;
import com.greatmancode.craftconomy3.database.tables.AccountTable;
import com.greatmancode.craftconomy3.database.tables.BalanceTable;
import com.greatmancode.craftconomy3.database.tables.iconomy.iConomyTable;

public class Common {

	private Logger log = null;
	private static Common instance = null;

	// True = Bukkit, False = Spout
	private static boolean isBukkit = false;

	// Managers
	private AccountManager accountManager = null;
	private ConfigurationManager config = null;
	private CurrencyManager currencyManager = null;

	private DatabaseManager dbManager = null;
	private CommandManager commandManager;
	private Caller serverCaller;

	public Common(boolean isBukkit, Logger log) {
		instance = this;
		Common.isBukkit = isBukkit;
		this.log = log;
		if (isBukkit()) {
			serverCaller = new BukkitCaller();
		} else {
			serverCaller = new SpoutCaller();
		}
	}

	public void initialize() {
		sendConsoleMessage(Level.INFO, "Starting up!");
		sendConsoleMessage(Level.INFO, "Loading the Configuration");
		config = new ConfigurationManager();
		config.initialize();
		sendConsoleMessage(Level.INFO, "Connecting to database");
		try {
			dbManager = new DatabaseManager();
		} catch (Exception e) {
			sendConsoleMessage(Level.SEVERE, "A error occured while trying to connect to the database. Message received: " + e.getMessage());
			getServerCaller().disablePlugin();
			return;
		}
		sendConsoleMessage(Level.INFO, "Loading Currencies");
		currencyManager = new CurrencyManager();
		sendConsoleMessage(Level.INFO, "Loading the Account Handler");
		accountManager = new AccountManager();

		sendConsoleMessage(Level.INFO, "Loading commands");
		commandManager = new CommandManager();

		// We check if we want to convert
		if (config.getConfig().getBoolean("System.Convert.Enabled")) {
			// First, we delete the whole system
			List<AccountTable> accountTable = getDatabaseManager().getDatabase().select(AccountTable.class).execute().find();
			getDatabaseManager().getDatabase().remove(accountTable);
			List<AccessTable> accessTable = getDatabaseManager().getDatabase().select(AccessTable.class).execute().find();
			getDatabaseManager().getDatabase().remove(accessTable);
			List<BalanceTable> balanceTable = getDatabaseManager().getDatabase().select(BalanceTable.class).execute().find();
			getDatabaseManager().getDatabase().remove(balanceTable);

			String defaultWorld = Common.getInstance().getServerCaller().getDefaultWorld();
			if (getConfigurationManager().getConfig().getString("System.Convert.Type").equalsIgnoreCase("iconomy")) {
				if (getConfigurationManager().getConfig().getString("System.Convert.DatabaseType").equalsIgnoreCase("minidb")) {
					File dbFile = new File(this.getServerCaller().getDataFolder(), getConfigurationManager().getConfig().getString("System.Convert.Address"));
					if (dbFile.exists()) {
						try {
							BufferedReader in = new BufferedReader(new FileReader(dbFile));
							String str;
							while ((str = in.readLine()) != null) {
								String[] info = str.split(" ");
								if (info.length >= 2) {
									String[] balance = info[1].split(":");
									try {
										getAccountManager().getAccount(info[0]).set(Double.parseDouble(balance[1]), defaultWorld, getCurrencyManager().getCurrency(CurrencyManager.DefaultCurrencyID).getName());
									} catch (NumberFormatException e) {
										sendConsoleMessage(Level.SEVERE, "User " + info[0] + " have a invalid balance" + balance[1]);
									}

								}
							}
							sendConsoleMessage(Level.INFO, "Done converting!");
						} catch (FileNotFoundException e) {
							sendConsoleMessage(Level.SEVERE, "Database file for Conversion not found! Stopping conversion. Error is: " + e.getMessage());
						} catch (IOException e) {
							sendConsoleMessage(Level.SEVERE, "IOException. Stopping Conversion. Error is:" + e.getMessage());
						}
					}
				} else if (getConfigurationManager().getConfig().getString("System.Convert.DatabaseType").equalsIgnoreCase("mysql") || getConfigurationManager().getConfig().getString("System.Convert.DatabaseType").equalsIgnoreCase("sqlite")) {
					Database db = null;
					if (getConfigurationManager().getConfig().getString("System.Convert.DatabaseType").equalsIgnoreCase("mysql")) {
						MySQLConfiguration config = new MySQLConfiguration();
						config.setHost(Common.getInstance().getConfigurationManager().getConfig().getString("System.Convert.Address"));
						config.setUser(Common.getInstance().getConfigurationManager().getConfig().getString("System.Convert.Username"));
						config.setPassword(Common.getInstance().getConfigurationManager().getConfig().getString("System.Convert.Password"));
						config.setDatabase(Common.getInstance().getConfigurationManager().getConfig().getString("System.Convert.Db"));
						config.setPort(Common.getInstance().getConfigurationManager().getConfig().getInt("System.Convert.Port"));
						db = DatabaseFactory.createNewDatabase(config);
					} else {
						SQLiteConfiguration config = new SQLiteConfiguration(Common.getInstance().getConfigurationManager().getDataFolder() + File.separator + getConfigurationManager().getConfig().getString("System.Convert.Address"));
						db = DatabaseFactory.createNewDatabase(config);
					}

					try {
						db.registerTable(iConomyTable.class);
						db.connect();
						List<iConomyTable> icoList = db.select(iConomyTable.class).execute().find();
						if (icoList != null && icoList.size() > 0) {
							Iterator<iConomyTable> icoListIterator = icoList.iterator();
							while (icoListIterator.hasNext()) {
								iConomyTable entry = icoListIterator.next();
								getAccountManager().getAccount(entry.username).set(entry.balance, defaultWorld, getCurrencyManager().getCurrency(CurrencyManager.DefaultCurrencyID).getName());
								
							}
							sendConsoleMessage(Level.INFO, "Done converting!");
						} else {
							sendConsoleMessage(Level.INFO, "No account found for converting!");
						}
					} catch (TableRegistrationException e) {
						sendConsoleMessage(Level.SEVERE, "Error while initializing the database connection. Error is: " + e.getMessage());
					} catch (ConnectionException e) {
						sendConsoleMessage(Level.SEVERE, "Error while trying to connect to the database. Error is: " + e.getMessage());
					}
				}
			}

		}
		sendConsoleMessage(Level.INFO, "Loaded!");
	}

	public static boolean isBukkit() {
		return isBukkit;
	}

	public Logger getLogger() {
		return log;
	}

	public void sendConsoleMessage(Level level, String msg) {
		getLogger().log(level, msg);
	}

	public static Common getInstance() {
		return instance;
	}

	public AccountManager getAccountManager() {
		return accountManager;
	}

	public ConfigurationManager getConfigurationManager() {
		return config;
	}

	public DatabaseManager getDatabaseManager() {
		return dbManager;
	}

	public CurrencyManager getCurrencyManager() {
		return currencyManager;
	}

	public CommandManager getCommandManager() {
		return commandManager;
	}

	public Caller getServerCaller() {
		return serverCaller;
	}

	public String format(String worldName, Currency currency, double balance) {
		StringBuilder string = new StringBuilder();

		if (worldName != null) {
			// We put the world name if the conf is true
			if (getConfigurationManager().getConfig().getBoolean("System.Default.Currency.MultiWorld")) {
				string.append(worldName + ":").append(" ");
			}
		}

		// We removes some cents if it's something like 20.20381 it would set it to 20.20
		String[] theAmount = Double.toString(balance).split("\\.");
		if (theAmount[1].length() > 2) {
			theAmount[1] = theAmount[1].substring(0, 2);
		}

		String name = currency.getName();
		if (Long.parseLong(theAmount[0]) > 1) {
			name = currency.getPlural();
		}

		// Do we seperate money and dollar or not?
		if (getConfigurationManager().getConfig().getBoolean("System.Default.Currency.LongMode")) {
			String subName = currency.getMinor();
			if (Long.parseLong(theAmount[1]) > 1) {
				subName = currency.getMinorPlural();
			}
			string.append(theAmount[0]).append(" ").append(name).append(" ").append(theAmount[1]).append(" ").append(subName);
		} else {
			string.append(theAmount[0]).append(".").append(theAmount[1]).append(" ").append(name);
		}
		return string.toString();
	}

}
