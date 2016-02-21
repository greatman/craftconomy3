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
import com.greatmancode.craftconomy3.storage.sql.tables.*;
import com.greatmancode.craftconomy3.utils.BackendErrorException;
import com.greatmancode.craftconomy3.utils.NoExchangeRate;
import com.greatmancode.tools.utils.Tools;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;
import java.util.*;

public abstract class SQLStorageEngine extends StorageEngine {

    protected String tablePrefix;
    protected HikariDataSource db;
    protected AccessTable accessTable;
    protected AccountTable accountTable;
    protected BalanceTable balanceTable;
    protected ConfigTable configTable;
    protected CurrencyTable currencyTable;
    protected ExchangeTable exchangeTable;
    protected LogTable logTable;
    protected WorldGroupTable worldGroupTable;
    private Connection commitConnection;


    @Override
    public void disable() {
        db.close();
    }

    @Override
    public Account getAccount(String name, boolean bankAccount, boolean createDefault) {
        //If plugin is still in setup mode, it's better to just throw nulls around so it doesn't break the converter.
        if (Common.getInstance().getCommandManager().getCurrentLevel() == 0) {
            return null;
        }
        boolean create = false;
        int id = 0;
        PreparedStatement statement = null;
        Connection connection = null;
        try {
            boolean infiniteMoney = false, ignoreACL = false;
            connection = (commitConnection != null) ? commitConnection : db.getConnection();
            statement = connection.prepareStatement(accountTable.selectEntryName);
            statement.setString(1, name);
            statement.setBoolean(2, bankAccount);
            ResultSet set = statement.executeQuery();
            if (!set.next()) {
                statement.close();
                if (bankAccount) {
                    statement = connection.prepareStatement(accountTable.insertEntryBank, Statement.RETURN_GENERATED_KEYS);
                    statement.setString(1, name);
                } else {
                    statement = connection.prepareStatement(accountTable.insertEntry, Statement.RETURN_GENERATED_KEYS);
                    statement.setString(1, name);
                    if (Common.getInstance().getServerCaller().getPlayerCaller().isOnline(name)) {
                        statement.setString(2, Common.getInstance().getServerCaller().getPlayerCaller().getUUID(name).toString());
                    } else {
                        statement.setString(2, null);
                    }
                }
                statement.executeUpdate();
                ResultSet keys = statement.getGeneratedKeys();
                keys.first();
                id = keys.getInt(1);
                keys.close();
                statement.close();
                create = true;
            } else {
                infiniteMoney = set.getBoolean("infiniteMoney");
                ignoreACL = set.getBoolean("ignoreACL");
                id = set.getInt("id");
            }
            statement.close();
            if (create && !bankAccount && createDefault) {
                statement = connection.prepareStatement(balanceTable.insertEntry);
                statement.setDouble(1, Common.getInstance().getDefaultHoldings());
                statement.setString(2, Account.getWorldGroupOfPlayerCurrentlyIn(name));
                statement.setString(3, name);
                statement.setBoolean(4, bankAccount);
                statement.setString(5, Common.getInstance().getCurrencyManager().getDefaultCurrency().getName());
                statement.executeUpdate();
                statement.close();
            }
            return new Account(id, name, bankAccount, infiniteMoney, ignoreACL);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Tools.closeJDBCStatement(statement);
            if (commitConnection == null) {
                Tools.closeJDBCConnection(connection);
            }
        }
        return null;
    }

