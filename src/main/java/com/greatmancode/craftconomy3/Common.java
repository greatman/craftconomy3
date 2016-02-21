/**
 * This file is part of Craftconomy3.
 *
 * Copyright (c) 2011-2016, Greatman <http://github.com/greatman/>
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

import com.greatmancode.craftconomy3.account.Account;
import com.greatmancode.craftconomy3.account.AccountManager;
import com.greatmancode.craftconomy3.commands.bank.*;
import com.greatmancode.craftconomy3.commands.config.*;
import com.greatmancode.craftconomy3.commands.currency.*;
import com.greatmancode.craftconomy3.commands.group.GroupAddWorldCommand;
import com.greatmancode.craftconomy3.commands.group.GroupCreateCommand;
import com.greatmancode.craftconomy3.commands.group.GroupDelWorldCommand;
import com.greatmancode.craftconomy3.commands.money.*;
import com.greatmancode.craftconomy3.commands.setup.*;
import com.greatmancode.craftconomy3.converter.H2ToMySQLConverter;
import com.greatmancode.craftconomy3.currency.Currency;
import com.greatmancode.craftconomy3.currency.CurrencyManager;
import com.greatmancode.craftconomy3.events.EventManager;
import com.greatmancode.craftconomy3.groups.WorldGroupsManager;
import com.greatmancode.craftconomy3.storage.StorageHandler;
import com.greatmancode.craftconomy3.utils.OldFormatConverter;
import com.greatmancode.tools.caller.bukkit.BukkitServerCaller;
import com.greatmancode.tools.caller.unittest.UnitTestServerCaller;
import com.greatmancode.tools.commands.CommandHandler;
import com.greatmancode.tools.commands.SubCommand;
import com.greatmancode.tools.configuration.Config;
import com.greatmancode.tools.configuration.ConfigurationManager;
import com.greatmancode.tools.interfaces.caller.ServerCaller;
import com.greatmancode.tools.language.LanguageManager;
import com.greatmancode.tools.utils.FeatherBoard;
import com.greatmancode.tools.utils.Metrics;
import com.greatmancode.tools.utils.Tools;
import com.greatmancode.tools.utils.Updater;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The core of Craftconomy. Every requests pass through this class
 *
 * @author greatman
 */
public class Common implements com.greatmancode.tools.interfaces.Common {
    private Logger log = null;
    private static Common instance = null;
    // Managers
    private AccountManager accountManager = null;
    private ConfigurationManager config = null;
    private CurrencyManager currencyManager = null;
    private StorageHandler storageHandler = null;
    private EventManager eventManager = null;
    private LanguageManager languageManager = null;
    private WorldGroupsManager worldGroupManager = null;
    private CommandHandler commandManager = null;
    private ServerCaller serverCaller = null;
    private boolean databaseInitialized = false;
    private boolean currencyInitialized = false;
    private static boolean initialized = false;
    private Metrics metrics = null;
    private Config mainConfig = null;
    private Updater updater;
    //Default values
    private DisplayFormat displayFormat = null;
    private double holdings = 0.0;
    private double bankPrice = 0.0;

