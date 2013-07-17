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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.alta189.simplesave.exceptions.ConnectionException;
import com.alta189.simplesave.exceptions.TableRegistrationException;
import com.greatmancode.craftconomy3.account.Account;
import com.greatmancode.craftconomy3.account.AccountManager;
import com.greatmancode.craftconomy3.commands.bank.BankBalanceCommand;
import com.greatmancode.craftconomy3.commands.bank.BankCreateCommand;
import com.greatmancode.craftconomy3.commands.bank.BankDeleteCommand;
import com.greatmancode.craftconomy3.commands.bank.BankDepositCommand;
import com.greatmancode.craftconomy3.commands.bank.BankGiveCommand;
import com.greatmancode.craftconomy3.commands.bank.BankListCommand;
import com.greatmancode.craftconomy3.commands.bank.BankPermCommand;
import com.greatmancode.craftconomy3.commands.bank.BankSetCommand;
import com.greatmancode.craftconomy3.commands.bank.BankTakeCommand;
import com.greatmancode.craftconomy3.commands.bank.BankWithdrawCommand;
import com.greatmancode.craftconomy3.commands.config.ConfigBankPriceCommand;
import com.greatmancode.craftconomy3.commands.config.ConfigFormatCommand;
import com.greatmancode.craftconomy3.commands.config.ConfigHoldingsCommand;
import com.greatmancode.craftconomy3.commands.currency.CurrencyAddCommand;
import com.greatmancode.craftconomy3.commands.currency.CurrencyDefaultCommand;
import com.greatmancode.craftconomy3.commands.currency.CurrencyDeleteCommand;
import com.greatmancode.craftconomy3.commands.currency.CurrencyEditCommand;
import com.greatmancode.craftconomy3.commands.currency.CurrencyExchangeCommand;
import com.greatmancode.craftconomy3.commands.currency.CurrencyInfoCommand;
import com.greatmancode.craftconomy3.commands.currency.CurrencyListCommand;
import com.greatmancode.craftconomy3.commands.currency.CurrencyRatesCommand;
import com.greatmancode.craftconomy3.commands.group.GroupAddWorldCommand;
import com.greatmancode.craftconomy3.commands.group.GroupCreateCommand;
import com.greatmancode.craftconomy3.commands.group.GroupDelWorldCommand;
import com.greatmancode.craftconomy3.commands.money.AllCommand;
import com.greatmancode.craftconomy3.commands.money.BalanceCommand;
import com.greatmancode.craftconomy3.commands.money.CreateCommand;
import com.greatmancode.craftconomy3.commands.money.DeleteCommand;
import com.greatmancode.craftconomy3.commands.money.ExchangeCommand;
import com.greatmancode.craftconomy3.commands.money.GiveCommand;
import com.greatmancode.craftconomy3.commands.money.InfiniteCommand;
import com.greatmancode.craftconomy3.commands.money.LogCommand;
import com.greatmancode.craftconomy3.commands.money.MainCommand;
import com.greatmancode.craftconomy3.commands.money.PayCommand;
import com.greatmancode.craftconomy3.commands.money.SetCommand;
import com.greatmancode.craftconomy3.commands.money.TakeCommand;
import com.greatmancode.craftconomy3.commands.money.TopCommand;
import com.greatmancode.craftconomy3.commands.payday.PayDayCreateCommand;
import com.greatmancode.craftconomy3.commands.payday.PayDayDeleteCommand;
import com.greatmancode.craftconomy3.commands.payday.PayDayInfoCommand;
import com.greatmancode.craftconomy3.commands.payday.PayDayListCommand;
import com.greatmancode.craftconomy3.commands.payday.PayDayModifyCommand;
import com.greatmancode.craftconomy3.commands.setup.NewSetupBasicCommand;
import com.greatmancode.craftconomy3.commands.setup.NewSetupConvertCommand;
import com.greatmancode.craftconomy3.commands.setup.NewSetupCurrencyCommand;
import com.greatmancode.craftconomy3.commands.setup.NewSetupDatabaseCommand;
import com.greatmancode.craftconomy3.commands.setup.NewSetupMainCommand;
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
import com.greatmancode.tools.caller.unittest.UnitTestServerCaller;
import com.greatmancode.tools.commands.CommandHandler;
import com.greatmancode.tools.commands.SubCommand;
import com.greatmancode.tools.configuration.Config;
import com.greatmancode.tools.configuration.ConfigurationManager;
import com.greatmancode.tools.database.DatabaseManager;
import com.greatmancode.tools.database.interfaces.DatabaseType;
import com.greatmancode.tools.database.throwable.InvalidDatabaseConstructor;
import com.greatmancode.tools.interfaces.caller.ServerCaller;
import com.greatmancode.tools.language.LanguageManager;
import com.greatmancode.tools.utils.Metrics;
import com.greatmancode.tools.utils.VersionChecker;

/**
 * The core of Craftconomy. Every requests pass through this class
 * @author greatman
 */
