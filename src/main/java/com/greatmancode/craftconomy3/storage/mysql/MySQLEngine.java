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
package com.greatmancode.craftconomy3.storage.mysql;

import com.greatmancode.craftconomy3.Cause;
import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.LogInfo;
import com.greatmancode.craftconomy3.account.Account;
import com.greatmancode.craftconomy3.account.AccountACLValue;
import com.greatmancode.craftconomy3.account.Balance;
import com.greatmancode.craftconomy3.commands.money.LogCommand;
import com.greatmancode.craftconomy3.commands.money.TopCommand;
import com.greatmancode.craftconomy3.currency.Currency;
import com.greatmancode.craftconomy3.database.tables.*;
import com.greatmancode.craftconomy3.groups.WorldGroup;
import com.greatmancode.craftconomy3.storage.StorageEngine;
import com.greatmancode.craftconomy3.utils.NoExchangeRate;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;
import java.util.*;

/**
 * Created by greatman on 2014-07-13.
 */
public class MySQLEngine extends StorageEngine {

    private final String tablePrefix;
    private final HikariDataSource db;
    private final AccessTable accessTable;
    private final AccountTable accountTable;
    private final BalanceTable balanceTable;
    private final ConfigTable configTable;
    private final CurrencyTable currencyTable;
    private final ExchangeTable exchangeTable;
    private final LogTable logTable;
    private final WorldGroupTable worldGroupTable;

    public MySQLEngine() {
        HikariConfig config = new HikariConfig();
        config.setMaximumPoolSize(10);
        config.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        config.addDataSourceProperty("serverName", Common.getInstance().getMainConfig().getString("System.Database.Address"));
        config.addDataSourceProperty("port", Common.getInstance().getMainConfig().getString("System.Database.Port"));
        config.addDataSourceProperty("databaseName", Common.getInstance().getMainConfig().getString("System.Database.Db"));
        config.addDataSourceProperty("user", Common.getInstance().getMainConfig().getString("System.Database.Username"));
        config.addDataSourceProperty("password", Common.getInstance().getMainConfig().getString("System.Database.Password"));
        config.addDataSourceProperty("autoDeserialize", true);
        db = new HikariDataSource(config);
        this.tablePrefix = Common.getInstance().getMainConfig().getString("System.Database.Prefix");
        accessTable = new AccessTable(tablePrefix);
        accountTable = new AccountTable(tablePrefix);
        balanceTable = new BalanceTable(tablePrefix);
        configTable = new ConfigTable(tablePrefix);
        currencyTable = new CurrencyTable(tablePrefix);
        exchangeTable = new ExchangeTable(tablePrefix);
        logTable = new LogTable(tablePrefix);
        worldGroupTable = new WorldGroupTable(tablePrefix);
    }

    @Override
    public void disable() {
        db.close();
    }

    @Override
    public Account getAccount(String name, boolean bankAccount) {
        boolean create = false;
        PreparedStatement statement = null;
        Connection connection = null;
        try  {
            boolean infiniteMoney = false, ignoreACL = false;
            connection = db.getConnection();
            statement = connection.prepareStatement(accountTable.SELECT_ENTRY_NAME);
            statement.setString(1, name);
            statement.setBoolean(2, bankAccount);
            ResultSet set = statement.executeQuery();
            if (!set.next()) {
                statement.close();
                if (bankAccount) {
                    statement = connection.prepareStatement(accountTable.INSERT_ENTRY_BANK);
                    statement.setString(1, name);
                } else {
                    statement = connection.prepareStatement(accountTable.INSERT_ENTRY);
                    statement.setString(1, name);
                    if (Common.getInstance().getServerCaller().getPlayerCaller().isOnline(name)) {
                        statement.setString(2, Common.getInstance().getServerCaller().getPlayerCaller().getUUID(name).toString());
                    } else {
                        statement.setString(2, null);
                    }
                }
                statement.executeUpdate();
                statement.close();
                create = true;
            } else {
                infiniteMoney = set.getBoolean("infiniteMoney");
                ignoreACL = set.getBoolean("ignoreACL");
            }
            statement.close();
            if (create && !bankAccount) {
                statement = connection.prepareStatement(balanceTable.INSERT_ENTRY);
                statement.setDouble(1, Common.getInstance().getDefaultHoldings());
                statement.setString(2, Account.getWorldGroupOfPlayerCurrentlyIn(name));
                statement.setString(3, name);
                statement.setString(4, Common.getInstance().getCurrencyManager().getDefaultCurrency().getName());
                statement.executeUpdate();
                statement.close();
            }
            return new Account(name, bankAccount, infiniteMoney, ignoreACL);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(statement);
            close(connection);
        }
        return null;
    }

