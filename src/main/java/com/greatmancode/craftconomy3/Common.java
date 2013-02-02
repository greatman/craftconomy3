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
import java.math.BigDecimal;
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
import com.greatmancode.craftconomy3.events.EventManager;
import com.greatmancode.craftconomy3.groups.WorldGroupsManager;
import com.greatmancode.craftconomy3.language.LanguageManager;
import com.greatmancode.craftconomy3.payday.PayDayManager;
import com.greatmancode.craftconomy3.utils.Metrics;
import com.greatmancode.craftconomy3.utils.Metrics.Graph;
import com.greatmancode.craftconomy3.utils.VersionChecker;

/**
 * The core of Craftconomy. Every requests pass through this class
 * @author greatman
 */
public class Common {
	private Logger log = null;
	private static Common instance = null;
	// Managers
	private AccountManager accountManager = null;
	private ConfigurationManager config = null;
	private CurrencyManager currencyManager = null;
	private DatabaseManager dbManager = null;
	private PayDayManager paydayManager = null;
	private EventManager eventManager = null;
	private LanguageManager languageManager = null;
	private WorldGroupsManager worldGroupManager = null;
	private CommandLoader commandManager;

	private Caller serverCaller;
	private VersionChecker versionChecker = null;
	private boolean databaseInitialized = false;
	private boolean currencyInitialized;
	private static boolean initialized = false;
	private Metrics metrics = null;

	/**
	 * Loads the Common core.
	 * @param loader The plugin Loader.
	 * @param log The Logger associated with this plugin.
	 */
	public Common(Loader loader, Logger log) {
		instance = this;
		this.log = log;
		if (loader.getServerType().equals(ServerType.BUKKIT)) {
			serverCaller = new BukkitCaller(loader);
		} else if (loader.getServerType().equals(ServerType.SPOUT)) {
			serverCaller = new SpoutCaller(loader);
		} else if (loader.getServerType().equals(ServerType.UNIT_TEST)) {
			serverCaller = new UnitTestCaller();
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
			config.initialize(Common.getInstance().getServerCaller().getDataFolder(), "config.yml");
			languageManager = new LanguageManager();
			try {
				metrics = new Metrics("Craftconomy", this.getServerCaller().getPluginVersion());
			} catch (IOException e) {
				this.getLogger().log(Level.SEVERE, String.format(getLanguageManager().getString("metric_start_error"), e.getMessage()));
			}
			if (getConfigurationManager().getConfig().getBoolean("System.CheckNewVersion")) {
				sendConsoleMessage(Level.INFO, getLanguageManager().getString("checking_new_version"));
				versionChecker = new VersionChecker(Common.getInstance().getServerCaller().getPluginVersion());
				if (versionChecker.isOld()) {
					sendConsoleMessage(Level.WARNING, String.format(getLanguageManager().getString("running_old_version"), versionChecker.getNewVersion()));
				}
			}
			sendConsoleMessage(Level.INFO, "Loading listeners.");
			eventManager = new EventManager();
			sendConsoleMessage(Level.INFO, "Loading commands");
			Common.getInstance().getServerCaller().registerPermission("craftconomy.*");
			commandManager = new CommandLoader();
			commandManager.initialize();
			if (config.getConfig().getBoolean("System.Setup")) {
				SetupWizard.setState(SetupWizard.valueOf(config.getConfig().getString("System.SetupStep")));
				if (SetupWizard.getState().equals(SetupWizard.DATABASE_SETUP)) {
					try {
						initialiseDatabase();
					} catch (Exception e) {
						sendConsoleMessage(Level.SEVERE, String.format(getLanguageManager().getString("database_connect_error"), e.getMessage()));
						getServerCaller().disablePlugin();
						e.printStackTrace();
						return;
					}
				}
				if (SetupWizard.getState().equals(SetupWizard.BASIC_SETUP)) {
					initializeCurrency();
					sendConsoleMessage(Level.INFO, getLanguageManager().getString("default_settings_loaded"));
				}
				if (SetupWizard.getState().equals(SetupWizard.CONVERT_SETUP)) {
					getConfigurationManager().loadDefaultSettings();
					startUp();
				}
				sendConsoleMessage(Level.WARNING, getLanguageManager().getString("loaded_setup_mode"));
			} else {
				try {
					initialiseDatabase();
				} catch (Exception e) {
					sendConsoleMessage(Level.SEVERE, String.format(getLanguageManager().getString("database_connect_error"), e.getMessage()));
					getServerCaller().disablePlugin();
					return;
				}
				initializeCurrency();
				sendConsoleMessage(Level.INFO, getLanguageManager().getString("loading_default_settings"));
				getConfigurationManager().loadDefaultSettings();
				sendConsoleMessage(Level.INFO, getLanguageManager().getString("default_settings_loaded"));
				startUp();
				sendConsoleMessage(Level.INFO, getLanguageManager().getString("ready"));
			}
			initialized = true;
		}
	}

