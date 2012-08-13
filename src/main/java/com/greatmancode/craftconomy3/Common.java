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
	private CommandLoader commandManager;
	private Caller serverCaller;
	private boolean databaseInitialized = false;
	private boolean currencyInitialized;

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
			sendConsoleMessage(Level.INFO, "Loading Currencies");
			initializeCurrency();
			startUp();
		}		
	}

	public void disable() {
		try {
			Common.getInstance().getDatabaseManager().getDatabase().close();
		} catch (ConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

	public CommandLoader getCommandManager() {
		return commandManager;
	}

	public Caller getServerCaller() {
		return serverCaller;
	}

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
			string.append(theAmount[0]).append(".").append(theAmount[1]).append(" ").append(name);
		}
		return string.toString();
	}

	public void initialiseDatabase() throws TableRegistrationException, ConnectionException {
		if (!databaseInitialized ) {
			sendConsoleMessage(Level.INFO, "Loading the Database manager");
				dbManager = new DatabaseManager();
				databaseInitialized = true;
				sendConsoleMessage(Level.INFO, "Loaded!");
		}
	}
	
	public void initializeCurrency() {
		if (!currencyInitialized) {
			sendConsoleMessage(Level.INFO, "Loading the currency manager");
			currencyManager = new CurrencyManager();
			currencyInitialized = true;
			sendConsoleMessage(Level.INFO, "Loaded!");
		}
	}
	
	public void startUp() {
		sendConsoleMessage(Level.INFO, "Loading the Account Handler");
		accountManager = new AccountManager();
		getServerCaller().addMultiworldGraph(getConfigurationManager().isMultiWorld());
		getServerCaller().startMetrics();
		sendConsoleMessage(Level.INFO, "Loaded!");
	}
}
