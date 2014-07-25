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

import com.greatmancode.craftconomy3.Cause;
import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.LogInfo;
import com.greatmancode.craftconomy3.storage.sql.tables.*;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.File;
import java.sql.*;
import java.util.*;
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
    private HikariDataSource db;
    private String prefix;

    public void run() {
        Common.getInstance().sendConsoleMessage(Level.INFO, Common.getInstance().getLanguageManager().getString("starting_database_convert"));
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setMaximumPoolSize(10);
        hikariConfig.setDataSourceClassName("com.sqlite.SQLiteDataSource");
        hikariConfig.addDataSourceProperty("databaseName", new File(Common.getInstance().getServerCaller().getDataFolder(), "database.db").getAbsolutePath());
        db = new HikariDataSource(hikariConfig);
        prefix = Common.getInstance().getMainConfig().getString("System.Database.Prefix");
        try {
            Connection connection = db.getConnection();

            Common.getInstance().sendConsoleMessage(Level.INFO, "Getting accounts information");
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + prefix + AccountTable.TABLE_NAME);
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                Account account = new Account();
                account.id = set.getInt("id");
                account.name = set.getString("name");
                account.ignoreACL = set.getBoolean("ignoreACL");
                account.uuid = UUID.fromString(set.getString("uuid"));
                account.infiniteMoney = set.getBoolean("ignoreACL");
                account.bank = set.getBoolean("bank");
                accountList.put(account.id, account);
            }
            set.close();
            statement.close();

            Common.getInstance().sendConsoleMessage(Level.INFO, "Getting currency table information");
            statement = connection.prepareStatement("SELECT * FROM " + prefix + CurrencyTable.TABLE_NAME);
            set = statement.executeQuery();
            while (set.next()) {
                Currency currency = new Currency(set.getInt("id"), set.getString("name"), set.getString("plural"), set.getString("minor"), set.getString("minorPlural"), set.getString("sign"), set.getBoolean("status"));
                currencyList.put(currency.id, currency);
            }
            set.close();
            statement.close();

            Common.getInstance().sendConsoleMessage(Level.INFO, "Getting Balance table information");
            statement = connection.prepareStatement("SELECT * FROM " + prefix + BalanceTable.TABLE_NAME);
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
            statement = connection.prepareStatement("SELECT * FROM " + prefix + AccessTable.TABLE_NAME);
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
            statement = connection.prepareStatement("SELECT * FROM " + prefix + ConfigTable.TABLE_NAME);
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
            statement = connection.prepareStatement("SELECT * FROM " + prefix + ExchangeTable.TABLE_NAME);
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
            statement = connection.prepareStatement("SELECT * FROM " + prefix + LogTable.TABLE_NAME);
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
            statement = connection.prepareStatement("SELECT * FROM " + prefix + WorldGroupTable.TABLE_NAME);
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
                Common.getInstance().getStorageHandler().getStorageEngine().saveCurrency(currency.getValue().getName(), currency.getValue());
            }

            Common.getInstance().sendConsoleMessage(Level.INFO, "Inserting config information");
            for (Config config : configList) {
                Common.getInstance().getStorageHandler().getStorageEngine().setConfigEntry(config.name, config.value);
            }

            Common.getInstance().sendConsoleMessage(Level.INFO, "Inserting Exchange information");
            for (Exchange exchange : exchangeList) {
                Common.getInstance().getStorageHandler().getStorageEngine().setExchangeRate(currencyList.get(exchange.currency_id_from), currencyList.get(exchange.currency_id_to), exchange.amount);
            }

            Common.getInstance().sendConsoleMessage(Level.INFO, "Inserting World Group information");
            for (WorldGroup worldGroup : worldGroupList) {
                Common.getInstance().getStorageHandler().getStorageEngine().saveWorldGroup(worldGroup.groupName, worldGroup.worldList);
            }

            Common.getInstance().sendConsoleMessage(Level.INFO, "Inserting account/balance/log/access information");
            for (Map.Entry<Integer, Account> accountEntry : accountList.entrySet()) {
                com.greatmancode.craftconomy3.account.Account account = Common.getInstance().getStorageHandler().getStorageEngine().getAccount(accountEntry.getValue().name, accountEntry.getValue().bank);
                Common.getInstance().getStorageHandler().getStorageEngine().updateUUID(accountEntry.getValue().name, accountEntry.getValue().uuid);
                Common.getInstance().getStorageHandler().getStorageEngine().setInfiniteMoney(account, accountEntry.getValue().infiniteMoney);
                Common.getInstance().getStorageHandler().getStorageEngine().setIgnoreACL(account, accountEntry.getValue().ignoreACL);

                for (Balance balance : accountEntry.getValue().balanceList) {
                    account.set(balance.balance, balance.worldName, currencyList.get(balance.currency_id).getName(), Cause.CONVERT, "SQLite=>MySQL conversion");
                }
                for (Access access : accountEntry.getValue().accessList) {
                    account.getAccountACL().set(access.playerName, access.deposit, access.withdraw, access.acl, access.balance, access.owner);
                }
                for (Log log : accountEntry.getValue().logList) {
                    Common.getInstance().getStorageHandler().getStorageEngine().saveLog(LogInfo.valueOf(log.type.toUpperCase()), Cause.valueOf(log.cause.toUpperCase()), log.causeReason, account, log.amount, currencyList.get(log.currency_id), log.worldName, log.timestamp);
                }
            }
            connection.close();
            db.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private class Account {
        public String name;
        public UUID uuid;
        public boolean infiniteMoney, ignoreACL, bank;
        public int id;
        public List<Balance> balanceList = new ArrayList<Balance>();
        public List<Access> accessList = new ArrayList<Access>();
        public List<Log> logList = new ArrayList<Log>();
    }

    private class Currency extends com.greatmancode.craftconomy3.currency.Currency{
        public int id;

        public Currency(int id, String name, String plural, String minor, String minorPlural, String sign, boolean status) {
            super(name, plural, minor, minorPlural, sign, status);
            this.id = id;
        }
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