    @Override
    public Account getAccount(UUID uuid) {
        PreparedStatement statement = null;
        Connection connection = null;
        try {
            connection = (commitConnection != null) ? commitConnection : db.getConnection();
            statement = connection.prepareStatement(accountTable.selectEntryUuid);
            statement.setString(1, uuid.toString());
            ResultSet set = statement.executeQuery();
            if (set.next()) {
                return new Account(set.getInt("id"), set.getString("name"), set.getBoolean("bank"), set.getBoolean("infiniteMoney"), set.getBoolean("ignoreACL"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Tools.closeJDBCStatement(statement);
            if (commitConnection == null) {
                Tools.closeJDBCConnection(connection);
            }
        }
        return null;
    }

    @Override
    public List<String> getAllAccounts(boolean bank) {
        List<String> result = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = (commitConnection != null) ? commitConnection : db.getConnection();
            statement = connection.prepareStatement(accountTable.selectAllEntry);
            statement.setBoolean(1, bank);
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                result.add(set.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Tools.closeJDBCStatement(statement);
            if (commitConnection == null) {
                Tools.closeJDBCConnection(connection);
            }
        }
        return result;
    }

    @Override
    public void saveLog(LogInfo info, Cause cause, String causeReason, Account account, double amount, Currency currency, String worldName) {
        saveLog(info, cause, causeReason, account, amount, currency, worldName, new Timestamp(System.currentTimeMillis()));
    }

    @Override
    public void saveLog(LogInfo info, Cause cause, String causeReason, Account account, double amount, Currency currency, String worldName, Timestamp timestamp) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = (commitConnection != null) ? commitConnection : db.getConnection();
            statement = connection.prepareStatement(logTable.insertEntry);
            statement.setString(1, account.getAccountName());
            statement.setBoolean(2, account.isBankAccount());
            statement.setString(3, info.toString());
            statement.setString(4, cause.toString());
            statement.setString(5, causeReason);
            statement.setString(6, worldName);
            statement.setDouble(7, amount);
            statement.setString(8, currency.getName());
            statement.setTimestamp(9, timestamp);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Tools.closeJDBCStatement(statement);
            if (commitConnection == null) {
                Tools.closeJDBCConnection(connection);
            }
        }
    }

    @Override
    public String getConfigEntry(String name) {
        Connection connection = null;
        PreparedStatement statement = null;
        String result = null;
        try {
            connection = (commitConnection != null) ? commitConnection : db.getConnection();
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
            if (commitConnection == null) {
                Tools.closeJDBCConnection(connection);
            }
        }
        return result;
    }

    @Override
    public void setConfigEntry(String name, String value) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = (commitConnection != null) ? commitConnection : db.getConnection();
            if (getConfigEntry(name) != null) {
                statement = connection.prepareStatement(configTable.updateEntry);
                statement.setString(1, value);
                statement.setString(2, name);
                statement.executeUpdate();
            } else {
                statement = connection.prepareStatement(configTable.insertEntry);
                statement.setString(1, name);
                statement.setString(2, value);
                statement.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Tools.closeJDBCStatement(statement);
            if (commitConnection == null) {
                Tools.closeJDBCConnection(connection);
            }
        }
    }