    /**
     * Initialize the Common core.
     */
    public void onEnable(ServerCaller serverCaller, final Logger log) {
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
            if (!getMainConfig().has("System.Database.Poolsize")) {
                getMainConfig().setValue("System.Database.Poolsize", 10);
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
            if (getMainConfig().getBoolean("System.CheckNewVersion") && (serverCaller instanceof BukkitServerCaller)) {
                updater = new Updater(serverCaller, 35564, Updater.UpdateType.NO_DOWNLOAD, false);
                if (updater.getResult() == Updater.UpdateResult.UPDATE_AVAILABLE) {
                    sendConsoleMessage(Level.WARNING, getLanguageManager().parse("running_old_version", updater.getLatestName()));
                }
            }
            sendConsoleMessage(Level.INFO, "Loading listeners.");
            serverCaller.getLoader().getEventManager().registerEvents(this, new EventManager());
            sendConsoleMessage(Level.INFO, "Loading commands");
            Common.getInstance().getServerCaller().registerPermission("craftconomy.*");
            commandManager = new CommandHandler(serverCaller);
            registerCommands();
            if (getMainConfig().getBoolean("System.Setup")) {

                //We got quick setup. Let's do it!!!!
                if (getMainConfig().getBoolean("System.QuickSetup.Enable")) {
                    quickSetup();
                    reloadPlugin();
                } else {
                    sendConsoleMessage(Level.WARNING, getLanguageManager().getString("loaded_setup_mode"));
                }
            } else {
                commandManager.setCurrentLevel(1);
                initialiseDatabase();
                updateDatabase();
                initializeCurrency();
                sendConsoleMessage(Level.INFO, getLanguageManager().getString("loading_default_settings"));
                loadDefaultSettings();
                sendConsoleMessage(Level.INFO, getLanguageManager().getString("default_settings_loaded"));
                startUp();
                sendConsoleMessage(Level.INFO, getLanguageManager().getString("ready"));
            }


            getServerCaller().registerPermission("craftconomy.money.log.others");
            addFeatherboardSupport();
            initialized = true;
        }
    }

    /**
     * Disable the plugin.
     */
    @Override
    public void onDisable() {
        if (getStorageHandler() != null) {
            getLogger().info(getLanguageManager().getString("closing_db_link"));
            getStorageHandler().disable();
        }
        // Managers
        accountManager = null;
        config = null;
        currencyManager = null;
        storageHandler = null;
        eventManager = null;
        languageManager = null;
        worldGroupManager = null;
        commandManager = null;
        databaseInitialized = false;
        currencyInitialized = false;
        initialized = false;
        metrics = null;
        mainConfig = null;
        updater = null;
        //Default values
        displayFormat = null;
        holdings = 0.0;
        bankPrice = 0.0;
    }