    @Override
    public Account getAccount(UUID uuid) {
        return null;
    }

    @Override
    public void saveLog(LogInfo info, Cause cause, String causeReason, Account account, double amount, Currency currency, String worldName) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = db.getConnection();
            statement = connection.prepareStatement(logTable.INSERT_ENTRY);
            statement.setString(1, account.getAccountName());
            statement.setString(2, info.toString());
            statement.setString(3, cause.toString());
            statement.setString(4, causeReason);
            statement.setString(5, worldName);
            statement.setDouble(6, amount);
            statement.setString(7, currency.getName());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(statement);
            close(connection);
        }
    }

    @Override
    public String getConfigEntry(String name) {
        Connection connection = null;
        PreparedStatement statement = null;
        String result = null;
        try {
            connection = db.getConnection();
            statement = connection.prepareStatement(configTable.SELECT_ENTRY);
            statement.setString(1, name);
            ResultSet set = statement.executeQuery();
            if (set.next()) {
                result = set.getString("value");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(statement);
            close(connection);
        }
        return result;
    }

    @Override
    public void setConfigEntry(String name, String value) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = db.getConnection();
            statement = connection.prepareStatement(configTable.UPDATE_ENTRY);
            statement.setString(1, value);
            statement.setString(2, name);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(statement);
            close(connection);
        }
    }

    @Override
    public List<Balance> getAllBalance(Account account) {
        List<Balance> balanceList = new ArrayList<Balance>();
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = db.getConnection();
            statement = connection.prepareStatement(balanceTable.SELECT_ALL_ENTRY_ACCOUNT);
            statement.setString(1, account.getAccountName());
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                balanceList.add(new Balance(set.getString(balanceTable.WORLD_NAME_FIELD),Common.getInstance().getCurrencyManager().getCurrency(set.getString(balanceTable.CURRENCY_FIELD)), set.getDouble(balanceTable.BALANCE_FIELD)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(statement);
            close(connection);
        }
        return balanceList;
    }

    @Override
    public List<Balance> getAllWorldBalance(Account account, String worldName) {
        List<Balance> balanceList = new ArrayList<Balance>();
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = db.getConnection();
            statement = connection.prepareStatement(balanceTable.SELECT_WORLD_ENTRY_ACCOUNT);
            statement.setString(1, account.getAccountName());
            statement.setString(2, worldName);
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                balanceList.add(new Balance(set.getString(balanceTable.WORLD_NAME_FIELD),Common.getInstance().getCurrencyManager().getCurrency(set.getString(balanceTable.CURRENCY_FIELD)), set.getDouble(balanceTable.BALANCE_FIELD)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(statement);
            close(connection);
        }
        return balanceList;
    }

    @Override
    public double getBalance(Account account, Currency currency, String world) {
        double balance = 0;
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = db.getConnection();
            statement = connection.prepareStatement(balanceTable.SELECT_WORLD_ENTRY_ACCOUNT);
            statement.setString(1, account.getAccountName());
            statement.setString(2, world);
            statement.setString(3, currency.getName());
            ResultSet set = statement.executeQuery();
            if (set.next()) {
                balance = set.getDouble(balanceTable.BALANCE_FIELD);
            } else {
                balance = 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(statement);
            close(connection);
        }
        return balance;
    }

    @Override
    public double setBalance(Account account, double amount, Currency currency, String world) {
        Connection connection = null;
        PreparedStatement statement = null;
        double result = 0;
        try {
            connection = db.getConnection();
            statement = connection.prepareStatement(balanceTable.SELECT_WORLD_ENTRY_ACCOUNT);
            statement.setString(1, account.getAccountName());
            statement.setString(2, world);
            statement.setString(3, currency.getName());
            ResultSet set = statement.executeQuery();
            if (set.next()) {
                result = set.getDouble(balanceTable.BALANCE_FIELD) + amount;
                statement.close();
                statement = connection.prepareStatement(balanceTable.UPDATE_ENTRY);
                statement.setDouble(1, result);
                statement.setString(2, account.getAccountName());
                statement.setString(3, currency.getName());
                statement.setString(4, world);
                statement.executeUpdate();
                statement.close();
            } else {
                result = amount;
                statement = connection.prepareStatement(balanceTable.INSERT_ENTRY);
                statement.setDouble(1, result);
                statement.setString(2, world);
                statement.setString(3, account.getAccountName());
                statement.setString(4, currency.getName());
                statement.executeUpdate();
                statement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(statement);
            close(connection);
        }
        return result;
    }

    @Override
    public void setInfiniteMoney(Account account, boolean infinite) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = db.getConnection();
            statement = connection.prepareStatement(accountTable.UPDATE_INFINITEMONEY_ENTRY);
            statement.setBoolean(1, infinite);
            statement.setString(2, account.getAccountName());
            statement.setBoolean(3, account.isBankAccount());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(statement);
            close(connection);
        }
    }

    @Override
    public void setIgnoreACL(Account account, boolean ignoreACL) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = db.getConnection();
            statement = connection.prepareStatement(accountTable.UPDATE_IGNOREACL_ENTRY);
            statement.setBoolean(1, ignoreACL);
            statement.setString(2, account.getAccountName());
            statement.setBoolean(3, account.isBankAccount());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(statement);
            close(connection);
        }
    }

    @Override
    public Map<String, AccountACLValue> retrieveACL(Account account) {
        Map<String, AccountACLValue> result = new HashMap<>();
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = db.getConnection();
            statement = connection.prepareStatement(accessTable.SELECT_ENTRY);
            statement.setString(1, account.getAccountName());
            statement.setBoolean(2, account.isBankAccount());
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                result.put(set.getString("playerName"), new AccountACLValue(set.getBoolean("deposit"), set.getBoolean("withdraw"), set.getBoolean("acl"), set.getBoolean("balance"), set.getBoolean("owner")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(statement);
            close(connection);
        }
        return result;
    }

    @Override
    public AccountACLValue saveACL(Account account, String name, boolean deposit, boolean withdraw, boolean acl, boolean show, boolean owner) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = db.getConnection();
            statement = connection.prepareStatement(accessTable.SELECT_ENTRY);
            //TODO not finished
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(statement);
            close(connection);
        }
    }

    @Override
    public double getExchangeRate(Currency currency, Currency otherCurrency) throws NoExchangeRate{
        return 0;
    }

    @Override
    public void setExchangeRate(Currency currency, Currency otherCurrency, double amount) {

    }

    @Override
    public void saveCurrency(Currency currency) {

    }

    @Override
    public void deleteCurrency(Currency currency) {

    }

    @Override
    public void updateUsername(String name, UUID uuid) {

    }

    @Override
    public void updateUUID(String name, UUID uuid) {

    }

    @Override
    public Map<String, WorldGroup> getWorldGroups() {
        return null;
    }

    @Override
    public void removeWorldGroup(String group) {

    }

    @Override
    public String[] getBankAccountList(String sender) {
        return new String[0];
    }

    @Override
    public List<LogCommand.LogEntry> getLog(Account user, int page) {
        List<LogCommand.LogEntry> logEntryList = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = db.getConnection();
            statement = connection.prepareStatement(logTable.SELECT_ENTRY_LIMIT);
            statement.setString(1, user.getAccountName());
            statement.setInt(2, (page - 1) * 10);
            statement.setInt(3, 10);
            ResultSet set = statement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(statement);
            close(connection);
        }
        return logEntryList;
    }

    @Override
    public List<TopCommand.TopEntry> getTopEntry(int page, Currency currency, String world) {
        List<TopCommand.TopEntry> result = new ArrayList<>();
        try {
            Connection connection = db.getConnection();
            PreparedStatement statement = connection.prepareStatement(balanceTable.LIST_TOP_ACCOUNT);
            statement.setString(1, world);
            statement.setString(2, currency.getName());
            statement.setInt(3, (page - 1) * 10);
            statement.setInt(4, 10);
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                result.add(new TopCommand.TopEntry(set.getString("username"), Common.getInstance().getCurrencyManager().getCurrency(set.getString("currencyName")), set.getDouble("balance")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }


    private void close(Connection connection) {
        try {
            if (connection != null) connection.close();
        } catch (SQLException e) {
        }
    }

    private void close(PreparedStatement statement) {
        try {
            if (statement != null) statement.close();
        } catch (SQLException e) {

        }
    }
}