    @Override
    public List<Balance> getAllBalance(Account account) {
        List<Balance> balanceList = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = (commitConnection != null) ? commitConnection : db.getConnection();
            statement = connection.prepareStatement(balanceTable.selectAllEntryAccount);
            statement.setString(1, account.getAccountName());
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                balanceList.add(new Balance(set.getString(balanceTable.WORLD_NAME_FIELD), Common.getInstance().getCurrencyManager().getCurrency(set.getString(balanceTable.CURRENCY_FIELD)), set.getDouble(balanceTable.BALANCE_FIELD)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Tools.closeJDBCStatement(statement);
            if (commitConnection == null) {
                Tools.closeJDBCConnection(connection);
            }
        }
        return balanceList;
    }

    @Override
    public List<Balance> getAllWorldBalance(Account account, String worldName) {
        List<Balance> balanceList = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = (commitConnection != null) ? commitConnection : db.getConnection();
            statement = connection.prepareStatement(balanceTable.selectWorldEntryAccount);
            statement.setString(1, account.getAccountName());
            statement.setString(2, worldName);
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                balanceList.add(new Balance(set.getString(balanceTable.WORLD_NAME_FIELD), Common.getInstance().getCurrencyManager().getCurrency(set.getString(balanceTable.CURRENCY_FIELD)), set.getDouble(balanceTable.BALANCE_FIELD)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Tools.closeJDBCStatement(statement);
            if (commitConnection == null) {
                Tools.closeJDBCConnection(connection);
            }
        }
        return balanceList;
    }

    @Override
    public double getBalance(Account account, Currency currency, String world) {
        double balance = 0;
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = (commitConnection != null) ? commitConnection : db.getConnection();
            statement = connection.prepareStatement(balanceTable.selectWorldCurrencyEntryAccount);
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
            Tools.closeJDBCStatement(statement);
            if (commitConnection == null) {
                Tools.closeJDBCConnection(connection);
            }
        }
        return balance;
    }

    @Override
    public double setBalance(Account account, double amount, Currency currency, String world) {
        Connection connection = null;
        PreparedStatement statement = null;
        double result = 0;
        try {
            connection = (commitConnection != null) ? commitConnection : db.getConnection();
            statement = connection.prepareStatement(balanceTable.selectWorldCurrencyEntryAccount);
            statement.setString(1, account.getAccountName());
            statement.setString(2, world);
            statement.setString(3, currency.getName());
            ResultSet set = statement.executeQuery();
            if (set.next()) {
                result = amount;
                statement.close();
                statement = connection.prepareStatement(balanceTable.updateEntry);
                statement.setDouble(1, result);
                statement.setInt(2, account.getId());
                statement.setString(3, currency.getName());
                statement.setString(4, world);
                statement.executeUpdate();
                statement.close();
            } else {
                result = amount;
                statement.close();
                statement = connection.prepareStatement(balanceTable.insertEntry);
                statement.setDouble(1, result);
                statement.setString(2, world);
                statement.setString(3, account.getAccountName());
                statement.setBoolean(4, account.isBankAccount());
                statement.setString(5, currency.getName());
                statement.executeUpdate();
                statement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new BackendErrorException(e.getMessage());
        } finally {
            Tools.closeJDBCStatement(statement);
            if (commitConnection == null) {
                Tools.closeJDBCConnection(connection);
            }
        }
        return result;
    }

    @Override
    public void setInfiniteMoney(Account account, boolean infinite) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = (commitConnection != null) ? commitConnection : db.getConnection();
            statement = connection.prepareStatement(accountTable.updateInfinitemoneyEntry);
            statement.setBoolean(1, infinite);
            statement.setString(2, account.getAccountName());
            statement.setBoolean(3, account.isBankAccount());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Tools.closeJDBCStatement(statement);
            if (commitConnection == null) {
                Tools.closeJDBCConnection(connection);
            }
        }
    }

