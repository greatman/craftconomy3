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
package com.greatmancode.craftconomy3.storage.sql;

import com.greatmancode.craftconomy3.Cause;
import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.LogInfo;
import com.greatmancode.craftconomy3.account.Account;
import com.greatmancode.craftconomy3.account.AccountACLValue;
import com.greatmancode.craftconomy3.account.Balance;
import com.greatmancode.craftconomy3.commands.currency.CurrencyRatesCommand;
import com.greatmancode.craftconomy3.commands.money.LogCommand;
import com.greatmancode.craftconomy3.commands.money.TopCommand;
import com.greatmancode.craftconomy3.converter.Converter;
import com.greatmancode.craftconomy3.currency.Currency;
import com.greatmancode.craftconomy3.groups.WorldGroup;
import com.greatmancode.craftconomy3.storage.StorageEngine;
import com.greatmancode.craftconomy3.storage.sql.tables.ConfigTable;
import com.greatmancode.craftconomy3.utils.NoExchangeRate;
import com.greatmancode.tools.utils.Tools;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.File;
import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SQLiteEngine extends StorageEngine {

    private final HikariDataSource db;
    private final String tablePrefix;
    private ConfigTable configTable = null;
    public SQLiteEngine() {
        this.tablePrefix = Common.getInstance().getMainConfig().getString("System.Database.Prefix");
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("org.sqlite.JDBC");
        config.setJdbcUrl("jdbc:sqlite:"+ Common.getInstance().getServerCaller().getDataFolder() + File.separator +  "database.db");
        config.setConnectionTestQuery("SELECT 1");
        db = new HikariDataSource(config);
        configTable = new ConfigTable(tablePrefix);
    }
    @Override
    public void disable() {

    }

    @Override
    public Account getAccount(String name, boolean isBank, boolean createDefault) {
        return null;
    }

    @Override
    public Account getAccount(UUID uuid) {
        return null;
    }

    @Override
    public List<String> getAllAccounts(boolean bank) {
        return null;
    }

    @Override
    public void saveLog(LogInfo info, Cause cause, String causeReason, Account account, double amount, Currency currency, String worldName) {

    }

    @Override
    public void saveLog(LogInfo info, Cause cause, String causeReason, Account account, double amount, Currency currency, String worldName, Timestamp timestamp) {

    }

    @Override
    public String getConfigEntry(String name) {
        Connection connection = null;
        PreparedStatement statement = null;
        String result = null;
        try {
            connection = db.getConnection();
            statement = connection.prepareStatement(configTable.selectEntry);
            statement.setString(1, name);
            ResultSet set = statement.executeQuery();
            if (set.next()) {
                result = set.getString("value");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Tools.closeJDBCStatement(statement);
            Tools.closeJDBCConnection(connection);
        }
        return result;
    }

    @Override
    public void setConfigEntry(String name, String value) {

    }

    @Override
    public List<Balance> getAllBalance(Account account) {
        return null;
    }

    @Override
    public List<Balance> getAllWorldBalance(Account account, String world) {
        return null;
    }

    @Override
    public double getBalance(Account account, Currency currency, String world) {
        return 0;
    }

    @Override
    public double setBalance(Account account, double amount, Currency currency, String world) {
        return 0;
    }

    @Override
    public void setInfiniteMoney(Account account, boolean infinite) {

    }

    @Override
    public void setIgnoreACL(Account account, boolean ignoreACL) {

    }

    @Override
    public Map<String, AccountACLValue> retrieveACL(Account account) {
        return null;
    }

    @Override
    public AccountACLValue saveACL(Account account, String name, boolean deposit, boolean withdraw, boolean acl, boolean show, boolean owner) {
        return null;
    }

    @Override
    public double getExchangeRate(Currency currency, Currency otherCurrency) throws NoExchangeRate {
        return 0;
    }

    @Override
    public void setExchangeRate(Currency currency, Currency otherCurrency, double amount) {

    }

    @Override
    public void saveCurrency(String oldName, Currency currency) {

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
    public String[] getBankAccountList(String playerName) {
        return new String[0];
    }

    @Override
    public List<LogCommand.LogEntry> getLog(Account user, int page) {
        return null;
    }

    @Override
    public List<TopCommand.TopEntry> getTopEntry(int page, Currency currency, String world) {
        return null;
    }

    @Override
    public List<CurrencyRatesCommand.CurrencyRateEntry> getCurrencyExchanges() {
        return null;
    }

    @Override
    public void cleanLog(Timestamp timestamp) {

    }

    @Override
    public boolean deleteAccount(String name, boolean bankAccount) {
        return false;
    }

    @Override
    public boolean accountExist(String name, boolean bankAccount) {
        return false;
    }

    @Override
    public void saveWorldGroup(String name, String worldList) {

    }

    @Override
    public List<String> getAllCurrencyNames() {
        return null;
    }

    @Override
    public void setDefaultCurrency(Currency currency) {

    }

    @Override
    public void setDefaultBankCurrency(Currency currency) {

    }

    @Override
    public Currency getCurrency(String name) {
        return null;
    }

    @Override
    public Map<String, Currency> getAllCurrencies() {
        return null;
    }

    @Override
    public String retrieveWorldGroupWorlds(String name) {
        return null;
    }

    @Override
    public void saveImporterUsers(List<Converter.User> userList) {

    }

    @Override
    public void disableAutoCommit() {

    }

    @Override
    public void enableAutoCommit() {

    }

    @Override
    public void commit() {

    }
}