public class Common implements com.greatmancode.tools.interfaces.Common {
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
	private CommandHandler commandManager;
	private ServerCaller serverCaller;
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
	 * Initialize the Common core.
	 */
	public void onEnable(ServerCaller serverCaller, Logger log) {
		this.serverCaller = serverCaller;
		instance = this;
		this.log = log;
		if (!initialized) {
			sendConsoleMessage(Level.INFO, "Starting up!");
			sendConsoleMessage(Level.INFO, "Loading the Configuration");
			config = new ConfigurationManager(serverCaller);
			mainConfig = config.loadFile(serverCaller.getDataFolder(), "config.yml");
			if (!mainConfig.has("System.Setup")) {
				initializeConfig();
			}
			if (!getMainConfig().has("System.Database.Prefix")) {
				getMainConfig().setValue("System.Database.Prefix", "cc3_");
			}

			languageManager = new LanguageManager(serverCaller, serverCaller.getDataFolder(), "lang.yml");
			loadLanguage();
			serverCaller.setCommandPrefix(languageManager.getString("command_prefix"));
			if (!(getServerCaller() instanceof UnitTestServerCaller)) {
				try {
					metrics = new Metrics("Craftconomy", this.getServerCaller().getPluginVersion(), serverCaller);
				} catch (IOException e) {
					this.getLogger().log(Level.SEVERE, String.format(getLanguageManager().getString("metric_start_error"), e.getMessage()));
				}
			}

			if (getMainConfig().getBoolean("System.CheckNewVersion")) {
				sendConsoleMessage(Level.INFO, getLanguageManager().getString("checking_new_version"));
				versionChecker = new VersionChecker(Common.getInstance().getServerCaller().getPluginVersion());
				if (versionChecker.isOld()) {
					sendConsoleMessage(Level.WARNING, getLanguageManager().parse("running_old_version", versionChecker.getNewVersion()));
				}
			}
			sendConsoleMessage(Level.INFO, "Loading listeners.");
			serverCaller.getLoader().getEventManager().registerEvents(this, new EventManager());
			sendConsoleMessage(Level.INFO, "Loading commands");
			Common.getInstance().getServerCaller().registerPermission("craftconomy.*");
			commandManager = new CommandHandler(serverCaller);
			registerCommands();
			//commandManager = new CommandLoader();
			//commandManager.initialize();
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
				commandManager.setLevel(1);
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
				updateDatabase();
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

	public void reloadPlugin() {
		sendConsoleMessage(Level.INFO, "Starting up!");
		sendConsoleMessage(Level.INFO, "Loading the Configuration");
		config = new ConfigurationManager(serverCaller);
		mainConfig = config.loadFile(serverCaller.getDataFolder(), "config.yml");
		if (!mainConfig.has("System.Setup")) {
			initializeConfig();
		}
		if (!getMainConfig().has("System.Database.Prefix")) {
			getMainConfig().setValue("System.Database.Prefix", "cc3_");
		}

		languageManager = new LanguageManager(serverCaller, serverCaller.getDataFolder(), "lang.yml");
		loadLanguage();
		serverCaller.setCommandPrefix(languageManager.getString("command_prefix"));
		commandManager.setLevel(1);
		try {
			initialiseDatabase();
			updateDatabase();
			initializeCurrency();
			sendConsoleMessage(Level.INFO, getLanguageManager().getString("loading_default_settings"));
			loadDefaultSettings();
			sendConsoleMessage(Level.INFO, getLanguageManager().getString("default_settings_loaded"));
			startUp();
			sendConsoleMessage(Level.INFO, getLanguageManager().getString("ready"));
		} catch (TableRegistrationException e) {
			e.printStackTrace();
		} catch (ConnectionException e) {
			e.printStackTrace();
		} catch (InvalidDatabaseConstructor invalidDatabaseConstructor) {
			invalidDatabaseConstructor.printStackTrace();
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
	public CommandHandler getCommandManager() {
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
	public ServerCaller getServerCaller() {
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
			if (DatabaseType.MYSQL.equals(databaseType)) {
				dbManager = new DatabaseManager(databaseType, getMainConfig().getString("System.Database.Address"), getMainConfig().getInt("System.Database.Port"), getMainConfig().getString("System.Database.Username"), getMainConfig().getString("System.Database.Password"), getMainConfig().getString("System.Database.Db"), getMainConfig().getString("System.Database.Prefix"), serverCaller);
			} else {
				dbManager = new DatabaseManager(databaseType, getMainConfig().getString("System.Database.Prefix"), new File(serverCaller.getDataFolder(), "database.db"), serverCaller);
			}
			addMetricsGraph("Database Engine", databaseType.name());

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
		if (metrics != null) {
			Metrics.Graph graph = metrics.createGraph(title);
			graph.addPlotter(new Metrics.Plotter(value) {
				@Override
				public int getValue() {
					return 1;
				}
			});
		}
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
		if (metrics != null) {
			metrics.start();
		}
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

	public void loadDefaultSettings() {
		displayFormat = DisplayFormat.valueOf(getDatabaseManager().getDatabase().select(ConfigTable.class).where().contains(ConfigTable.NAME_FIELD, "longmode").execute().findOne().getValue().toUpperCase());
		addMetricsGraph("Display Format", displayFormat.name());
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
		commandManager.setLevel(1);
		sendConsoleMessage(Level.INFO, "Quick-Config done!");
	}

	private void registerCommands() {
		commandManager.setWrongLevelMsg(languageManager.getString("command_disabled_setup_mode"));
		SubCommand money = new SubCommand("money", commandManager, null, 1);
		money.addCommand("", new MainCommand());
		money.addCommand("all", new AllCommand());
		money.addCommand("pay", new PayCommand());
		money.addCommand("give", new GiveCommand());
		money.addCommand("take", new TakeCommand());
		money.addCommand("set", new SetCommand());
		money.addCommand("delete", new DeleteCommand());
		money.addCommand("create", new CreateCommand());
		money.addCommand("balance", new BalanceCommand());
		money.addCommand("top", new TopCommand());
		money.addCommand("exchange", new ExchangeCommand());
		money.addCommand("infinite", new InfiniteCommand());
		money.addCommand("log", new LogCommand());
		commandManager.registerMainCommand("money", money);

		SubCommand bank = new SubCommand("bank", commandManager, null, 1);
		bank.addCommand("create", new BankCreateCommand());
		bank.addCommand("balance", new BankBalanceCommand());
		bank.addCommand("deposit", new BankDepositCommand());
		bank.addCommand("withdraw", new BankWithdrawCommand());
		bank.addCommand("set", new BankSetCommand());
		bank.addCommand("give", new BankGiveCommand());
		bank.addCommand("take", new BankTakeCommand());
		bank.addCommand("perm", new BankPermCommand());
		bank.addCommand("list", new BankListCommand());
		bank.addCommand("delete", new BankDeleteCommand());
		commandManager.registerMainCommand("bank", bank);

		SubCommand ccsetup = new SubCommand("ccsetup", commandManager, null, 0);
		ccsetup.addCommand("", new NewSetupMainCommand());
		ccsetup.addCommand("database", new NewSetupDatabaseCommand());
		ccsetup.addCommand("currency", new NewSetupCurrencyCommand());
		ccsetup.addCommand("basic", new NewSetupBasicCommand());
		ccsetup.addCommand("convert", new NewSetupConvertCommand());
		commandManager.registerMainCommand("ccsetup", ccsetup);

		SubCommand currency = new SubCommand("currency", commandManager, null, 1);
		currency.addCommand("add", new CurrencyAddCommand());
		currency.addCommand("delete", new CurrencyDeleteCommand());
		currency.addCommand("edit", new CurrencyEditCommand());
		currency.addCommand("info", new CurrencyInfoCommand());
		currency.addCommand("default", new CurrencyDefaultCommand());
		currency.addCommand("exchange", new CurrencyExchangeCommand());
		currency.addCommand("rates", new CurrencyRatesCommand());
		currency.addCommand("list", new CurrencyListCommand());
		commandManager.registerMainCommand("currency", currency);

		SubCommand configCommand = new SubCommand("craftconomy", commandManager, null, 1);
		configCommand.addCommand("holdings", new ConfigHoldingsCommand());
		configCommand.addCommand("bankprice", new ConfigBankPriceCommand());
		configCommand.addCommand("format", new ConfigFormatCommand());
		commandManager.registerMainCommand("craftconomy", configCommand);

		SubCommand payday = new SubCommand("payday", commandManager, null, 1);
		payday.addCommand("create", new PayDayCreateCommand());
		payday.addCommand("delete", new PayDayDeleteCommand());
		payday.addCommand("modify", new PayDayModifyCommand());
		payday.addCommand("list", new PayDayListCommand());
		payday.addCommand("info", new PayDayInfoCommand());
		commandManager.registerMainCommand("payday", payday);

		SubCommand ccgroup = new SubCommand("ccgroup", commandManager, null, 1);
		ccgroup.addCommand("create", new GroupCreateCommand());
		ccgroup.addCommand("addworld", new GroupAddWorldCommand());
		ccgroup.addCommand("delworld", new GroupDelWorldCommand());
		commandManager.registerMainCommand("ccgroup", ccgroup);
	}

	private void loadLanguage() {
		languageManager.addLanguageEntry("metric_start_error", "Unable to load Metrics! The error is: %s");
		languageManager.addLanguageEntry("checking_new_version", "Checking if there's a new version.");
		languageManager.addLanguageEntry("running_old_version", "Running a old version of Craftconomy! New version is: %s");
		languageManager.addLanguageEntry("database_connect_error", "A error occured while trying to connect to the database. Message received: %s");
		languageManager.addLanguageEntry("loading_default_settings", "Loading default settings.");
		languageManager.addLanguageEntry("default_settings_loaded", "Default settings loaded!");
		languageManager.addLanguageEntry("loaded_setup_mode", "Loading Craftconomy in setup mode. Please type /ccsetup to start the setup.");
		languageManager.addLanguageEntry("ready", "Ready!");
		languageManager.addLanguageEntry("closing_db_link", "Closing the connection to the database.");
		languageManager.addLanguageEntry("unable_close_db_link", "Unable to close the database connection! Reason is: %s");
		languageManager.addLanguageEntry("loading_database_manager", "Loading the Database Manager");
		languageManager.addLanguageEntry("database_manager_loaded", "Database Manager Loaded!");
		languageManager.addLanguageEntry("loading_curency_manager", "Loading the Currency Manager");
		languageManager.addLanguageEntry("currency_manager_loaded", "Currency Manager Loaded!");
		languageManager.addLanguageEntry("loading_account_manager", "Loading the Account Manager");
		languageManager.addLanguageEntry("account_manager_loaded", "Account Manager Loaded!");
		languageManager.addLanguageEntry("loading_payday_manager", "Loading the PayDay manager.");
		languageManager.addLanguageEntry("payday_manager_loaded", "PayDay Manager loaded!");
		languageManager.addLanguageEntry("error_write_log", "Error while writing the transaction logger! Error is: %s");
		languageManager.addLanguageEntry("invalid_library", "Invalid library URL for: %s. Full error is: %s");
		languageManager.addLanguageEntry("command_disabled_setup_mode", "{{DARK_RED}}This command is disabled while Craftconomy is under setup mode! Type /ccsetup to configure the plugin.");
		languageManager.addLanguageEntry("user_only_command", "{{DARK_RED}}This command should only be used by players!");
		languageManager.addLanguageEntry("no_permission", "{{DARK_RED}}You don't have permissions!");
		languageManager.addLanguageEntry("command_usage", "Usage: %s");
		languageManager.addLanguageEntry("subcommand_not_exist", "{{DARK_RED}}This subcommand doesn't exist!");
		languageManager.addLanguageEntry("bank_statement", "{{DARK_GREEN}}Bank Statement:");
		languageManager.addLanguageEntry("cant_check_bank_statement", "{{DARK_RED}}You can't check this bank account statement");
		languageManager.addLanguageEntry("account_not_exist", "{{DARK_RED}}This account doesn't exist!");
		languageManager.addLanguageEntry("bank_account_created", "{{DARK_GREEN}}The account has been created!");
		languageManager.addLanguageEntry("bank_account_not_enough_money_create", "{{DARK_RED}}You don't have enough money to create a bank account! You need {{WHITE}}%s");
		languageManager.addLanguageEntry("account_already_exists", "{{DARK_RED}}This account already exists!");
		languageManager.addLanguageEntry("currency_not_exist", "{{DARK_RED}}That currency doesn't exist!");
		languageManager.addLanguageEntry("not_enough_money", "{{DARK_RED}}Not enough money!");
		languageManager.addLanguageEntry("invalid_amount", "{{DARK_RED}}Invalid amount!");
		languageManager.addLanguageEntry("bank_cant_deposit", "{{DARK_RED}}You can't deposit in this account!");
		languageManager.addLanguageEntry("deposited", "{{DARK_GREEN}}Deposited {{WHITE}}%s {{DARK_GREEN}}in the {{WHITE}}%s {{DARK_GREEN}}bank Account.");
		languageManager.addLanguageEntry("bank_help_title", "{{DARK_GREEN}} ======== Bank Commands ========");
		languageManager.addLanguageEntry("bank_create_cmd_help", "/bank create <Account Name> - Create a bank account");
		languageManager.addLanguageEntry("bank_balance_cmd_help", "/bank balance <Account Name> - Check the balance of a account.");
		languageManager.addLanguageEntry("bank_deposit_cmd_help", "/bank deposit <Account Name> <Amount> [Currency] - Deposit money in a bank account.");
		languageManager.addLanguageEntry("bank_give_cmd_help", "/bank give <Account Name> <Amount> [Currency] [World] - Give money in a bank account.");
		languageManager.addLanguageEntry("bank_help_cmd_help", "/bank - Shows bank help");
		languageManager.addLanguageEntry("bank_perm_cmd_help", "/bank perm <Account Name> <deposit/withdraw/acl/show> <Player Name> <true/false> - Modify the permission of a player");
		languageManager.addLanguageEntry("bank_set_cmd_help", "/bank set <Account Name> <Amount> [Currency] [World]- Set a balance in a account.");
		languageManager.addLanguageEntry("bank_take_cmd_help", "/bank take <Account Name> <Amount> [Currency] [World]- Take money from a account.");
		languageManager.addLanguageEntry("bank_withdraw_cmd_help", "/bank withdraw <Account Name> <Amount> [Currency] - Withdraw money in a account.");
		languageManager.addLanguageEntry("world_not_exist", "{{DARK_RED}}This world doesn't exist!");
		languageManager.addLanguageEntry("bank_give_success", "{{DARK_GREEN}}Deposited {{WHITE}}%s{{DARK_GREEN}} from the {{WHITE}}%s{{DARK_GREEN}} bank Account.");
		languageManager.addLanguageEntry("invalid_flag", "{{DARK_RED}}Invalid flag!");
		languageManager.addLanguageEntry("bank_flag_set", "{{DARK_GREEN}}The flag {{WHITE}}%s {{DARK_GREEN}}for the player {{WHITE}}%s {{DARK_GREEN}}has been set to {{WHITE}}%s");
		languageManager.addLanguageEntry("cant_modify_acl", "{{DARK_RED}}You can't modify the ACL of this account!");
		languageManager.addLanguageEntry("bank_set_success", "{{DARK_GREEN}}Set {{WHITE}}%s {{DARK_GREEN}}in the {{WHITE}}%s {{DARK_GREEN}}bank Account.");
		languageManager.addLanguageEntry("bank_not_enough_money", "{{DARK_RED}}The bank account doesn't have enough money!");
		languageManager.addLanguageEntry("bank_take_success", "{{DARK_GREEN}}Taken {{WHITE}}%s {{DARK_GREEN}}from the {{WHITE}}%s {{DARK_GREEN}}bank Account.");
		languageManager.addLanguageEntry("cant_withdraw_bank", "{{DARK_RED}}You can't withdraw in this account!");
		languageManager.addLanguageEntry("bank_price_modified", "{{DARK_GREEN}}Bank price modified!");
		languageManager.addLanguageEntry("config_bankprice_cmd_help", "/craftconomy bankprice <Amount> - Change the price to create a bank account.");
		languageManager.addLanguageEntry("config_format_cmd_help", "/craftconomy format <long/small/sign/majoronly> - Set the display format.");
		languageManager.addLanguageEntry("config_cmd_help", "/craftconomy - shows config command help");
		languageManager.addLanguageEntry("config_holdings_cmd_help", "/craftconomy holdings <Amount> - Set the default amount of money of a user account.");
		languageManager.addLanguageEntry("config_help_title", "{{DARK_GREEN}} ======== Craftconomy Commands ========");
		languageManager.addLanguageEntry("format_modified", "{{DARK_GREEN}}long balance format changed!");
		languageManager.addLanguageEntry("invalid_mode", "{{DARK_RED}}Invalid mode!");
		languageManager.addLanguageEntry("default_holding_modified", "{{DARK_GREEN}}Default holdings modified!");
		languageManager.addLanguageEntry("currency_added", "{{DARK_GREEN}}Currency added!");
		languageManager.addLanguageEntry("currency_already_exists", "{{DARK_RED}}This currency already exists!");
		languageManager.addLanguageEntry("currency_add_cmd_help", "/currency add <Name> <Name Plural> <Minor> <Minor Plural> <Sign> - Add a currency.");
		languageManager.addLanguageEntry("currency_default_cmd_help", "/currency default <Name> - Set a currency as the default one.");
		languageManager.addLanguageEntry("currency_delete_cmd_help", "/currency delete <Name> - Delete a currency {{DARK_RED}}It also deletes all balance with this currency.");
		languageManager.addLanguageEntry("currency_edit_cmd_help", "/currency edit <name/nameplural/minor/minorplural/sign> <Currency Name> <new Value> - Modify a currency.");
		languageManager.addLanguageEntry("currency_help_cmd_help", "/currency - shows currency command help");
		languageManager.addLanguageEntry("currency_info_cmd_help", "/currency info <Name> - Display the information about a currency.");
		languageManager.addLanguageEntry("default_currency_set", "%s {{DARK_GREEN}}has been set as the default currency!");
		languageManager.addLanguageEntry("currency_deleted", "{{DARK_GREEN}}Currency deleted!");
		languageManager.addLanguageEntry("currency_modified", "{{DARK_GREEN}}Currency modified!");
		languageManager.addLanguageEntry("invalid_type", "{{DARK_RED}}Invalid type!");
		languageManager.addLanguageEntry("currency_empty_value", "{{DARK_RED}}Can't change a currency value to empty (Aka \\)");
		languageManager.addLanguageEntry("currency_help_title", "{{DARK_GREEN}} ======== Currency Commands ========");
		languageManager.addLanguageEntry("currency_info_name", "{{DARK_GREEN}}Name: {{WHITE}}%s");
		languageManager.addLanguageEntry("currency_info_name_plural", "{{DARK_GREEN}}Name Plural: {{WHITE}}%s");
		languageManager.addLanguageEntry("currency_info_minor", "{{DARK_GREEN}}Minor: {{WHITE}}%s");
		languageManager.addLanguageEntry("currency_info_minor_plural", "{{DARK_GREEN}}Minor plural: {{WHITE}}%s");
		languageManager.addLanguageEntry("money_all_title", "{{DARK_GREEN}}Balance: ");
		languageManager.addLanguageEntry("money_all_cmd_help", "/money all - Display your balance on all the worlds");
		languageManager.addLanguageEntry("money_balance_cmd_help", "/money balance <Player Name> - Display the balance of a player");
		languageManager.addLanguageEntry("money_create_cmd_help", "/money create <Name> - Create a account");
		languageManager.addLanguageEntry("money_delete_cmd_help", "/money delete <Name> - Delete a account");
		languageManager.addLanguageEntry("money_give_cmd_help", "/money give <Player Name> <Amount> [Currency] [World] - Give money to someone");
		languageManager.addLanguageEntry("money_main_cmd_help", "/money  - List your balance");
		languageManager.addLanguageEntry("money_help_cmd_help", "/money help - Shows money help");
		languageManager.addLanguageEntry("money_pay_cmd_help", "/money pay <Player Name> <Amount> [Currency] - Send money to someone");
		languageManager.addLanguageEntry("money_set_cmd_help", "/money set <Player Name> <Amount> [Currency] [World] - set a balance of someone");
		languageManager.addLanguageEntry("money_take_cmd_help", "/money take <Player Name> <Amount> [Currency] [World] - take money from someone");
		languageManager.addLanguageEntry("money_top_cmd_help", "/money top <Currency> [Page] [World] - Shows the top list");
		languageManager.addLanguageEntry("money_create_success", "{{DARK_GREEN}} Account created!");
		languageManager.addLanguageEntry("money_delete_success", "{{DARK_GREEN}}The account {{WHITE}}%s {{DARK_GREEN}}has been deleted!");
		languageManager.addLanguageEntry("money_give_received", "{{DARK_GREEN}}Received {{WHITE}}%s {{DARK_GREEN}}from {{WHITE}}%s");
		languageManager.addLanguageEntry("money_give_send", "{{DARK_GREEN}}Gave {{WHITE}}%s {{DARK_GREEN}}to {{WHITE}}%s");
		languageManager.addLanguageEntry("money_help_title", "{{DARK_GREEN}} ======== Money Commands ========");
		languageManager.addLanguageEntry("money_pay_sent", "{{DARK_GREEN}}Sent {{WHITE}}%s {{DARK_GREEN}}to {{WHITE}}%s");
		languageManager.addLanguageEntry("money_pay_received", "{{DARK_GREEN}}Received {{WHITE}}%s {{DARK_GREEN}}from {{WHITE}}%s");
		languageManager.addLanguageEntry("money_set", "{{DARK_GREEN}}Set {{WHITE}}%s {{DARK_GREEN}}balance to {{WHITE}}%s");
		languageManager.addLanguageEntry("money_set_other", "{{DARK_GREEN}}Your money has been set to {{WHITE}}%s {{DARK_GREEN}}by {{WHITE}}%s");
		languageManager.addLanguageEntry("money_take", "{{DARK_GREEN}}Took {{WHITE}}%s {{DARK_GREEN}}from {{WHITE}}%s");
		languageManager.addLanguageEntry("money_take_other", "{{WHITE}}%s {{DARK_GREEN}}has been removed from your account by {{WHITE}}%s");
		languageManager.addLanguageEntry("player_not_exist", "{{DARK_RED}}The player doesn't exist!");
		languageManager.addLanguageEntry("invalid_page", "{{DARK_RED}}Invalid page!");
		languageManager.addLanguageEntry("money_top_header", "{{DARK_GREEN}} Money Top | Page {{WHITE}}%s {{DARK_GREEN}} | World {{WHITE}}%s");
		languageManager.addLanguageEntry("payday_create_cmd_help", "/payday create <Name> <Interval> <wage/tax> <Amount> [Account] [Currency Name] [World Name] - Create a new payday");
		languageManager.addLanguageEntry("payday_delete_cmd_help", "/payday delete <Name> - Delete a PayDay.");
		languageManager.addLanguageEntry("payday_help_cmd_help", "/payday - shows payday command help");
		languageManager.addLanguageEntry("payday_info_cmd_help", "/payday info <Payday Name> - Show information about a payday.");
		languageManager.addLanguageEntry("payday_list_cmd_help", "/payday list - List all payday");
		languageManager.addLanguageEntry("payday_modify_cmd_help", "/payday modify <Name> <Name/status/disabled/interval/amount/account/currency/World> <Value> - Modify a payday setting.");
		languageManager.addLanguageEntry("payday_help_title", "{{DARK_GREEN}} ======== PayDay Commands ========");
		languageManager.addLanguageEntry("payday_already_exist", "{{DARK_RED}}There's already a payday named like that!");
		languageManager.addLanguageEntry("invalid_interval", "{{DARK_RED}}Invalid interval!");
		languageManager.addLanguageEntry("payday_invalid_mode", "{{DARK_RED}}Invalid mode. only wage or tax is supported!");
		languageManager.addLanguageEntry("payday_create_success", "{{DARK_GREEN}}Payday added! Add the permission node {{WHITE}}%s {{DARK_GREEN}}to the players you want to add this payday!");
		languageManager.addLanguageEntry("payday_not_found", "{{DARK_RED}}PayDay not found!");
		languageManager.addLanguageEntry("error_occured", "{{DARK_GREEN}}A error occured. Check the console for any errors!");
		languageManager.addLanguageEntry("payday_removed", "{{DARK_GREEN}}Payday removed!");
		languageManager.addLanguageEntry("payday_info_title", "{{DARK_GREEN}} ======== {{WHITE}}%s information {{DARK_GREEN}}========");
		languageManager.addLanguageEntry("payday_info_type_wage", "{{DARK_GREEN}}Type: {{WHITE}}Wage");
		languageManager.addLanguageEntry("payday_info_type_tax", "{{DARK_GREEN}}Type: {{WHITE}}Tax");
		languageManager.addLanguageEntry("payday_info_account", "{{DARK_GREEN}}Account: {{WHITE}}%s");
		languageManager.addLanguageEntry("payday_info_interval", "{{DARK_GREEN}}Interval: {{WHITE}}%s");
		languageManager.addLanguageEntry("payday_info_amount", "{{DARK_GREEN}}Amount: {{WHITE}}%s");
		languageManager.addLanguageEntry("payday_list_title", "{{DARK_GREEN}} ========= {{WHITE}}Payday list {{DARK_GREEN}}=========");
		languageManager.addLanguageEntry("invalid_edit_mode", "{{DARK_RED}}Invalid Edit mode.");
		languageManager.addLanguageEntry("world_changed", "{{DARK_GREEN}}World changed!");
		languageManager.addLanguageEntry("currency_changed", "{{DARK_GREEN}}Currency changed!");
		languageManager.addLanguageEntry("account_changed", "{{DARK_GREEN}}Account changed!");
		languageManager.addLanguageEntry("amount_changed", "{{DARK_GREEN}}Amount changed!");
		languageManager.addLanguageEntry("interval_changed", "{{DARK_GREEN}}Interval changed!");
		languageManager.addLanguageEntry("disabled_changed", "{{DARK_GREEN}}Disabled changed!");
		languageManager.addLanguageEntry("status_changed", "{{DARK_GREEN}}Status changed!");
		languageManager.addLanguageEntry("name_changed", "{{DARK_GREEN}}Name changed!");
		languageManager.addLanguageEntry("invalid_interval", "{{DARK_RED}}Invalid interval! I need a amount of seconds! (Example: 60 for 60 seconds)");
		languageManager.addLanguageEntry("invalid_disabled", "{{DARK_RED}}Invalid disabled mode! Valid values are: true/false");
		languageManager.addLanguageEntry("invalid_status", "{{DARK_RED}}Invalid status! Valid values are: wage/tax");
		languageManager.addLanguageEntry("payday_with_name_already_exist", "{{DARK_RED}}There's already a payday with this name!");
		languageManager.addLanguageEntry("money_exchange_cmd_help", "/money exchange <Current Currency> <New Currency> <Amount> -Exchange a currency");
		languageManager.addLanguageEntry("no_exchange_rate", "{{DARK_RED}}No exchange rate from {{WHITE}}%s {{DARK_RED}}to {{WHITE}}%s!");
		languageManager.addLanguageEntry("exchange_done", "{{WHITE}}%s %s {{DARK_GREEN}}has been converted to {{WHITE}}%s %s");
		languageManager.addLanguageEntry("currency_exchange_cmd_help", "/currency exchange <Currency from> <Currency to> <amount> - set a currency exchange rate");
		languageManager.addLanguageEntry("currency_exchange_set", "Currency exchange set!");
		languageManager.addLanguageEntry("world_group_manager_loaded", "World Group Manager loaded!");
		languageManager.addLanguageEntry("group_create_cmd_help", "/ccgroup create <Name> - Create a world group.");
		languageManager.addLanguageEntry("group_addworld_cmd_help", "/ccgroup addworld <Group Name> <World Name> - Add a world to a world group.");
		languageManager.addLanguageEntry("group_already_exist", "{{DARK_RED}}This world group already exist!");
		languageManager.addLanguageEntry("group_created", "World group created!");
		languageManager.addLanguageEntry("group_not_exist", "{{DARK_RED}}This world group doesn't exist!");
		languageManager.addLanguageEntry("group_world_added", "World added to the group!");
		languageManager.addLanguageEntry("world_already_in_group", "{{DARK_RED}}This world is already in a group! Please remove it with {{WHITE}}/ccgroup delworld %s");
		languageManager.addLanguageEntry("group_delworld_cmd_help", "/ccgroup delworld <World Name> - Remove a world from his group. Reverting it to the default group.");
		languageManager.addLanguageEntry("world_not_in_group", "{{DARK_RED}}This world isin't in a group!");
		languageManager.addLanguageEntry("world_removed_from_group", "The world has been removed from the group! It has been set to the default group.");
		languageManager.addLanguageEntry("loading_currency_manager", "Loading the Currency manager.");
		languageManager.addLanguageEntry("command_prefix", "{{DARK_GREEN}}[{{WHITE}}Money{{DARK_GREEN}}]{{WHITE}} ");
		languageManager.addLanguageEntry("group_help_title", "{{DARK_GREEN}} ======== Group Commands ========");
		languageManager.addLanguageEntry("group_help_cmd_help", "/ccgroup - Shows ccgroup help.");
		languageManager.addLanguageEntry("money_infinite_cmd_help", "/money infinite <Account Name> - Put a account in infinite money mode.");
		languageManager.addLanguageEntry("money_infinite_set_false", "The account is no more infinite!");
		languageManager.addLanguageEntry("money_infinite_set_true", "The account is now infinite!");
		languageManager.addLanguageEntry("money_log_header", "{{DARK_GREEN}} Money Log | Page {{WHITE}}%s {{DARK_GREEN}} | Account {{WHITE}}%s");
		languageManager.addLanguageEntry("money_log_cmd_help", "/money log <page> [Account Name] - Show the account log");
		languageManager.addLanguageEntry("bank_list_cmd_help", "/bank list - List all the bank accounts you have access");
		languageManager.addLanguageEntry("bank_account_list", "List of bank accounts: %s");
		languageManager.addLanguageEntry("currency_rates_cmd_help", "/currency rates - Show all the exchange rates available.");
		languageManager.addLanguageEntry("rates_header", "{{DARK_GREEN}}[Currency rates]");
		languageManager.addLanguageEntry("bank_delete_cmd_help", "/bank delete <Name> - Delete a bank account that you own.");
		languageManager.addLanguageEntry("bank_delete_not_owner", "{{DARK_RED}}You aren't this bank owner!");
		languageManager.addLanguageEntry("currency_list_cmd_help", "/currency list - List all the currencies");
		languageManager.addLanguageEntry("currency_list_title", "{{DARK_GREEN}}====== {{WHITE}}Currencies {{DARK_GREEN}}======");
	}

	private void initializeConfig() {
		mainConfig.setValue("System.Setup", true);
		mainConfig.setValue("System.QuickSetup.Enable", false);
		mainConfig.setValue("System.QuickSetup.Currency.Name", "Dollar");
		mainConfig.setValue("System.QuickSetup.Currency.NamePlural", "Dollars");
		mainConfig.setValue("System.QuickSetup.Currency.Minor", "Coin");
		mainConfig.setValue("System.QuickSetup.Currency.MinorPlural", "Coins");
		mainConfig.setValue("System.QuickSetup.Currency.Sign", "$");
		mainConfig.setValue("System.QuickSetup.StartBalance", 100.0);
		mainConfig.setValue("System.QuickSetup.PriceBank", 200.0);
		mainConfig.setValue("System.QuickSetup.DisplayMode", "long");
		mainConfig.setValue("System.CheckNewVersion", true);
		mainConfig.setValue("System.Case-sentitive", false);
		mainConfig.setValue("System.CreateOnLogin", false);
		mainConfig.setValue("System.Logging.Enabled", false);
		mainConfig.setValue("System.Database.Type", "sqlite");
		mainConfig.setValue("System.Database.Address", "localhost");
		mainConfig.setValue("System.Database.Port", 3306);
		mainConfig.setValue("System.Database.Username", "root");
		mainConfig.setValue("System.Database.Password", "");
		mainConfig.setValue("System.Database.Db", "craftconomy");
		mainConfig.setValue("System.Database.Prefix", "cc3_");
	}

	private void updateDatabase() {
		ConfigTable dbVersion = Common.getInstance().getDatabaseManager().getDatabase().select(ConfigTable.class).where().equal(ConfigTable.NAME_FIELD, "dbVersion").execute().findOne();
		if (dbVersion == null) {
			alertOldDbVersion("0", 1);
			List<PayDayTable> payday = Common.getInstance().getDatabaseManager().getDatabase().select(PayDayTable.class).execute().find();
			if (payday != null) {
				for (PayDayTable entry : payday) {
					entry.setName(entry.getName().toLowerCase());
					Common.getInstance().getDatabaseManager().getDatabase().save(entry);
				}
			}
			dbVersion = new ConfigTable();
			dbVersion.setName("dbVersion");
			dbVersion.setValue(1 + "");
			Common.getInstance().getDatabaseManager().getDatabase().save(dbVersion);
			Common.getInstance().getLogger().info("Updated to Revision 1!");
		}
		if (dbVersion.getValue().equalsIgnoreCase("")) {
			alertOldDbVersion(dbVersion.getValue(), 1);
			dbVersion.setValue(1 + "");
			Common.getInstance().getDatabaseManager().getDatabase().save(dbVersion);
			Common.getInstance().getLogger().info("Really updated to Revision 1.");
		}
		if (dbVersion.getValue().equalsIgnoreCase("1")) {
			alertOldDbVersion(dbVersion.getValue(), 2);
			// Testing to see if in the config we have true or false as the display value
			ConfigTable display = Common.getInstance().getDatabaseManager().getDatabase().select(ConfigTable.class).where().equal(ConfigTable.NAME_FIELD, "longmode").execute().findOne();
			if (display.getValue().equals("true") || display.getValue().equalsIgnoreCase("false")) {
				if (display.getValue().equals("true")) {
					display.setValue("long");
				}
				if (display.getValue().equals("false")) {
					display.setValue("short");
				}
				Common.getInstance().getDatabaseManager().getDatabase().save(display);
				dbVersion.setValue(2 + "");
				Common.getInstance().getDatabaseManager().getDatabase().save(dbVersion);
				Common.getInstance().getLogger().info("Updated to Revision 2!");
			}
		}
		if (dbVersion.getValue().equalsIgnoreCase("2")) {
			alertOldDbVersion(dbVersion.getValue(), 3);
			Common.getInstance().getLogger().info("Checking if the display format is valid.");
			ConfigTable display = Common.getInstance().getDatabaseManager().getDatabase().select(ConfigTable.class).where().equal(ConfigTable.NAME_FIELD, "longmode").execute().findOne();
			if (!display.getValue().equalsIgnoreCase("long") && !display.getValue().equalsIgnoreCase("small") && !display.getValue().equalsIgnoreCase("sign") && !display.getValue().equalsIgnoreCase("majoronly")) {
				Common.getInstance().getLogger().info("Display format is invalid. Saving a valid one.");
				display.setValue("long");
				Common.getInstance().getDatabaseManager().getDatabase().save(display);
			}
			dbVersion.setValue(3 + "");
			Common.getInstance().getDatabaseManager().getDatabase().save(dbVersion);
			Common.getInstance().getLogger().info("Updated to Revision 3!");
		}
		if (dbVersion.getValue().equalsIgnoreCase("3")) {
			alertOldDbVersion(dbVersion.getValue(), 4);
			Common.getInstance().getLogger().info("Converting worlds to the new format.");
			Common.getInstance().initializeWorldGroup();
			ResultSet result = Common.getInstance().getDatabaseManager().getDatabase().directQueryWithResult("SELECT DISTINCT worldName FROM " + getMainConfig().getString("System.Database.Prefix") + "balance");
			try {
				while (result.next()) {
					String entry = result.getString("worldName");
					Common.getInstance().getWorldGroupManager().addWorldGroup(entry);
					Common.getInstance().getWorldGroupManager().addWorldToGroup(entry, entry);
				}
				result.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			dbVersion.setValue(4 + "");
			Common.getInstance().getDatabaseManager().getDatabase().save(dbVersion);
			Common.getInstance().getLogger().info("Updated to Revision 4!");
		}
		if (dbVersion.getValue().equalsIgnoreCase("4")) {
			alertOldDbVersion(dbVersion.getValue(), 5);
			Common.getInstance().getDatabaseManager().getDatabase().directQuery("UPDATE " + getMainConfig().getString("System.Database.Prefix") + "balance SET worldName='default' WHERE worldName='any';");
			dbVersion.setValue(5 + "");
			Common.getInstance().getDatabaseManager().getDatabase().save(dbVersion);
			Common.getInstance().getLogger().info("Updated to Revision 5!");
		}
		if (dbVersion.getValue().equalsIgnoreCase("5")) {
			alertOldDbVersion(dbVersion.getValue(), 6);
			Common.getInstance().sendConsoleMessage(Level.INFO, "Notice: This may take some time.");
			List<BalanceTable> entryList = Common.getInstance().getDatabaseManager().getDatabase().select(BalanceTable.class).execute().find();
			int amount = 0;
			for (BalanceTable entry : entryList) {
				entry.setBalance(Account.format(entry.getBalance()));
				Common.getInstance().getDatabaseManager().getDatabase().save(entry);
				if (amount % 100 == 0) {
					Common.getInstance().sendConsoleMessage(Level.INFO, amount + " of " + entryList.size() + " updated!");
				}
				amount++;
			}
			dbVersion.setValue(6 + "");
			Common.getInstance().getDatabaseManager().getDatabase().save(dbVersion);
			Common.getInstance().getLogger().info("Updated to Revision 6!");
		}
	}

	private void alertOldDbVersion(String currentVersion, int newVersion) {
		Common.getInstance().getLogger().info("Your database is out of date! (Version " + currentVersion + "). Updating it to Revision " + newVersion + ".");
	}
}
