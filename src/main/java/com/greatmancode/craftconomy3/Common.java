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
package com.greatmancode.craftconomy3;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.alta189.simplesave.exceptions.ConnectionException;
import com.alta189.simplesave.exceptions.TableRegistrationException;
import com.greatmancode.craftconomy3.account.Account;
import com.greatmancode.craftconomy3.account.AccountManager;
import com.greatmancode.craftconomy3.commands.CommandLoader;
import com.greatmancode.craftconomy3.currency.Currency;
import com.greatmancode.craftconomy3.currency.CurrencyManager;
import com.greatmancode.craftconomy3.database.tables.AccessTable;
import com.greatmancode.craftconomy3.database.tables.AccountTable;
import com.greatmancode.craftconomy3.database.tables.BalanceTable;
import com.greatmancode.craftconomy3.database.tables.ConfigTable;
import com.greatmancode.craftconomy3.database.tables.CurrencyTable;
import com.greatmancode.craftconomy3.database.tables.ExchangeTable;
import com.greatmancode.craftconomy3.database.tables.LogTable;
import com.greatmancode.craftconomy3.database.tables.PayDayTable;
import com.greatmancode.craftconomy3.database.tables.WorldGroupTable;
import com.greatmancode.craftconomy3.events.EventManager;
import com.greatmancode.craftconomy3.groups.WorldGroupsManager;
import com.greatmancode.craftconomy3.payday.PayDayManager;
import com.greatmancode.craftconomy3.utils.Metrics;
import com.greatmancode.craftconomy3.utils.Metrics.Graph;
import com.greatmancode.craftconomy3.utils.VersionChecker;
import com.greatmancode.tools.ServerType;
import com.greatmancode.tools.caller.bukkit.BukkitCaller;
import com.greatmancode.tools.caller.spout.SpoutCaller;
import com.greatmancode.tools.caller.unittest.UnitTestCaller;
import com.greatmancode.tools.configuration.Config;
import com.greatmancode.tools.configuration.ConfigurationManager;
import com.greatmancode.tools.database.DatabaseManager;
import com.greatmancode.tools.database.interfaces.DatabaseType;
import com.greatmancode.tools.database.throwable.InvalidDatabaseConstructor;
import com.greatmancode.tools.interfaces.Caller;
import com.greatmancode.tools.interfaces.Loader;
import com.greatmancode.tools.language.LanguageManager;

/**
 * The core of Craftconomy. Every requests pass through this class
 * @author greatman
 */
public class Common implements com.greatmancode.tools.interfaces.Common{
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

	private Config mainConfig = null;