	/**
	 * Disable the plugin.
	 */
	void disable() {
		if (getDatabaseManager() != null && getDatabaseManager().getDatabase() != null) {
			getLogger().info(getLanguageManager().getString("closing_db_link"));
			try {
				getDatabaseManager().getDatabase().close();
			} catch (ConnectionException e) {
				this.getLogger().severe(String.format(getLanguageManager().getString("unable_close_db_link"), e.getMessage()));
			}
		}
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
	 * @param format the display format to use
	 * @return A pretty String showing the balance. Returns a empty string if currency is invalid.
	 */
	public String format(String worldName, Currency currency, double balance, DisplayFormat format) {
		StringBuilder string = new StringBuilder();

		if (worldName != null && getConfigurationManager().isMultiWorld()) {
			// We put the world name if the conf is true
			string.append(worldName + ":").append(" ");
		}
		if (currency != null) {
			// We removes some cents if it's something like 20.20381 it would set it
			// to 20.20

			String[] theAmount = BigDecimal.valueOf(balance).toPlainString().split("\\.");
			String name = currency.getName();
			if (Long.parseLong(theAmount[0]) > 1) {
				name = currency.getPlural();
			}
			String coin;
			if (theAmount.length == 2) {
				if (theAmount[1].length() >= 2) {
					coin = theAmount[1].substring(0, 2);
				} else {
					coin = theAmount[1] + "0";
				}
			} else {
				coin = "0";
			}
			// Do we seperate money and dollar or not?
			if (format == DisplayFormat.LONG) {
				String subName = currency.getMinor();
				if (Long.parseLong(coin) > 1) {
					subName = currency.getMinorPlural();
				}
				string.append(theAmount[0]).append(" ").append(name).append(" ").append(coin).append(" ").append(subName);
			} else if (format == DisplayFormat.SMALL) {
				string.append(theAmount[0]).append(".").append(coin).append(" ").append(name);
			} else if (format == DisplayFormat.SIGN) {
				string.append(currency.getSign()).append(theAmount[0]).append(".").append(coin);
			} else if (format == DisplayFormat.MAJOR_ONLY) {
				string.append(theAmount[0]).append(" ").append(name);
			}
		}
		return string.toString();
	}

	/**
	 * Format a balance to a readable string with the default formatting.
	 * @param worldName The world Name associated with this balance
	 * @param currency The currency instance associated with this balance.
	 * @param balance The balance.
	 * @return A pretty String showing the balance. Returns a empty string if currency is invalid.
	 */
	public String format(String worldName, Currency currency, double balance) {
		return format(worldName, currency, balance, getConfigurationManager().getDisplayFormat());
	}

	/**
	 * Initialize the database Manager
	 * @throws TableRegistrationException
	 * @throws ConnectionException
	 */
	public void initialiseDatabase() throws TableRegistrationException, ConnectionException {
		if (!databaseInitialized) {
			sendConsoleMessage(Level.INFO, getLanguageManager().getString("loading_database_manager"));
			dbManager = new DatabaseManager();
			databaseInitialized = true;
			sendConsoleMessage(Level.INFO, getLanguageManager().getString("database_manager_loaded"));
		}
	}

	/**
	 * Initialize the Currency Manager.
	 */
	public void initializeCurrency() {
		if (!currencyInitialized) {
			sendConsoleMessage(Level.INFO, getLanguageManager().getString("loading_currency_manager"));
			currencyManager = new CurrencyManager();
			currencyInitialized = true;
			sendConsoleMessage(Level.INFO, getLanguageManager().getString("currency_manager_loaded"));
		}
	}

	/**
	 * Initialize the Account & PayDay Manager
	 */
	public void startUp() {
		sendConsoleMessage(Level.INFO, getLanguageManager().getString("loading_account_manager"));
		accountManager = new AccountManager();
		addMetricsGraph("Multiworld", getConfigurationManager().isMultiWorld());
		startMetrics();
		sendConsoleMessage(Level.INFO, getLanguageManager().getString("account_manager_loaded"));
		sendConsoleMessage(Level.INFO, getLanguageManager().getString("loading_payday_manager"));
		paydayManager = new PayDayManager();
		sendConsoleMessage(Level.INFO, getLanguageManager().getString("payday_manager_loaded"));
		eventManager = new EventManager();
		worldGroupManager = new WorldGroupsManager();
		sendConsoleMessage(Level.INFO, getLanguageManager().getString("world_group_manager_loaded"));
	}

	/**
	 * Add a graph to Metrics
	 * @param title The title of the Graph
	 * @param value The value of the entry
	 */
	public void addMetricsGraph(String title, String value) {
		Graph graph = metrics.createGraph(title);
		graph.addPlotter(new Metrics.Plotter(value) {
			@Override
			public int getValue() {
				return 1;
			}
		});
	}

	/**
	 * Add a graph to Metrics
	 * @param title The title of the Graph
	 * @param value The value of the entry
	 */
	public void addMetricsGraph(String title, boolean value) {
		String stringEnabled = "No";
		if (value) {
			stringEnabled = "Yes";
		}
		addMetricsGraph(title, stringEnabled);
	}

	public void startMetrics() {
		metrics.start();
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
				getLogger().severe(String.format(getLanguageManager().getString("error_write_log"), e.getMessage()));
			}
		}
	}

	/**
	 * Get the version Checker.
	 * @return The version checker. May return null if the system is disabled in the config.yml
	 */
	public VersionChecker getVersionChecker() {
		return versionChecker;
	}

	/**
	 * Retrieve the Event manager.
	 * @return The Event manager.
	 */
	public EventManager getEventManager() {
		return eventManager;
	}

	/**
	 * Retrieve the {@link LanguageManager}
	 * @return The {@link LanguageManager}
	 */
	public LanguageManager getLanguageManager() {
		return languageManager;
	}

	public WorldGroupsManager getWorldGroupManager() {
		return worldGroupManager;
	}
	/**
	 * Check if the system has been initialized.
	 * @return True if the system has been initialized else false.
	 */
	public static boolean isInitialized() {
		return initialized;
	}
}