    @Override
    public void setIgnoreACL(Account account, boolean ignoreACL) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = (commitConnection != null) ? commitConnection : db.getConnection();
            statement = connection.prepareStatement(accountTable.updateIgnoreaclEntry);
            statement.setBoolean(1, ignoreACL);
            statement.setString(2, account.getAccountName());
            statement.setBoolean(3, account.isBankAccount());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Tools.closeJDBCStatement(statement);
            if (commitConnection == null) {
                Tools.closeJDBCConnection(connection);
            }
        }
    }

    @Override
    public Map<String, AccountACLValue> retrieveACL(Account account) {
        Map<String, AccountACLValue> result = new HashMap<>();
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = (commitConnection != null) ? commitConnection : db.getConnection();
            statement = connection.prepareStatement(accessTable.selectEntry);
            statement.setString(1, account.getAccountName());
            statement.setBoolean(2, account.isBankAccount());
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                result.put(set.getString("playerName"), new AccountACLValue(set.getBoolean("deposit"), set.getBoolean("withdraw"), set.getBoolean("acl"), set.getBoolean("balance"), set.getBoolean("owner")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Tools.closeJDBCStatement(statement);
            if (commitConnection == null) {
                Tools.closeJDBCConnection(connection);
            }
        }
        return result;
    }

    @Override
    public AccountACLValue saveACL(Account account, String name, boolean deposit, boolean withdraw, boolean acl, boolean balance, boolean owner) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = (commitConnection != null) ? commitConnection : db.getConnection();
            statement = connection.prepareStatement(accessTable.selectEntryUnique);
            statement.setString(1, account.getAccountName());
            statement.setBoolean(2, account.isBankAccount());
            statement.setString(3, name);
            ResultSet set = statement.executeQuery();
            if (set.next()) {
                statement.close();
                statement = connection.prepareStatement(accessTable.updateEntry);
                statement.setBoolean(1, owner);
                statement.setBoolean(2, balance);
                statement.setBoolean(3, deposit);
                statement.setBoolean(4, acl);
                statement.setBoolean(5, withdraw);
                statement.setString(6, account.getAccountName());
                statement.setBoolean(7, account.isBankAccount());
                statement.setString(8, name);
            } else {
                statement.close();
                statement = connection.prepareStatement(accessTable.insertEntry);
                statement.setString(1, account.getAccountName());
                statement.setBoolean(2, account.isBankAccount());
                statement.setString(3, name);
                statement.setBoolean(4, owner);
                statement.setBoolean(5, balance);
                statement.setBoolean(6, deposit);
                statement.setBoolean(7, acl);
                statement.setBoolean(8, withdraw);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Tools.closeJDBCStatement(statement);
            if (commitConnection == null) {
                Tools.closeJDBCConnection(connection);
            }
        }
        return new AccountACLValue(deposit, withdraw, acl, balance, owner);
    }

    @Override
    public double getExchangeRate(Currency currency, Currency otherCurrency) throws NoExchangeRate {
        double result = 0.0;
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = (commitConnection != null) ? commitConnection : db.getConnection();
            statement = connection.prepareStatement(exchangeTable.selectEntry);
            statement.setString(1, currency.getName());
            statement.setString(2, otherCurrency.getName());
            ResultSet set = statement.executeQuery();
            if (set.next()) {
                result = set.getDouble("amount");
            } else {
                throw new NoExchangeRate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Tools.closeJDBCStatement(statement);
            if (commitConnection == null) {
                Tools.closeJDBCConnection(connection);
            }
        }
        return result;
    }

    @Override
    public void setExchangeRate(Currency currency, Currency otherCurrency, double amount) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = (commitConnection != null) ? commitConnection : db.getConnection();
            statement = connection.prepareStatement(exchangeTable.selectEntry);
            statement.setString(1, currency.getName());
            statement.setString(2, otherCurrency.getName());
            ResultSet set = statement.executeQuery();
            if (set.next()) {
                statement.close();
                statement = connection.prepareStatement(exchangeTable.updateEntry);
                statement.setString(1, currency.getName());
                statement.setString(2, otherCurrency.getName());
                statement.setDouble(3, amount);
                statement.executeUpdate();
            } else {
                statement.close();
                statement = connection.prepareStatement(exchangeTable.insertEntry);
                statement.setString(1, currency.getName());
                statement.setString(2, otherCurrency.getName());
                statement.setDouble(3, amount);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Tools.closeJDBCStatement(statement);
            if (commitConnection == null) {
                Tools.closeJDBCConnection(connection);
            }
        }
    }

    @Override
    public void saveCurrency(String oldName, Currency currency) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = (commitConnection != null) ? commitConnection : db.getConnection();
            statement = connection.prepareStatement(currencyTable.selectEntry);
            statement.setString(1, currency.getName());
            ResultSet set = statement.executeQuery();
            if (set.next()) {
                statement.close();
                statement = connection.prepareStatement(currencyTable.updateEntry);
                statement.setString(1, currency.getName());
                statement.setString(2, currency.getPlural());
                statement.setString(3, currency.getMinor());
                statement.setString(4, currency.getMinorPlural());
                statement.setString(5, currency.getSign());
                statement.setBoolean(6, currency.getStatus());
                statement.setBoolean(7, currency.isPrimaryBankCurrency());
                statement.setString(8, oldName);
                statement.executeUpdate();
            } else {
                statement.close();
                statement = connection.prepareStatement(currencyTable.insertEntry);
                statement.setString(1, currency.getName());
                statement.setString(2, currency.getPlural());
                statement.setString(3, currency.getMinor());
                statement.setString(4, currency.getMinorPlural());
                statement.setString(5, currency.getSign());
                statement.setBoolean(6, currency.getStatus());
                statement.setBoolean(7, currency.isPrimaryBankCurrency());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Tools.closeJDBCStatement(statement);
            if (commitConnection == null) {
                Tools.closeJDBCConnection(connection);
            }
        }
    }

    @Override
    public void deleteCurrency(Currency currency) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = (commitConnection != null) ? commitConnection : db.getConnection();
            statement = connection.prepareStatement(currencyTable.deleteEntry);
            statement.setString(1, currency.getName());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Tools.closeJDBCStatement(statement);
            if (commitConnection == null) {
                Tools.closeJDBCConnection(connection);
            }
        }
    }

    @Override
    public void updateUsername(String name, UUID uuid) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = (commitConnection != null) ? commitConnection : db.getConnection();
            statement = connection.prepareStatement(accountTable.updateNameByUuid);
            statement.setString(1, name);
            statement.setString(2, uuid.toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Tools.closeJDBCStatement(statement);
            if (commitConnection == null) {
                Tools.closeJDBCConnection(connection);
            }
        }
    }

    @Override
    public void updateUUID(String name, UUID uuid) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = (commitConnection != null) ? commitConnection : db.getConnection();
            statement = connection.prepareStatement(accountTable.updateUuidByName);
            statement.setString(1, uuid.toString());
            statement.setString(2, name);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Tools.closeJDBCStatement(statement);
            if (commitConnection == null) {
                Tools.closeJDBCConnection(connection);
            }
        }
    }

    @Override
    public Map<String, WorldGroup> getWorldGroups() {
        Map<String, WorldGroup> result = new HashMap<>();
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = (commitConnection != null) ? commitConnection : db.getConnection();
            statement = connection.prepareStatement(worldGroupTable.selectAllEntry);
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                result.put(set.getString("groupName"), new WorldGroup(set.getString("groupName")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Tools.closeJDBCStatement(statement);
            if (commitConnection == null) {
                Tools.closeJDBCConnection(connection);
            }
        }
        return result;
    }

    @Override
    public void removeWorldGroup(String group) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = (commitConnection != null) ? commitConnection : db.getConnection();
            statement = connection.prepareStatement(worldGroupTable.deleteEntry);
            statement.setString(1, group);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Tools.closeJDBCStatement(statement);
            if (commitConnection == null) {
                Tools.closeJDBCConnection(connection);
            }
        }
    }

    @Override
    public String[] getBankAccountList(String sender) {
        List<String> results = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = (commitConnection != null) ? commitConnection : db.getConnection();
            statement = connection.prepareStatement(accessTable.getAccountList);
            statement.setString(1, sender);
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                results.add(set.getString("name"));
            }
        } catch (SQLException e) {

        } finally {
            Tools.closeJDBCStatement(statement);
            if (commitConnection == null) {
                Tools.closeJDBCConnection(connection);
            }
        }
        return results.toArray(new String[results.size()]);
    }

    @Override
    public List<LogCommand.LogEntry> getLog(Account user, int page) {
        List<LogCommand.LogEntry> logEntryList = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = (commitConnection != null) ? commitConnection : db.getConnection();
            statement = connection.prepareStatement(logTable.selectEntryLimit);
            statement.setString(1, user.getAccountName());
            statement.setInt(2, (page - 1) * 10);
            statement.setInt(3, 10);
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                logEntryList.add(new LogCommand.LogEntry(set.getTimestamp("timestamp"), set.getString("type"), set.getString("worldName"), set.getString("cause"), set.getString("causeReason"), Common.getInstance().getCurrencyManager().getCurrency(set.getString("currency_id")), set.getDouble("amount")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Tools.closeJDBCStatement(statement);
            if (commitConnection == null) {
                Tools.closeJDBCConnection(connection);
            }
        }
        return logEntryList;
    }

    @Override
    public List<TopCommand.TopEntry> getTopEntry(int page, Currency currency, String world) {
        List<TopCommand.TopEntry> result = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = (commitConnection != null) ? commitConnection : db.getConnection();
            statement = connection.prepareStatement(balanceTable.listTopAccount);
            statement.setString(1, world);
            statement.setString(2, currency.getName());
            statement.setInt(3, (page - 1) * 10);
            statement.setInt(4, 10);
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                result.add(new TopCommand.TopEntry(set.getString("name"), Common.getInstance().getCurrencyManager().getCurrency(set.getString("currencyName")), set.getDouble("balance")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Tools.closeJDBCStatement(statement);
            if (commitConnection == null) {
                Tools.closeJDBCConnection(connection);
            }
        }
        return result;
    }

    @Override
    public List<CurrencyRatesCommand.CurrencyRateEntry> getCurrencyExchanges() {
        List<CurrencyRatesCommand.CurrencyRateEntry> results = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = (commitConnection != null) ? commitConnection : db.getConnection();
            statement = connection.prepareStatement(exchangeTable.selectAll);
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                results.add(new CurrencyRatesCommand.CurrencyRateEntry(Common.getInstance().getCurrencyManager().getCurrency(set.getString("from_currency")), Common.getInstance().getCurrencyManager().getCurrency(set.getString("to_currency")), set.getDouble("amount")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Tools.closeJDBCStatement(statement);
            if (commitConnection == null) {
                Tools.closeJDBCConnection(connection);
            }
        }
        return results;
    }

    @Override
    public void cleanLog(Timestamp timestamp) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = (commitConnection != null) ? commitConnection : db.getConnection();
            statement = connection.prepareStatement(logTable.cleanEntry);
            statement.setTimestamp(1, timestamp);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Tools.closeJDBCStatement(statement);
            if (commitConnection == null) {
                Tools.closeJDBCConnection(connection);
            }
        }
    }

    @Override
    public boolean deleteAccount(String name, boolean bankAccount) {
        boolean result = false;
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = (commitConnection != null) ? commitConnection : db.getConnection();
            statement = connection.prepareStatement(accountTable.deleteEntry);
            statement.setString(1, name);
            statement.setBoolean(2, bankAccount);
            if (statement.executeUpdate() == 1) {
                result = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Tools.closeJDBCStatement(statement);
            if (commitConnection == null) {
                Tools.closeJDBCConnection(connection);
            }
        }
        return result;
    }

    @Override
    public boolean accountExist(String name, boolean bankAccount) {
        boolean result = false;
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = (commitConnection != null) ? commitConnection : db.getConnection();
            statement = connection.prepareStatement(accountTable.selectEntryName);
            statement.setString(1, name);
            statement.setBoolean(2, bankAccount);
            ResultSet set = statement.executeQuery();
            if (set.next()) {
                result = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Tools.closeJDBCStatement(statement);
            if (commitConnection == null) {
                Tools.closeJDBCConnection(connection);
            }
        }
        return result;
    }

    @Override
    public void saveWorldGroup(String name, String worldList) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = (commitConnection != null) ? commitConnection : db.getConnection();
            statement = connection.prepareStatement(worldGroupTable.selectEntry);
            statement.setString(1, name);
            ResultSet set = statement.executeQuery();
            if (set.next()) {
                statement.close();
                statement = connection.prepareStatement(worldGroupTable.updateEntry);
                statement.setString(1, worldList);
                statement.setString(2, name);
                statement.executeUpdate();
            } else {
                statement.close();
                statement = connection.prepareStatement(worldGroupTable.insertEntryWithWorldlist);
                statement.setString(1, name);
                statement.setString(2, worldList);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Tools.closeJDBCStatement(statement);
            if (commitConnection == null) {
                Tools.closeJDBCConnection(connection);
            }
        }
    }

    @Override
    public List<String> getAllCurrencyNames() {
        List<String> results = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = (commitConnection != null) ? commitConnection : db.getConnection();
            statement = connection.prepareStatement(currencyTable.selectAllEntry);
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                results.add(set.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Tools.closeJDBCStatement(statement);
            if (commitConnection == null) {
                Tools.closeJDBCConnection(connection);
            }
        }
        return results;
    }

    @Override
    public void setDefaultCurrency(Currency currency) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = (commitConnection != null) ? commitConnection : db.getConnection();
            statement = connection.prepareStatement(currencyTable.setAsDefault1);
            statement.executeUpdate();
            statement.close();
            statement = connection.prepareStatement(currencyTable.setAsDefault2);
            statement.setString(1, currency.getName());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Tools.closeJDBCStatement(statement);
            if (commitConnection == null) {
                Tools.closeJDBCConnection(connection);
            }
        }
    }

    @Override
    public void setDefaultBankCurrency(Currency currency) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = (commitConnection != null) ? commitConnection : db.getConnection();
            statement = connection.prepareStatement(currencyTable.setAsDefaultBank1);
            statement.executeUpdate();
            statement.close();
            statement = connection.prepareStatement(currencyTable.setAsDefaultBank2);
            statement.setString(1, currency.getName());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Tools.closeJDBCStatement(statement);
            if (commitConnection == null) {
                Tools.closeJDBCConnection(connection);
            }
        }
    }

    @Override
    public Currency getCurrency(String name) {
        Currency result = null;
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = (commitConnection != null) ? commitConnection : db.getConnection();
            statement = connection.prepareStatement(currencyTable.selectEntry);
            statement.setString(1, name);
            ResultSet set = statement.executeQuery();
            if (set.next()) {
                result = new Currency(set.getString("name"), set.getString("plural"), set.getString("minor"), set.getString("minorPlural"), set.getString("sign"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Tools.closeJDBCStatement(statement);
            if (commitConnection == null) {
                Tools.closeJDBCConnection(connection);
            }
        }
        return result;
    }

    @Override
    public Map<String, Currency> getAllCurrencies() {
        Map<String, Currency> results = new HashMap<>();
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = (commitConnection != null) ? commitConnection : db.getConnection();
            statement = connection.prepareStatement(currencyTable.selectAllEntry);
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                results.put(set.getString("name"), new Currency(set.getString("name"), set.getString("plural"), set.getString("minor"), set.getString("minorPlural"), set.getString("sign"), set.getBoolean("status"), set.getBoolean("bankCurrency")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Tools.closeJDBCStatement(statement);
            if (commitConnection == null) {
                Tools.closeJDBCConnection(connection);
            }
        }
        return results;
    }

    @Override
    public String retrieveWorldGroupWorlds(String name) {
        String result = "";
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = (commitConnection != null) ? commitConnection : db.getConnection();
            statement = connection.prepareStatement(worldGroupTable.selectEntry);
            statement.setString(1, name);
            ResultSet set = statement.executeQuery();
            if (set.next()) {
                result = set.getString("worldList");
            } else {
                statement.close();
                statement = connection.prepareStatement(worldGroupTable.insertEntry);
                statement.setString(1, name);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Tools.closeJDBCStatement(statement);
            if (commitConnection == null) {
                Tools.closeJDBCConnection(connection);
            }
        }
        return result;
    }

    @Override
    public void saveImporterUsers(List<Converter.User> userList) {
        StringBuilder builder;
        builder = new StringBuilder("INSERT INTO " + tablePrefix + AccountTable.TABLE_NAME + "(name,uuid) VALUES(");
        StringBuilder balanceBuilder = new StringBuilder("INSERT INTO " + tablePrefix + BalanceTable.TABLE_NAME + "(balance, worldName, currency_id, username_id) VALUES(");
        boolean first = true;
        for (Converter.User userEntry : userList) {
            if (!first) {
                builder.append(",(");
                balanceBuilder.append(",(");
            } else {
                first = false;
            }
            if (userEntry.getUuid() == null) {
                builder.append("'" + userEntry.getUser() + "',null)");
            } else {
                builder.append("'" + userEntry.getUser() + "','" + userEntry.getUuid() + "')");
            }
            if (userEntry.getUuid() != null) {
                balanceBuilder.append(userEntry.getBalance() + ",'default','" + Common.getInstance().getCurrencyManager().getDefaultCurrency().getName() + "',(SELECT id from " + tablePrefix + AccountTable.TABLE_NAME + " WHERE uuid='" + userEntry.getUuid() + "'))");
            } else {
                balanceBuilder.append(userEntry.getBalance() + ",'default','" + Common.getInstance().getCurrencyManager().getDefaultCurrency().getName() + "',(SELECT id from " + tablePrefix + AccountTable.TABLE_NAME + " WHERE name='" + userEntry.getUser() + "'))");
            }
        }
        builder.append(";");
        balanceBuilder.append(";");
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = (commitConnection != null) ? commitConnection : db.getConnection();
            statement = connection.prepareStatement(builder.toString());
            statement.executeUpdate();
            statement.close();
            statement = connection.prepareStatement(balanceBuilder.toString());
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Tools.closeJDBCStatement(statement);
            if (commitConnection == null) {
                Tools.closeJDBCConnection(connection);
            }
        }


    }

    @Override
    public void disableAutoCommit() {
        try {
            commitConnection = db.getConnection();
            commitConnection.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void enableAutoCommit() {
        try {
            commitConnection.close();
            commitConnection = null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void commit() {
        if (commitConnection != null) {
            try {
                commitConnection.commit();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
