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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.alta189.simplesave.exceptions.ConnectionException;
import com.alta189.simplesave.exceptions.TableRegistrationException;
import com.greatmancode.craftconomy3.account.AccountManager;
import com.greatmancode.craftconomy3.commands.CommandLoader;
import com.greatmancode.craftconomy3.configuration.ConfigurationManager;
import com.greatmancode.craftconomy3.currency.Currency;
import com.greatmancode.craftconomy3.currency.CurrencyManager;
import com.greatmancode.craftconomy3.database.DatabaseManager;
import com.greatmancode.craftconomy3.payday.PayDayManager;

/**
 * The core of Craftconomy. Every requests pass through this class
 * @author greatman
 *
 */
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
	private PayDayManager paydayManager = null;
	
	private CommandLoader commandManager;
	private Caller serverCaller;
	private boolean databaseInitialized = false;
	private boolean currencyInitialized;
	private boolean initialized = false;

	/**
	 * Loads the Common core.
	 * @param isBukkit If the server is Craftbukkit or not
	 * @param log The Logger associated with this plugin.
	 */
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

	/**
	 * Initialize the Common core.
	 */
	public void initialize() {
		if (!initialized) {
			sendConsoleMessage(Level.INFO, "Starting up!");
			sendConsoleMessage(Level.INFO, "Loading the Configuration");
			config = new ConfigurationManager();
			config.initialize();
			sendConsoleMessage(Level.INFO, "Loading commands");
			commandManager = new CommandLoader();
			if (config.getConfig().getBoolean("System.Setup")) {
				sendConsoleMessage(Level.WARNING, "Loading Craftconomy in setup mode. Please type /ccsetup to start the setup.");
			} else {
				try {
					initialiseDatabase();
				} catch (Exception e) {
					sendConsoleMessage(Level.SEVERE, "A error occured while trying to connect to the database. Message received: " + e.getMessage());
					getServerCaller().disablePlugin();
					return;
				}
				initializeCurrency();
				sendConsoleMessage(Level.INFO, "Loading default settings.");
				getConfigurationManager().loadDefaultSettings();
				sendConsoleMessage(Level.INFO, "Default settings loaded!");
				startUp();
				sendConsoleMessage(Level.INFO, "Ready!");
			}
			initialized = true;
		}
		
	}

	/**
	 * Disable the plugin.
	 */
	void disable() {
		try {
			Common.getInstance().getDatabaseManager().getDatabase().close();
		} catch (ConnectionException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Checks if the server is a Craftbukkit server or something else.
	 * @return True if the server is a Craftbukkit server. Else false for Spout Server
	 */
	public static boolean isBukkit() {
		return isBukkit;
	}

	/**
	 * Retrieve the logger associated with this plugin.
	 * @return The logger instance.
	 */
	public Logger getLogger() {
		return log;
	}

	/**
	 * Sends a message to the console through the Logge.r
	 * @param level The log level to show.
	 * @param msg The message to send.
	 */
	public void sendConsoleMessage(Level level, String msg) {
		getLogger().log(level, msg);
	}

	/**
	 * Retrieve the instance of Common. Need to go through that to access any managers.
	 * @return The Common instance.
	 */
	public static Common getInstance() {
		return instance;
	}

	/**
	 * Retrieve the Account Manager.
	 * @return The Account Manager instance or null if the manager is not initialized.
	 */
	public AccountManager getAccountManager() {
		return accountManager;
	}

	/**
	 * Retrieve the Configuration Manager.
	 * @return The Configuration Manager instance or null if the manager is not initialized.
	 */
	public ConfigurationManager getConfigurationManager() {
		return config;
	}

	/**
	 * Retrieve the Database Manager.
	 * @return The Database Manager instance or null if the manager is not initialized.
	 */
	public DatabaseManager getDatabaseManager() {
		return dbManager;
	}

	/**
	 * Retrieve the Currency Manager.
	 * @return The Currency Manager instance or null if the manager is not initialized.
	 */
	public CurrencyManager getCurrencyManager() {
		return currencyManager;
	}

	/**
	 * Retrieve the Command Manager.
	 * @return The Command Manager instance or null if the manager is not initialized.
	 */
	public CommandLoader getCommandManager() {
		return commandManager;
	}
	
	/**
	 * Retrieve the Payday Manager
	 * @return The Command Manager instance or null if the manager is not initialized.
	 */
	public PayDayManager getPaydayManager() {
		return paydayManager;
	}

	/**
	 * Retrieve the Server Caller.
	 * @return The Server Caller instance or null if the caller is not initialized.
	 */
	public Caller getServerCaller() {
		return serverCaller;
	}

	/**
	 * Format a balance to a readable string.
	 * @param worldName The world Name associated with this balance
	 * @param currency The currency instance associated with this balance.
	 * @param balance The balance.
	 * @return A pretty String showing the balance.
	 */
	public String format(String worldName, Currency currency, double balance) {
		StringBuilder string = new StringBuilder();

		if (worldName != null) {
			// We put the world name if the conf is true
			if (getConfigurationManager().isMultiWorld()) {
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
		if (getConfigurationManager().isLongmode()) {
			String subName = currency.getMinor();
			if (Long.parseLong(theAmount[1]) > 1) {
				subName = currency.getMinorPlural();
			}
			string.append(theAmount[0]).append(" ").append(name).append(" ").append(theAmount[1]).append(" ").append(subName);
		} else {
			string.append(theAmount[0]).append(",").append(theAmount[1]).append(" ").append(name);
		}
		return string.toString();
	}

	/**
	 * Initialize the database Manager
	 * @throws TableRegistrationException
	 * @throws ConnectionException
	 */
	public void initialiseDatabase() throws TableRegistrationException, ConnectionException {
		if (!databaseInitialized) {
			sendConsoleMessage(Level.INFO, "Loading the Database Manager");
			dbManager = new DatabaseManager();
			databaseInitialized = true;
			sendConsoleMessage(Level.INFO, "Database Manager Loaded!");
		}
	}

	/**
	 * Initialize the Currency Manager.
	 */
	public void initializeCurrency() {
		if (!currencyInitialized) {
			sendConsoleMessage(Level.INFO, "Loading the Currency Manager");
			currencyManager = new CurrencyManager();
			currencyInitialized = true;
			sendConsoleMessage(Level.INFO, "Currency Manager Loaded!");
		}
	}

	/**
	 * Initialize the Account & PayDay Manager
	 */
	public void startUp() {
		sendConsoleMessage(Level.INFO, "Loading the Account Manager");
		accountManager = new AccountManager();
		getServerCaller().addMultiworldGraph(getConfigurationManager().isMultiWorld());
		getServerCaller().startMetrics();
		sendConsoleMessage(Level.INFO, "Account Manager Loaded!");
		sendConsoleMessage(Level.INFO, "Loading the PayDay manager.");
		paydayManager = new PayDayManager();
		sendConsoleMessage(Level.INFO, "PayDay Manager loaded!");
		
	}

	/**
	 * Write a transaction to the Log.
	 * @param info The type of transaction to log.
	 * @param username The username that did this transaction.
	 * @param amount The amount of money in this transaction.
	 * @param currency The currency associated with this transaction
	 * @param worldName The world name associated with this transaction
	 */
	public void writeLog(LogInfo info, String username, double amount, Currency currency, String worldName) {
		if (getConfigurationManager().getConfig().getBoolean("System.Logging.Enabled")) {
			try {
				PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(new File(getServerCaller().getDataFolder(), "craftconomy.log"), true)));

				out.println(info.toString() + ": User: " + username + " Currency: " + currency.getName() + " World: " + worldName + " Amount:" + amount);
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
