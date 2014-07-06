package com.greatmancode.craftconomy3.converter;

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.database.tables.*;
import com.greatmancode.tools.database.DatabaseManager;
import com.greatmancode.tools.database.interfaces.DatabaseType;
import com.greatmancode.tools.database.throwable.InvalidDatabaseConstructor;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * Created by greatman on 2014-07-06.
 */
public class SQLiteToMySQLConverter {

    public Map<Integer, Account> accountList = new HashMap<Integer, Account>();
    public Map<Integer, Currency> currencyList = new HashMap<Integer, Currency>();
    public List<Config> configList = new ArrayList<Config>();
    public List<Exchange> exchangeList = new ArrayList<Exchange>();

    public void run() throws InvalidDatabaseConstructor {
        Common.getInstance().sendConsoleMessage(Level.INFO, Common.getInstance().getLanguageManager().getString("starting_database_convert"));
        DatabaseManager sqliteManager = new DatabaseManager(DatabaseType.SQLITE, Common.getInstance().getMainConfig().getString("System.Database.Prefix"), new File(Common.getInstance().getServerCaller().getDataFolder(), "database.db"), Common.getInstance().getServerCaller());
        try {
            Connection connection = sqliteManager.getDatabase().getConnection();
            Common.getInstance().sendConsoleMessage(Level.INFO, Common.getInstance().getLanguageManager().getString("convert_save_account"));
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM "+sqliteManager.getTablePrefix()+ AccountTable.TABLE_NAME);
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                Account account = new Account();
                account.id = set.getInt("id");
                account.name = set.getString("name");
                account.ignoreACL = set.getBoolean("ignoreACL");
                account.uuid = set.getString("uuid");
                account.infiniteMoney = set.getBoolean("ignoreACL");
                account.bank = set.getBoolean("bank");
                accountList.put(account.id, account);
            }
            set.close();
            statement.close();
            statement = connection.prepareStatement("SELECT * FROM "+sqliteManager.getTablePrefix()+ CurrencyTable.TABLE_NAME);
            set = statement.executeQuery();
            while (set.next()) {
                Currency currency = new Currency();
                currency.id = set.getInt("id");
                currency.name = set.getString("name");
                currency.plural = set.getString("plural");
                currency.minor = set.getString("minor");
                currency.minorPlural = set.getString("minorPlural");
                currency.hardCap = set.getDouble("hardCap");
                currency.sign = set.getString("sign");
                currency.status = set.getBoolean("status");
                currencyList.put(currency.id, currency);
            }
            set.close();
            statement.close();
            statement = connection.prepareStatement("SELECT * FROM "+sqliteManager.getTablePrefix()+ BalanceTable.TABLE_NAME);
            set = statement.executeQuery();
            while (set.next()) {
                Balance balance = new Balance();
                balance.balance = set.getDouble("balance");
                balance.worldName = set.getString("worldName");
                balance.currency_id = set.getInt("currency_id");
                accountList.get(set.getInt("account_id")).balanceList.add(balance);
            }
            set.close();
            statement.close();
            statement = connection.prepareStatement("SELECT * FROM "+sqliteManager.getTablePrefix()+ AccessTable.TABLE_NAME);
            set = statement.executeQuery();
            while (set.next()) {
                Access access = new Access();
                access.acl = set.getBoolean("acl");
                access.balance = set.getBoolean("balance");
                access.deposit = set.getBoolean("deposit");
                access.owner = set.getBoolean("owner");
                access.withdraw = set.getBoolean("withdraw");
                access.playerName = set.getString("playerName");
                accountList.get(set.getInt("account_id")).accessList.add(access);
            }
            set.close();
            statement.close();
            statement = connection.prepareStatement("SELECT * FROM "+sqliteManager.getTablePrefix()+ ConfigTable.TABLE_NAME);
            set = statement.executeQuery();
            while (set.next()) {
                Config config = new Config();
                config.name = set.getString("name");
                config.value = set.getString("value");
                configList.add(config);
            }
            set.close();
            statement.close();
            statement = connection.prepareStatement("SELECT * FROM "+sqliteManager.getTablePrefix()+ExchangeTable.TABLE_NAME);
            set = statement.executeQuery();
            while (set.next()) {
                Exchange exchange = new Exchange();
                exchange.currency_id_from = set.getInt("from_currency_id");
                exchange.currency_id_to = set.getInt("to_currency_id");
                exchange.amount = set.getInt("amount");
                exchangeList.add(exchange);
            }
            set.close();
            statement.close();
            statement = connection.prepareStatement("SELECT * FROM "+sqliteManager.getTablePrefix()+LogTable.TABLE_NAME);
            set = statement.executeQuery();
            while (set.next()) {
                Log log = new Log();
                log.amount = set.getDouble("amount");
                log.cause = set.getString("cause");
                log.causeReason = set.getString("causeReason");
                log.currency_id = set.getInt("currency_id");
                log.timestamp = set.getTimestamp("timestamp");
                log.type = set.getString("type");
                log.worldName = set.getString("worldName");
                accountList.get(set.getInt("account_id")).logList.add(log);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private class Account {
        public String name, uuid;
        public boolean infiniteMoney, ignoreACL, bank;
        public int id, newID;
        public List<Balance> balanceList = new ArrayList<Balance>();
        public List<Access> accessList = new ArrayList<Access>();
        public List<Log> logList = new ArrayList<Log>();
    }

    private class Currency {
        public int id;
        public String name, plural, minor, minorPlural, sign;
        public double hardCap;
        public boolean status;
    }
    private class Balance {
        public double balance;
        public String worldName;
        public int currency_id;
    }

    private class Access {
        public boolean owner, balance, deposit, acl, withdraw;
        public String playerName;
    }

    private class Config {
        public String name, value;
    }

    private class Exchange {
        public int currency_id_from, currency_id_to;
        public double amount;
    }

    private class Log {
        public String type, cause, causeReason, worldName;
        public Timestamp timestamp;
        public double amount;
        public int currency_id;
    }

    private class WorldGroup {
        public String worldList, groupName;
    }
}
