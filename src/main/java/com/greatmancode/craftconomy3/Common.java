package com.greatmancode.craftconomy3;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.greatmancode.craftconomy3.account.AccountHandler;
import com.greatmancode.craftconomy3.configuration.ConfigurationManager;
import com.greatmancode.craftconomy3.currency.CurrencyManager;
import com.greatmancode.craftconomy3.database.DatabaseManager;

public class Common {

	private Logger log = null;
	private static Common instance = null;

	// True = Bukkit, False = Spout
	private static boolean isBukkit = false;

	// Managers
	private AccountHandler accountHandler = null;
	private ConfigurationManager config = null;
	private CurrencyManager currencyManager = null;

	private DatabaseManager dbManager = null;

	public Common(boolean isBukkit) {
		instance = this;
		Common.isBukkit = isBukkit;
		if (isBukkit) {
			log = BukkitLoader.getInstance().getLogger();
		} else {
			log = SpoutLoader.getInstance().getLogger();
		}

		sendConsoleMessage(Level.INFO, "Starting up!");
		sendConsoleMessage(Level.INFO, "Loading the Configuration");
		config = new ConfigurationManager();
		sendConsoleMessage(Level.INFO, "Connecting to database");
		try {
			dbManager = new DatabaseManager();
		} catch (Exception e) {
			sendConsoleMessage(Level.SEVERE, "A error occured while trying to connect to the database. Message received: " + e.getMessage());
			if (isBukkit()) {
				BukkitLoader.getInstance().getPluginLoader().disablePlugin(BukkitLoader.getInstance());
			} else {
				SpoutLoader.getInstance().getPluginLoader().disablePlugin(SpoutLoader.getInstance());
			}
			return;
		}
		sendConsoleMessage(Level.INFO, "Loading Currencies");
		currencyManager = new CurrencyManager();
		sendConsoleMessage(Level.INFO, "Loading the Account Handler");
		accountHandler = new AccountHandler();
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

	public AccountHandler getAccountHandler() {
		return accountHandler;
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

}
