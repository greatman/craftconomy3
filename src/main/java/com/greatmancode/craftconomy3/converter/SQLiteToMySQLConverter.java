/*
 * This file is part of Craftconomy3.
 *
 * Copyright (c) 2011-2014, Greatman <http://github.com/greatman/>
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

    private Map<Integer, Account> accountList = new HashMap<Integer, Account>();
    private Map<Integer, Currency> currencyList = new HashMap<Integer, Currency>();
    private List<Config> configList = new ArrayList<Config>();
    private List<Exchange> exchangeList = new ArrayList<Exchange>();
    private List<WorldGroup> worldGroupList = new ArrayList<WorldGroup>();

    public void run() throws InvalidDatabaseConstructor {
        Common.getInstance().sendConsoleMessage(Level.INFO, Common.getInstance().getLanguageManager().getString("starting_database_convert"));
        DatabaseManager sqliteManager = new DatabaseManager(DatabaseType.SQLITE, Common.getInstance().getMainConfig().getString("System.Database.Prefix"), new File(Common.getInstance().getServerCaller().getDataFolder(), "database.db"), Common.getInstance().getServerCaller());
        try {
            Connection connection = sqliteManager.getDatabase().getConnection();

            Common.getInstance().sendConsoleMessage(Level.INFO, "Getting accounts information");
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

            Common.getInstance().sendConsoleMessage(Level.INFO, "Getting currency table information");
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

            Common.getInstance().sendConsoleMessage(Level.INFO, "Getting Balance table information");
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

            Common.getInstance().sendConsoleMessage(Level.INFO, "Getting access table information");
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

            Common.getInstance().sendConsoleMessage(Level.INFO, "Getting config table information");
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

            Common.getInstance().sendConsoleMessage(Level.INFO, "Getting Exchange table information");
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

            Common.getInstance().sendConsoleMessage(Level.INFO, "Getting log table information");
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
            set.close();
            statement.close();

            Common.getInstance().sendConsoleMessage(Level.INFO, "Getting world group information");
            statement = connection.prepareStatement("SELECT * FROM "+sqliteManager.getTablePrefix()+WorldGroupTable.TABLE_NAME);
            set = statement.executeQuery();
            while (set.next()) {
                WorldGroup worldGroup = new WorldGroup();
                worldGroup.groupName = set.getString("groupName");
                worldGroup.worldList = set.getString("worldList");
                worldGroupList.add(worldGroup);
            }
            set.close();
            statement.close();

            Common.getInstance().sendConsoleMessage(Level.INFO, "Inserting currency information");
            for (Map.Entry<Integer, Currency> currency : currencyList.entrySet()) {
                statement = connection.prepareStatement(CurrencyTable.INSERT_ENTRY);
                statement.setString(1, currency.getValue().name);
                statement.setString(2, currency.getValue().plural);
                statement.setString(3, currency.getValue().minor);
                statement.setString(4, currency.getValue().minorPlural);
                statement.setString(5, currency.getValue().sign);
                statement.executeUpdate();
                statement.close();
            }

            Common.getInstance().sendConsoleMessage(Level.INFO, "Inserting config information");
            for(Config config : configList) {
                statement = connection.prepareStatement(ConfigTable.INSERT_ENTRY);
                statement.setString(1,config.name);
                statement.setString(2, config.value);
                statement.executeUpdate();
                statement.close();
            }

            Common.getInstance().sendConsoleMessage(Level.INFO, "Inserting Exchange information");
            for (Exchange exchange :exchangeList) {
                statement = connection.prepareStatement(ExchangeTable.INSERT_ENTRY);
                statement.setString(1, currencyList.get(exchange.currency_id_from).name);
                statement.setString(2, currencyList.get(exchange.currency_id_to).name);
                statement.setDouble(3, exchange.amount);
                statement.executeUpdate();
                statement.close();
            }

            Common.getInstance().sendConsoleMessage(Level.INFO, "Inserting World Group information");
            for (WorldGroup worldGroup : worldGroupList) {
                statement = connection.prepareStatement(WorldGroupTable.INSERT_ENTRY);
                statement.setString(1, worldGroup.groupName);
                statement.setString(2, worldGroup.worldList);
                statement.executeUpdate();
                statement.close();
            }

            Common.getInstance().sendConsoleMessage(Level.INFO, "Inserting account/balance/log/access information");
            for (Map.Entry<Integer, Account> account : accountList.entrySet()) {
                statement = connection.prepareStatement(AccountTable.INSERT_ENTRY);
                statement.setString(1, account.getValue().name);
                statement.setString(2, account.getValue().uuid);
                statement.setBoolean(3, account.getValue().infiniteMoney);
                statement.setBoolean(4, account.getValue().ignoreACL);
                statement.setBoolean(5, account.getValue().bank);
                statement.executeUpdate();
                statement.close();

                for (Balance balance : account.getValue().balanceList) {
                    statement = connection.prepareStatement(BalanceTable.INSERT_ENTRY);
                    statement.setDouble(1, balance.balance);
                    statement.setString(2, balance.worldName);
                    statement.setString(3, account.getValue().name);
                    statement.setString(4, currencyList.get(balance.currency_id).name);
                    statement.executeUpdate();
                    statement.close();
                }
                for (Access access : account.getValue().accessList) {
                    statement = connection.prepareStatement(AccessTable.INSERT_ENTRY);
                    statement.setString(1, account.getValue().name);
                    statement.setString(2, access.playerName);
                    statement.setBoolean(3, access.owner);
                    statement.setBoolean(4, access.balance);
                    statement.setBoolean(5, access.deposit);
                    statement.setBoolean(6, access.acl);
                    statement.setBoolean(7, access.withdraw);
                    statement.executeUpdate();
                    statement.close();
                }
                for (Log log : account.getValue().logList) {
                    statement = connection.prepareStatement(LogTable.INSERT_ENTRY);
                    statement.setString(1, account.getValue().name);
                    statement.setString(2, log.type);
                    statement.setString(3, log.cause);
                    statement.setString(4, log.causeReason);
                    statement.setString(5, log.worldName);
                    statement.setTimestamp(6, log.timestamp);
                    statement.setDouble(7,log.amount);
                    statement.setString(8, currencyList.get(log.currency_id).name);
                    statement.executeUpdate();
                    statement.close();
                }
            }
            connection.close();
            sqliteManager.getDatabase().close();

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
