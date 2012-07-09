package com.greatmancode.craftconomy3;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.greatmancode.craftconomy3.account.AccountHandler;
import com.greatmancode.craftconomy3.commands.CommandManager;
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
	private AccountHandler accountHandler = null;
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
		} else
		{
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
		accountHandler = new AccountHandler();

		sendConsoleMessage(Level.INFO, "Loading commands");
		commandManager = new CommandManager();

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

	public CommandManager getCommandManager() {
		return commandManager;
	}

	public Caller getServerCaller() {
		return serverCaller;
	}
	
	public String format(String worldName, Currency currency, double balance) {
		StringBuilder string = new StringBuilder();
		
		if (worldName != null)
		{
			//We put the world name if the conf is true
			if (getConfigurationManager().getConfig().getBoolean("System.Default.Currency.MultiWorld")) {
				string.append(worldName  + ":").append(" ");
			}
		}
		
		
		//We removes some cents if it's something like 20.20381 it would set it to 20.20
		String[] theAmount = Double.toString(balance).split("\\.");
		if (theAmount[1].length() > 2)
		{
			theAmount[1] = theAmount[1].substring(0,2);
		}
		
		String name = currency.getName();
		if (Long.parseLong(theAmount[0]) > 1)
		{
			name = currency.getPlural();
		}
		
		//Do we seperate money and dollar or not? 
		if (getConfigurationManager().getConfig().getBoolean("System.Default.Currency.LongMode")) {
			String subName = currency.getMinor();
			if (Long.parseLong(theAmount[1]) > 1)
			{
				subName = currency.getMinorPlural();
			}
			string.append(theAmount[0]).append(" ").append(name).append(" ").append(theAmount[1]).append(" ").append(subName);
		}
		else
		{
			string.append(theAmount[0]).append(".").append(theAmount[1]).append(" ").append(name);
		}
		return string.toString();
	}

}