    /**
     * Reload the plugin.
     */
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
        commandManager.setCurrentLevel(1);
        initialiseDatabase();
        updateDatabase();
        initializeCurrency();
        sendConsoleMessage(Level.INFO, getLanguageManager().getString("loading_default_settings"));
        loadDefaultSettings();
        sendConsoleMessage(Level.INFO, getLanguageManager().getString("default_settings_loaded"));
        startUp();
        sendConsoleMessage(Level.INFO, getLanguageManager().getString("ready"));

    }

    /**
     * Retrieve the main configuration file
     *
     * @return the main configuration file
     */
    public Config getMainConfig() {
        return mainConfig;
    }

    /**
     * Retrieve the logger associated with this plugin.
     *
     * @return The logger instance.
     */
    public Logger getLogger() {
        return log;
    }

    /**
     * Sends a message to the console through the Logge.r
     *
     * @param level The log level to show.
     * @param msg   The message to send.
     */
    public void sendConsoleMessage(Level level, String msg) {
        if (!(getServerCaller() instanceof UnitTestServerCaller)) {
            getLogger().log(level, msg);
        }
    }

    /**
     * Retrieve the instance of Common. Need to go through that to access any managers.
     *
     * @return The Common instance.
     */
    public static Common getInstance() {
        return instance;
    }

    /**
     * Retrieve the Account Manager.
     *
     * @return The Account Manager instance or null if the manager is not initialized.
     */
    public AccountManager getAccountManager() {
        return accountManager;
    }

    /**
     * Retrieve the Configuration Manager.
     *
     * @return The Configuration Manager instance or null if the manager is not initialized.
     */
    public ConfigurationManager getConfigurationManager() {
        return config;
    }

    /**
     * Retrieve the Storage Handler.
     *
     * @return The Storage Handler instance or null if the handler is not initialized.
     */
    public StorageHandler getStorageHandler() {
        return storageHandler;
    }

    /**
     * Retrieve the Currency Manager.
     *
     * @return The Currency Manager instance or null if the manager is not initialized.
     */
    public CurrencyManager getCurrencyManager() {
        return currencyManager;
    }

    /**
     * Retrieve the Command Manager.
     *
     * @return The Command Manager instance or null if the manager is not initialized.
     */
    public CommandHandler getCommandManager() {
        return commandManager;
    }

    /**
     * Retrieve the Server Caller.
     *
     * @return The Server Caller instance or null if the caller is not initialized.
     */
    public ServerCaller getServerCaller() {
        return serverCaller;
    }

    /**
     * Format a balance to a readable string.
     *
     * @param worldName The world Name associated with this balance
     * @param currency  The currency instance associated with this balance.
     * @param balance   The balance.
     * @param format    the display format to use
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
            if (balance > 1.0 || balance < 1.0) {
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
            String amount;
            try {
                amount = decimalFormat.format(Double.parseDouble(theAmount[0]));
            } catch (NumberFormatException e) {
                amount = theAmount[0];
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
            } else if (format == DisplayFormat.SIGNFRONT) {
                string.append(amount).append(".").append(coin).append(currency.getSign());
            }else if (format == DisplayFormat.MAJORONLY) {
                string.append(amount).append(" ").append(name);
            }
        }
        return string.toString();
    }

    /**
     * Format a balance to a readable string with the default formatting.
     *
     * @param worldName The world Name associated with this balance
     * @param currency  The currency instance associated with this balance.
     * @param balance   The balance.
     * @return A pretty String showing the balance. Returns a empty string if currency is invalid.
     */
    public String format(String worldName, Currency currency, double balance) {
        return format(worldName, currency, balance, displayFormat);
    }

    /**
     * Initialize the database Manager
     */
    public void initialiseDatabase() {
        if (!databaseInitialized) {
            sendConsoleMessage(Level.INFO, getLanguageManager().getString("loading_database_manager"));
            storageHandler = new StorageHandler();

            //TODO: Re-support that
            if (getMainConfig().getBoolean("System.Database.ConvertFromH2")) {
                convertDatabase();
            }

            databaseInitialized = true;
            sendConsoleMessage(Level.INFO, getLanguageManager().getString("database_manager_loaded"));
        }
    }

    /**
     * Convert from SQLite to MySQL
     */
    private void convertDatabase(){
        sendConsoleMessage(Level.INFO, getLanguageManager().getString("starting_database_convert"));
        new H2ToMySQLConverter().run();
        sendConsoleMessage(Level.INFO, getLanguageManager().getString("convert_done"));
        getMainConfig().setValue("System.Database.ConvertFromH2", false);
    }

    /**
     * Initialize the {@link CurrencyManager}
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
     * Initialize the {@link WorldGroupsManager}
     */
    public void initializeWorldGroup() {
        if (worldGroupManager == null) {
            worldGroupManager = new WorldGroupsManager();
            sendConsoleMessage(Level.INFO, getLanguageManager().getString("world_group_manager_loaded"));
        }
    }

    /**
     * Initialize the {@link AccountManager}, Metrics and {@link EventManager}
     */
    public void startUp() {
        sendConsoleMessage(Level.INFO, getLanguageManager().getString("loading_account_manager"));
        accountManager = new AccountManager();
        //addMetricsGraph("Multiworld", getConfigurationManager().isMultiWorld());
        startMetrics();
        sendConsoleMessage(Level.INFO, getLanguageManager().getString("account_manager_loaded"));
        eventManager = new EventManager();
        initializeWorldGroup();
    }

    /**
     * Add a graph to Metrics
     *
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
     *
     * @param title The title of the Graph
     * @param value The value of the entry
     */
    public void addMetricsGraph(String title, boolean value) {
        addMetricsGraph(title, value ? "Yes" : "No");
    }

    /**
     * Start Metrics.
     */
    public void startMetrics() {
        if (metrics != null) {
            getLogger().info("Starting Metrics.");
            metrics.start();
        }
    }

    /**
     * Write a transaction to the Log.
     *
     * @param info        The type of transaction to log.
     * @param cause       The cause of the transaction.
     * @param causeReason The reason of the cause
     * @param account     The account being impacted by the change
     * @param amount      The amount of money in this transaction.
     * @param currency    The currency associated with this transaction
     * @param worldName   The world name associated with this transaction
     */
    public void writeLog(LogInfo info, Cause cause, String causeReason, Account account, double amount, Currency currency, String worldName) {
        if (getMainConfig().getBoolean("System.Logging.Enabled")) {
            getStorageHandler().getStorageEngine().saveLog(info, cause, causeReason, account, amount, currency, worldName);
        }
    }

    /**
     * Get the version Checker.
     *
     * @return The version checker. May return null if the system is disabled in the config.yml
     */
    public Updater getVersionChecker() {
        return updater;
    }

    /**
     * Retrieve the Event manager.
     *
     * @return The Event manager.
     */
    public EventManager getEventManager() {
        return eventManager;
    }

    /**
     * Retrieve the {@link com.greatmancode.tools.language.LanguageManager}
     *
     * @return The {@link com.greatmancode.tools.language.LanguageManager}
     */
    public LanguageManager getLanguageManager() {
        return languageManager;
    }

    /**
     * Retrieve the {@link WorldGroupsManager}
     *
     * @return The {@link WorldGroupsManager}
     */
    public WorldGroupsManager getWorldGroupManager() {
        return worldGroupManager;
    }

    /**
     * Check if the system has been initialized.
     *
     * @return True if the system has been initialized else false.
     */
    public static boolean isInitialized() {
        return initialized;
    }

    /**
     * Reload the default settings.
     */
    public void loadDefaultSettings() {
        String value = getStorageHandler().getStorageEngine().getConfigEntry("longmode");
        if (value != null) {
            displayFormat = DisplayFormat.valueOf(value.toUpperCase());
        } else {
            getStorageHandler().getStorageEngine().setConfigEntry("longmode", "long");
            displayFormat = DisplayFormat.LONG;
        }
        addMetricsGraph("Display Format", displayFormat.toString());
        value = getStorageHandler().getStorageEngine().getConfigEntry("holdings");
        if (value != null && Tools.isValidDouble(value)) {
            holdings = Double.parseDouble(value);
        } else {
            getStorageHandler().getStorageEngine().setConfigEntry("holdings", 100.0 + "");
            sendConsoleMessage(Level.SEVERE, "No default value was set for account creation or was invalid! Defaulting to 100.");
            holdings = 100.0;
        }
        value = getStorageHandler().getStorageEngine().getConfigEntry("bankprice");
        if (value != null && Tools.isValidDouble(value)) {
            bankPrice = Double.parseDouble(value);
        } else {
            getStorageHandler().getStorageEngine().setConfigEntry("bankprice", 100.0 + "");
            sendConsoleMessage(Level.SEVERE, "No default value was set for bank creation or was invalid! Defaulting to 100.");
            bankPrice = 100.0;
        }
    }

    /**
     * Retrieve the display format for any formatting through {@link #format(String, com.greatmancode.craftconomy3.currency.Currency, double, DisplayFormat)}
     *
     * @return the display format used.
     */
    public DisplayFormat getDisplayFormat() {
        return displayFormat;
    }

    /**
     * Set the display format for any formatting through {@link #format(String, com.greatmancode.craftconomy3.currency.Currency, double, DisplayFormat)}
     *
     * @param format The format display to be set to
     */
    public void setDisplayFormat(DisplayFormat format) {
        getStorageHandler().getStorageEngine().setConfigEntry("longmode", format.toString());
        displayFormat = format;
    }

    /**
     * Get the default amount of money a account will have
     *
     * @return the default amount of money
     */
    public double getDefaultHoldings() {
        return holdings;
    }

    /**
     * Set the default amount of money a account will have
     *
     * @param value the default amount of money
     */
    public void setDefaultHoldings(double value) {
        getStorageHandler().getStorageEngine().setConfigEntry("holdings", String.valueOf(value));
        holdings = value;
    }

    /**
     * Retrieve the price of a bank account creation
     *
     * @return The price of a bank account creation
     */
    public double getBankPrice() {
        return bankPrice;
    }

    /**
     * Set the bank account creation price
     *
     * @param value the bank account creation price
     */
    public void setBankPrice(double value) {
        getStorageHandler().getStorageEngine().setConfigEntry("bankprice", String.valueOf(value));
        bankPrice = value;
    }

    /**
     * Perform a quick setup
     */
    private void quickSetup() {
        initialiseDatabase();
        Common.getInstance().initializeCurrency();
        Currency currency = Common.getInstance().getCurrencyManager().addCurrency(getMainConfig().getString("System.QuickSetup.Currency.Name"), getMainConfig().getString("System.QuickSetup.Currency.NamePlural"), getMainConfig().getString("System.QuickSetup.Currency.Minor"), getMainConfig().getString("System.QuickSetup.Currency.MinorPlural"), getMainConfig().getString("System.QuickSetup.Currency.Sign"), true);
        Common.getInstance().getCurrencyManager().setDefault(currency);
        Common.getInstance().getCurrencyManager().setDefaultBankCurrency(currency);
        getStorageHandler().getStorageEngine().setConfigEntry("longmode", DisplayFormat.valueOf(getMainConfig().getString("System.QuickSetup.DisplayMode").toUpperCase()).toString());
        getStorageHandler().getStorageEngine().setConfigEntry("holdings", getMainConfig().getString("System.QuickSetup.StartBalance"));
        getStorageHandler().getStorageEngine().setConfigEntry("bankprice", getMainConfig().getString("System.QuickSetup.PriceBank"));
        initializeCurrency();
        loadDefaultSettings();
        Common.getInstance().startUp();
        Common.getInstance().getMainConfig().setValue("System.Setup", false);
        commandManager.setCurrentLevel(1);
        sendConsoleMessage(Level.INFO, "Quick-Config done!");
    }

    /**
     * Register all the commands
     */
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
        bank.addCommand("ignoreacl", new BankIgnoreACLCommand());
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
        configCommand.addCommand("clearlog", new ConfigClearLogCommand());
        configCommand.addCommand("reload", new ConfigReloadCommand());
        commandManager.registerMainCommand("craftconomy", configCommand);

        SubCommand ccgroup = new SubCommand("ccgroup", commandManager, null, 1);
        ccgroup.addCommand("create", new GroupCreateCommand());
        ccgroup.addCommand("addworld", new GroupAddWorldCommand());
        ccgroup.addCommand("delworld", new GroupDelWorldCommand());
        commandManager.registerMainCommand("ccgroup", ccgroup);

        SubCommand payCommand = new SubCommand("pay", commandManager, null, 1);
        payCommand.addCommand("", new PayCommand());
        commandManager.registerMainCommand("pay", payCommand);
    }

    /**
     * Initialize the configuration file
     */
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
        languageManager.addLanguageEntry("config_format_cmd_help", "/craftconomy format <long/small/sign/signfront/majoronly> - Set the display format.");
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
        languageManager.addLanguageEntry("bank_account_deleted", "Bank account deleted");
        languageManager.addLanguageEntry("currency_list_cmd_help", "/currency list - List all the currencies");
        languageManager.addLanguageEntry("currency_list_title", "{{DARK_GREEN}}====== {{WHITE}}Currencies {{DARK_GREEN}}======");
        languageManager.addLanguageEntry("invalid_time_log", "Invalid time! It needs to be a positive number!");
        languageManager.addLanguageEntry("log_cleared", "The log table has been cleared up to the time you said!");
        languageManager.addLanguageEntry("craftconomy_clearlog_cmd_help", "/craftconomy clearlog <Time in days> - Clear the log table from entries olders than the value provided");
        languageManager.addLanguageEntry("bank_ignoreacl_cmd_help", "/bank ignoreacl <Account Name>  - Ignore the ACL system for that account.");
        languageManager.addLanguageEntry("account_is_ignoring_acl", "The account is now ignoring the ACL!");
        languageManager.addLanguageEntry("account_is_not_ignoring_acl", "The account is now following the ACL!");
        languageManager.addLanguageEntry("starting_database_convert", "Starting database convertion to MySQL. This can take come time.");
        languageManager.addLanguageEntry("convert_save_account", "Converting accounts... (1/9)");
        languageManager.addLanguageEntry("convert_save_balance", "Converting balances... (2/9)");
        languageManager.addLanguageEntry("convert_save_access", "Converting bank access... (3/9)");
        languageManager.addLanguageEntry("convert_save_currency", "Converting currencies... (4/9)");
        languageManager.addLanguageEntry("convert_save_config", "Converting config... (5/9)");
        languageManager.addLanguageEntry("convert_save_payday", "Converting payday... (6/9)");
        languageManager.addLanguageEntry("convert_save_exchange", "Converting exchange... (7/9)");
        languageManager.addLanguageEntry("convert_save_worldgroup", "Converting worldgroups... (8/9)");
        languageManager.addLanguageEntry("convert_save_log", "Converting logs... (9/9)");
        languageManager.addLanguageEntry("convert_done", "Conversion done!");
        languageManager.addLanguageEntry("config_reload_help_cmd", "/craftconomy reload - Reload craftconomy.");
        languageManager.addLanguageEntry("craftconomy_reloaded", "Craftconomy has been reloaded!");
    }

    /**
     * Initialize the configuration file
     */
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
        mainConfig.setValue("System.Database.Type", "h2");
        mainConfig.setValue("System.Database.Address", "localhost");
        mainConfig.setValue("System.Database.Port", 3306);
        mainConfig.setValue("System.Database.Username", "root");
        mainConfig.setValue("System.Database.Password", "");
        mainConfig.setValue("System.Database.Db", "craftconomy");
        mainConfig.setValue("System.Database.Prefix", "cc3_");
        mainConfig.setValue("System.Database.Poolsize", 10);
        mainConfig.setValue("System.Database.ConvertFromH2", false);
    }

    /**
     * Run a database update.
     */
    private void updateDatabase() {
        if (getMainConfig().getInt("Database.dbVersion") == 0) {
            alertOldDbVersion(0, 1);
            //We first check if we have the DB version in the database. If we do, we have a old layout in our hands
            String value = getStorageHandler().getStorageEngine().getConfigEntry("dbVersion");
            if (value != null) {
                //We have a old database, do the whole conversion
                try {
                    new OldFormatConverter().run();
                    getMainConfig().setValue("Database.dbVersion", 1);
                    sendConsoleMessage(Level.INFO, "Updated to Revision 1!");

                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                getMainConfig().setValue("Database.dbVersion", 1);
                sendConsoleMessage(Level.INFO, "Updated to Revision 1!");
            }
        } else if (getMainConfig().getInt("Database.dbVersion") == -1) {
            alertOldDbVersion(-1,1);
            try {
                    new OldFormatConverter().step2();
                    getMainConfig().setValue("Database.dbVersion", 1);
                    sendConsoleMessage(Level.INFO, "Updated to Revision 1!");

                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
        }
    }

    /**
     * Alert in the console of a database update.
     *
     * @param currentVersion The current version
     * @param newVersion     The database update version
     */
    private void alertOldDbVersion(int currentVersion, int newVersion) {
        Common.getInstance().sendConsoleMessage(Level.INFO, "Your database is out of date! (Version " + currentVersion + "). Updating it to Revision " + newVersion + ".");
    }

    private void addFeatherboardSupport() {
        if (getServerCaller() instanceof BukkitServerCaller && getServerCaller().isPluginEnabled("MVdWPlaceholderAPI")) {
            FeatherBoard.registerPlaceHolder(getServerCaller().getLoader(), "cc3money", new FeatherBoard.FeatherBoardReplaceEvent() {

                @Override
                public String getResult(String username, boolean isOnline) {
                    if (getAccountManager().exist(username, false)) {
                        return format(null, getCurrencyManager().getDefaultCurrency(), getAccountManager().getAccount(username, false).getBalance("default", getCurrencyManager().getDefaultCurrency().getName()), getDisplayFormat());
                    }
                    return "";
                }
            });
        }
    }

}