	//Default values
	private DisplayFormat displayFormat = null;
	private double holdings = 0.0;
	private double bankPrice = 0.0;
	private int bankCurrencyId = 0;

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
	public void onEnable() {
		if (!initialized) {
			sendConsoleMessage(Level.INFO, "Starting up!");
			sendConsoleMessage(Level.INFO, "Loading the Configuration");
			config = new ConfigurationManager(serverCaller);
			mainConfig = config.loadFile(serverCaller.getDataFolder(), "config.yml", false, true);

			if (!getMainConfig().has("System.Database.Prefix")) {
				getMainConfig().setValue("System.Database.Prefix", "cc3_");
			}

			languageManager = new LanguageManager(serverCaller, serverCaller.getDataFolder(), "lang.yml");
			try {
				metrics = new Metrics("Craftconomy", this.getServerCaller().getPluginVersion());
			} catch (IOException e) {
				this.getLogger().log(Level.SEVERE, String.format(getLanguageManager().getString("metric_start_error"), e.getMessage()));
			}
			if (getMainConfig().getBoolean("System.CheckNewVersion")) {
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
			if (getMainConfig().getBoolean("System.Setup")) {

				//We got quick setup. Let's do it!!!!
				if (getMainConfig().getBoolean("System.QuickSetup.Enable")) {
					try {
						quickSetup();
					} catch (TableRegistrationException e) {
						e.printStackTrace();
						sendConsoleMessage(Level.SEVERE, String.format(getLanguageManager().getString("database_connect_error"), e.getMessage()));
						getServerCaller().disablePlugin();
						return;
					} catch (ConnectionException e) {
						e.printStackTrace();
						sendConsoleMessage(Level.SEVERE, String.format(getLanguageManager().getString("database_connect_error"), e.getMessage()));
						getServerCaller().disablePlugin();
						return;
					} catch (InvalidDatabaseConstructor invalidDatabaseConstructor) {
						invalidDatabaseConstructor.printStackTrace();
						sendConsoleMessage(Level.SEVERE, String.format(getLanguageManager().getString("database_connect_error"), invalidDatabaseConstructor.getMessage()));
						getServerCaller().disablePlugin();
					}
				} else {
					SetupWizard.setState(SetupWizard.DATABASE_SETUP);
					sendConsoleMessage(Level.WARNING, getLanguageManager().getString("loaded_setup_mode"));
				}
			} else {
				try {
					initialiseDatabase();
				} catch (Exception e) {
					e.printStackTrace();
					sendConsoleMessage(Level.SEVERE, String.format(getLanguageManager().getString("database_connect_error"), e.getMessage()));
					getServerCaller().disablePlugin();
					return;
				} catch (InvalidDatabaseConstructor invalidDatabaseConstructor) {
					invalidDatabaseConstructor.printStackTrace();
					sendConsoleMessage(Level.SEVERE, String.format(getLanguageManager().getString("database_connect_error"), invalidDatabaseConstructor.getMessage()));
					getServerCaller().disablePlugin();
				}
				initializeCurrency();
				sendConsoleMessage(Level.INFO, getLanguageManager().getString("loading_default_settings"));
				loadDefaultSettings();
				sendConsoleMessage(Level.INFO, getLanguageManager().getString("default_settings_loaded"));
				startUp();
				sendConsoleMessage(Level.INFO, getLanguageManager().getString("ready"));
			}

			//TODO: Let's make that less ugly shall we?
			getServerCaller().registerPermission("craftconomy.money.log.others");
			initialized = true;
		}
	}

	/**
	 * Disable the plugin.
	 */
	@Override
	public void onDisable() {
		if (getDatabaseManager() != null && getDatabaseManager().getDatabase() != null) {
			getLogger().info(getLanguageManager().getString("closing_db_link"));
			try {
				getDatabaseManager().getDatabase().close();
			} catch (ConnectionException e) {
				this.getLogger().severe(String.format(getLanguageManager().getString("unable_close_db_link"), e.getMessage()));
			}
		}
	}

	public Config getMainConfig() {
		return mainConfig;
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

		if (worldName != null && !worldName.equals(WorldGroupsManager.DEFAULT_GROUP_NAME)) {
			// We put the world name if the conf is true
			string.append(worldName).append(": ");
		}
		if (currency != null) {
			// We removes some cents if it's something like 20.20381 it would set it
			// to 20.20

			String[] theAmount = BigDecimal.valueOf(balance).toPlainString().split("\\.");
			DecimalFormatSymbols unusualSymbols = new DecimalFormatSymbols();
			unusualSymbols.setGroupingSeparator(',');
			DecimalFormat decimalFormat = new DecimalFormat("###,###", unusualSymbols);
			String name = currency.getName();
			if (balance > 2.0) {
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
			String amount = theAmount[0];
			try {
				amount = decimalFormat.format(Double.parseDouble(theAmount[0]));
			} catch (NumberFormatException e) {

			}

			// Do we seperate money and dollar or not?
			if (format == DisplayFormat.LONG) {
				String subName = currency.getMinor();
				if (Long.parseLong(coin) > 1) {
					subName = currency.getMinorPlural();
				}
				string.append(amount).append(" ").append(name).append(" ").append(coin).append(" ").append(subName);
			} else if (format == DisplayFormat.SMALL) {
				string.append(amount).append(".").append(coin).append(" ").append(name);
			} else if (format == DisplayFormat.SIGN) {
				string.append(currency.getSign()).append(amount).append(".").append(coin);
			} else if (format == DisplayFormat.MAJORONLY) {
				string.append(amount).append(" ").append(name);
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
		return format(worldName, currency, balance, displayFormat);
	}

	/**
	 * Initialize the database Manager
	 * @throws TableRegistrationException
	 * @throws ConnectionException
	 */
	public void initialiseDatabase() throws TableRegistrationException, ConnectionException, InvalidDatabaseConstructor {
		if (!databaseInitialized) {
			sendConsoleMessage(Level.INFO, getLanguageManager().getString("loading_database_manager"));
			DatabaseType databaseType = DatabaseType.valueOf(getMainConfig().getString("System.Database.Type").toUpperCase());
			if (DatabaseType.MySQL.equals(databaseType)) {
				dbManager = new DatabaseManager(databaseType,getMainConfig().getString("System.Database.Address"), getMainConfig().getInt("System.Database.Port"), getMainConfig().getString("System.Database.Username"), getMainConfig().getString("System.Database.Password"), getMainConfig().getString("System.Database.Db"), getMainConfig().getString("System.Database.Prefix"));
			} else {
				dbManager = new DatabaseManager(databaseType, getMainConfig().getString("System.Database.Prefix"), new File(serverCaller.getDataFolder(), "database.db"));
			}
			dbManager.registerTable(AccountTable.class);
			dbManager.registerTable(AccessTable.class);
			dbManager.registerTable(BalanceTable.class);
			dbManager.registerTable(CurrencyTable.class);
			dbManager.registerTable(ConfigTable.class);
			dbManager.registerTable(PayDayTable.class);
			dbManager.registerTable(ExchangeTable.class);
			dbManager.registerTable(WorldGroupTable.class);
			dbManager.registerTable(LogTable.class);
			dbManager.connect();
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

	public void initializeWorldGroup() {
		if (worldGroupManager == null) {
			worldGroupManager = new WorldGroupsManager();
			sendConsoleMessage(Level.INFO, getLanguageManager().getString("world_group_manager_loaded"));
		}
	}

	/**
	 * Initialize the Account & PayDay Manager
	 */
	public void startUp() {
		sendConsoleMessage(Level.INFO, getLanguageManager().getString("loading_account_manager"));
		accountManager = new AccountManager();
		//addMetricsGraph("Multiworld", getConfigurationManager().isMultiWorld());
		startMetrics();
		sendConsoleMessage(Level.INFO, getLanguageManager().getString("account_manager_loaded"));
		sendConsoleMessage(Level.INFO, getLanguageManager().getString("loading_payday_manager"));
		paydayManager = new PayDayManager();
		sendConsoleMessage(Level.INFO, getLanguageManager().getString("payday_manager_loaded"));
		eventManager = new EventManager();
		initializeWorldGroup();
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
	 * @param cause The cause of the transaction.
	 * @param amount The amount of money in this transaction.
	 * @param currency The currency associated with this transaction
	 * @param worldName The world name associated with this transaction
	 */
	public void writeLog(LogInfo info, Cause cause, String causeReason, Account account, double amount, Currency currency, String worldName) {
		if (getMainConfig().getBoolean("System.Logging.Enabled")) {
			LogTable log = new LogTable();
			log.username_id = account.getAccountID();
			log.amount = amount;
			log.type = info;
			log.cause = cause;
			log.causeReason = causeReason;
			log.currencyName = currency.getName();
			log.worldName = worldName;
			log.timestamp = new Timestamp(System.currentTimeMillis());
			getDatabaseManager().getDatabase().save(log);
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

	private void quickSetup() throws TableRegistrationException, ConnectionException, InvalidDatabaseConstructor {
		initialiseDatabase();
		Common.getInstance().initializeCurrency();
		Common.getInstance().getCurrencyManager().addCurrency(getMainConfig().getString("System.QuickSetup.Currency.Name"), getMainConfig().getString("System.QuickSetup.Currency.NamePlural"), getMainConfig().getString("System.QuickSetup.Currency.Minor"), getMainConfig().getString("System.QuickSetup.Currency.MinorPlural"), 0.0, getMainConfig().getString("System.QuickSetup.Currency.Sign"), true);
		int dbId = Common.getInstance().getCurrencyManager().getCurrency(getMainConfig().getString("System.QuickSetup.Currency.Name")).getDatabaseID();
		Common.getInstance().getCurrencyManager().setDefault(dbId);
		ConfigTable table = new ConfigTable();
		table.setName("bankcurrency");
		table.setValue(dbId + "");
		Common.getInstance().getDatabaseManager().getDatabase().save(table);
		table = new ConfigTable();
		table.setName("holdings");
		table.setValue(getMainConfig().getString("System.QuickSetup.StartBalance"));
		Common.getInstance().getDatabaseManager().getDatabase().save(table);
		table = new ConfigTable();
		table.setName("bankprice");
		table.setValue(getMainConfig().getString("System.QuickSetup.PriceBank"));
		Common.getInstance().getDatabaseManager().getDatabase().save(table);
		table = new ConfigTable();
		table.setName("longmode");
		table.setValue(DisplayFormat.valueOf(getMainConfig().getString("System.QuickSetup.DisplayMode").toUpperCase()).name());
		Common.getInstance().getDatabaseManager().getDatabase().save(table);
		table = new ConfigTable();
		table.setName("dbVersion");
		table.setValue("4");
		Common.getInstance().getDatabaseManager().getDatabase().save(table);
		initializeCurrency();
		loadDefaultSettings();
		Common.getInstance().startUp();
		Common.getInstance().getMainConfig().setValue("System.Setup", false);
		sendConsoleMessage(Level.INFO, "Quick-Config done!");
	}

	private void loadDefaultSettings() {
		displayFormat = DisplayFormat.valueOf(getDatabaseManager().getDatabase().select(ConfigTable.class).where().contains(ConfigTable.NAME_FIELD, "longmode").execute().findOne().getValue().toUpperCase());
		holdings = Double.parseDouble(getDatabaseManager().getDatabase().select(ConfigTable.class).where().equal(ConfigTable.NAME_FIELD, "holdings").execute().findOne().getValue());
		bankPrice = Double.parseDouble(getDatabaseManager().getDatabase().select(ConfigTable.class).where().equal(ConfigTable.NAME_FIELD, "bankprice").execute().findOne().getValue());
		ConfigTable currencyId = getDatabaseManager().getDatabase().select(ConfigTable.class).where().equal(ConfigTable.NAME_FIELD, "bankcurrency").execute().findOne();
		if (currencyId != null) {
			bankCurrencyId = Integer.parseInt(currencyId.getValue());
		}

		// Test if the currency is good. Else we revert it to the default value.
		if (getCurrencyManager().getCurrency(bankCurrencyId) == null) {
			bankCurrencyId = getCurrencyManager().getDefaultCurrency().getDatabaseID();
		}
	}

	public DisplayFormat getDisplayFormat() {
		return displayFormat;
	}

	public void setDisplayFormat(DisplayFormat format) {
		ConfigTable table = getDatabaseManager().getDatabase().select(ConfigTable.class).where().equal(ConfigTable.NAME_FIELD, "longmode").execute().findOne();
		table.setValue(format.name());
		getDatabaseManager().getDatabase().save(table);
		displayFormat = format;
	}
	public double getDefaultHoldings() {
		return holdings;
	}

	public void setDefaultHoldings(double value) {
		ConfigTable table = getDatabaseManager().getDatabase().select(ConfigTable.class).where().equal(ConfigTable.NAME_FIELD, "holdings").execute().findOne();
		table.setValue(String.valueOf(value));
		getDatabaseManager().getDatabase().save(table);
		holdings = value;
	}

	public double getBankPrice() {
		return bankPrice;
	}

	public void setBankPrice(double value) {
		ConfigTable table = getDatabaseManager().getDatabase().select(ConfigTable.class).where().equal(ConfigTable.NAME_FIELD, "bankprice").execute().findOne();
		table.setValue(String.valueOf(value));
		getDatabaseManager().getDatabase().save(table);
		bankPrice = value;
	}

	public int getBankCurrencyId() {
		return bankCurrencyId;
	}


}
