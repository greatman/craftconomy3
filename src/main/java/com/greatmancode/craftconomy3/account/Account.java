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
package com.greatmancode.craftconomy3.account;

import com.greatmancode.craftconomy3.Cause;
import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.LogInfo;
import com.greatmancode.craftconomy3.currency.Currency;
import com.greatmancode.craftconomy3.database.tables.AccountTable;
import com.greatmancode.craftconomy3.database.tables.BalanceTable;
import com.greatmancode.tools.events.event.EconomyChangeEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a economy account.
 *
 * @author greatman
 */
public class Account {
    private AccountACL acl;
    private boolean bankAccount, infiniteMoney, ignoreACL;
    private String name;
    /**
     * Load a account. Creates one if it doesn't exist.
     *
     * @param name The account name
     */
    public Account(String name, boolean bankAccount) {
        try {
            this.name = name;
            this.bankAccount = bankAccount;
            boolean create = false;
            Connection connection = Common.getInstance().getDatabaseManager().getDatabase().getConnection();
            PreparedStatement statement = connection.prepareStatement(AccountTable.SELECT_ENTRY_NAME);
            statement.setString(1, name);
            statement.setBoolean(2, bankAccount);
            ResultSet set = statement.executeQuery();
            if (!set.next()) {
                statement.close();
                if (bankAccount) {
                    statement = connection.prepareStatement(AccountTable.INSERT_ENTRY_BANK);
                    statement.setString(1, name);
                } else {
                    statement = connection.prepareStatement(AccountTable.INSERT_ENTRY);
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
            if (create && !isBankAccount()) {
                statement = connection.prepareStatement(BalanceTable.INSERT_ENTRY);
                statement.setDouble(1, Common.getInstance().getDefaultHoldings());
                statement.setString(2, getWorldGroupOfPlayerCurrentlyIn());
                statement.setString(3, name);
                statement.setString(4, Common.getInstance().getCurrencyManager().getDefaultCurrency().getName());
                statement.executeUpdate();
                statement.close();
            }
            connection.close();
            if (isBankAccount()) {
                acl = new AccountACL(this);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the account name.
     *
     * @return The account name
     */
    public String getAccountName() {
        return name;
    }

    /**
     * Checks if this account is a bank account
     *
     * @return True if this account is a bank account, else false
     */
    public boolean isBankAccount() {
        return bankAccount;
    }

    /**
     * Get the account ACL. Only used with a bank account
     *
     * @return The account ACL if it's a bank account, else null
     */
    public AccountACL getAccountACL() {
        AccountACL accountAcl = null;
        if (isBankAccount()) {
            accountAcl = acl;
        }
        return accountAcl;
    }

    /**
     * Get the whole account balance
     *
     * @return A list of all account balance
     */
    public List<Balance> getAllBalance() {
        List<Balance> balanceList = new ArrayList<Balance>();
        try {
            Connection connection = Common.getInstance().getDatabaseManager().getDatabase().getConnection();
            PreparedStatement statement = connection.prepareStatement(BalanceTable.SELECT_ALL_ENTRY_ACCOUNT);
            statement.setString(1, name);
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                balanceList.add(new Balance(set.getString(BalanceTable.WORLD_NAME_FIELD),Common.getInstance().getCurrencyManager().getCurrency(set.getString(BalanceTable.CURRENCY_FIELD)), set.getDouble(BalanceTable.BALANCE_FIELD)));
            }
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return balanceList;
    }

    /**
     * Get the whole account balance in a certain world / world group
     *
     * @param world The world / world group to search in
     * @return A list of Balance
     */
    public List<Balance> getAllWorldBalance(String world) {
        if (!Common.getInstance().getWorldGroupManager().worldGroupExist(world)) {
            world = Common.getInstance().getWorldGroupManager().getWorldGroupName(world);
        }
        List<Balance> balanceList = new ArrayList<Balance>();
        try {
            Connection connection = Common.getInstance().getDatabaseManager().getDatabase().getConnection();
            PreparedStatement statement = connection.prepareStatement(BalanceTable.SELECT_WORLD_ENTRY_ACCOUNT);
            statement.setString(1, name);
            statement.setString(2, world);
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                balanceList.add(new Balance(set.getString(BalanceTable.WORLD_NAME_FIELD),Common.getInstance().getCurrencyManager().getCurrency(set.getString(BalanceTable.CURRENCY_FIELD)), set.getDouble(BalanceTable.BALANCE_FIELD)));
            }
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return balanceList;
    }

    /**
     * Get's the player balance. Sends double.MIN_NORMAL in case of a error
     *
     * @param world        The world / world group to search in
     * @param currencyName The currency Name
     * @return The balance. If the account has infinite money. Double.MAX_VALUE is returned.
     */
    public double getBalance(String world, String currencyName) {
        double balance = Double.MIN_NORMAL;
        if (!Common.getInstance().getWorldGroupManager().worldGroupExist(world)) {
            world = Common.getInstance().getWorldGroupManager().getWorldGroupName(world);
        }
        Currency currency = Common.getInstance().getCurrencyManager().getCurrency(currencyName);
        if (currency != null) {
            if (!hasInfiniteMoney()) {
                try {
                    Connection connection = Common.getInstance().getDatabaseManager().getDatabase().getConnection();
                    PreparedStatement statement = connection.prepareStatement(BalanceTable.SELECT_WORLD_ENTRY_ACCOUNT);
                    statement.setString(1, name);
                    statement.setString(2, world);
                    statement.setString(3, currency.getName());
                    ResultSet set = statement.executeQuery();
                    if (set.next()) {
                        balance = set.getDouble(BalanceTable.BALANCE_FIELD);
                    } else {
                        balance = 0;
                    }
                    statement.close();
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                balance = Double.MAX_VALUE;
            }
        }
        return format(balance);
    }

    /**
     * Adds a certain amount of money in the account
     *
     * @param amount       The amount of money to add
     * @param world        The World / World group we want to add money in
     * @param currencyName The currency we want to add money in
     * @return The new balance. If the account has infinite money. Double.MAX_VALUE is returned.
     * @deprecated use {@link #deposit(double, String, String, com.greatmancode.craftconomy3.Cause, String)}
     */
    @Deprecated
    public double deposit(double amount, String world, String currencyName) {
        return deposit(amount, world, currencyName, Cause.UNKNOWN, null);
    }

    /**
     * Adds a certain amount of money in the account
     *
     * @param amount       The amount of money to add
     * @param world        The World / World group we want to add money in
     * @param currencyName The currency we want to add money in
     * @param cause        The cause of the change.
     * @param causeReason  The reason of the cause
     * @return The new balance. If the account has infinite money. Double.MAX_VALUE is returned.
     */
    public double deposit(double amount, String world, String currencyName, Cause cause, String causeReason) {
        BalanceTable balanceTable;
        double result = 0;
        amount = format(amount);
        if (!Common.getInstance().getWorldGroupManager().worldGroupExist(world)) {
            world = Common.getInstance().getWorldGroupManager().getWorldGroupName(world);
        }
        Currency currency = Common.getInstance().getCurrencyManager().getCurrency(currencyName);
        if (currency != null) {
            if (!hasInfiniteMoney()) {
                try {
                    Connection connection = Common.getInstance().getDatabaseManager().getDatabase().getConnection();
                    PreparedStatement statement = connection.prepareStatement(BalanceTable.SELECT_WORLD_ENTRY_ACCOUNT);
                    statement.setString(1, name);
                    statement.setString(2, world);
                    statement.setString(3, currency.getName());
                    ResultSet set = statement.executeQuery();
                    if (set.next()) {
                        result = set.getDouble(BalanceTable.BALANCE_FIELD) + amount;
                        statement.close();
                        statement = connection.prepareStatement(BalanceTable.UPDATE_ENTRY);
                        statement.setDouble(1, result);
                        statement.setString(2, name);
                        statement.setString(3, currency.getName());
                        statement.setString(4, world);
                        statement.executeUpdate();
                        statement.close();
                    } else {
                        result = amount;
                        statement = connection.prepareStatement(BalanceTable.INSERT_ENTRY);
                        statement.setDouble(1, result);
                        statement.setString(2, world);
                        statement.setString(3, name);
                        statement.setString(4, currency.getName());
                        statement.executeUpdate();
                        statement.close();
                    }
                    statement.close();
                    connection.close();
                    Common.getInstance().writeLog(LogInfo.DEPOSIT, cause, causeReason, this, amount, currency, world);
                    Common.getInstance().getServerCaller().throwEvent(new EconomyChangeEvent(this.getAccountName(), result));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                result = Double.MAX_VALUE;
            }
        }

        return format(result);
    }

    /**
     * withdraw a certain amount of money in the account
     *
     * @param amount       The amount of money to withdraw
     * @param world        The World / World group we want to withdraw money from
     * @param currencyName The currency we want to withdraw money from
     * @return The new balance. If the account has infinite money. Double.MAX_VALUE is returned.
     * @deprecated use {@link #withdraw(double, String, String, com.greatmancode.craftconomy3.Cause, String)}
     */
    @Deprecated
    public double withdraw(double amount, String world, String currencyName) {
        return withdraw(amount, world, currencyName, Cause.UNKNOWN, null);
    }

    /**
     * withdraw a certain amount of money in the account
     *
     * @param amount       The amount of money to withdraw
     * @param world        The World / World group we want to withdraw money from
     * @param currencyName The currency we want to withdraw money from
     * @param cause        The cause of the change.
     * @param causeReason  The reason of the cause.
     * @return The new balance. If the account has infinite money. Double.MAX_VALUE is returned.
     */
    public double withdraw(double amount, String world, String currencyName, Cause cause, String causeReason) {
        BalanceTable balanceTable;
        double result = 0;
        amount = format(amount);
        if (!Common.getInstance().getWorldGroupManager().worldGroupExist(world)) {
            world = Common.getInstance().getWorldGroupManager().getWorldGroupName(world);
        }
        Currency currency = Common.getInstance().getCurrencyManager().getCurrency(currencyName);
        if (currency != null) {
            if (!hasInfiniteMoney()) {
                try {
                    Connection connection = Common.getInstance().getDatabaseManager().getDatabase().getConnection();
                    PreparedStatement statement = connection.prepareStatement(BalanceTable.SELECT_WORLD_CURRENCY_ENTRY_ACCOUNT);
                    statement.setString(1, name);
                    statement.setString(2, world);
                    statement.setString(3, currency.getName());
                    ResultSet set = statement.executeQuery();
                    if (set.next()) {
                        result = set.getDouble(BalanceTable.BALANCE_FIELD) - amount;
                        statement.close();
                        statement = connection.prepareStatement(BalanceTable.UPDATE_ENTRY);
                        statement.setDouble(1, result);
                        statement.setString(2, name);
                        statement.setString(3, currency.getName());
                        statement.setString(4, world);
                        statement.executeUpdate();
                        statement.close();
                    } else {
                        result = amount;
                        statement = connection.prepareStatement(BalanceTable.INSERT_ENTRY);
                        statement.setDouble(1, result);
                        statement.setString(2, world);
                        statement.setString(3, name);
                        statement.setString(4, currency.getName());
                        statement.executeUpdate();
                        statement.close();
                    }
                    Common.getInstance().writeLog(LogInfo.WITHDRAW, cause, causeReason, this, amount, currency, world);
                    Common.getInstance().getServerCaller().throwEvent(new EconomyChangeEvent(this.getAccountName(), result));
                    statement.close();
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                result = Double.MAX_VALUE;
            }
        }
        return format(result);
    }

    /**
     * set a certain amount of money in the account
     *
     * @param amount       The amount of money to set
     * @param world        The World / World group we want to set money to
     * @param currencyName The currency we want to set money to
     * @return The new balance
     * @deprecated use {@link #set(double, String, String, com.greatmancode.craftconomy3.Cause, String)}
     */
    @Deprecated
    public double set(double amount, String world, String currencyName) {
        return set(amount, world, currencyName, Cause.UNKNOWN, null);
    }

    /**
     * set a certain amount of money in the account
     *
     * @param amount       The amount of money to set
     * @param world        The World group we want to set money to
     * @param currencyName The currency we want to set money to
     * @param cause        The cause of the change.
     * @param causeReason  The reason of the cause.
     * @return The new balance. If the account has infinite money. Double.MAX_VALUE is returned.
     */
    public double set(double amount, String world, String currencyName, Cause cause, String causeReason) {
        double result = 0;
        if (!Common.getInstance().getWorldGroupManager().worldGroupExist(world)) {
            world = Common.getInstance().getWorldGroupManager().getWorldGroupName(world);
        }
        amount = format(amount);
        Currency currency = Common.getInstance().getCurrencyManager().getCurrency(currencyName);
        if (currency != null) {
            if (!hasInfiniteMoney()) {
                try {
                    Connection connection = Common.getInstance().getDatabaseManager().getDatabase().getConnection();
                    PreparedStatement statement = connection.prepareStatement(BalanceTable.SELECT_WORLD_CURRENCY_ENTRY_ACCOUNT);
                    statement.setString(1, name);
                    statement.setString(2, world);
                    statement.setString(3, currency.getName());
                    ResultSet set = statement.executeQuery();
                    if (set.next()) {
                        result = amount;
                        statement.close();
                        statement = connection.prepareStatement(BalanceTable.UPDATE_ENTRY);
                        statement.setDouble(1, result);
                        statement.setString(2, name);
                        statement.setString(3, currency.getName());
                        statement.setString(4, world);
                        statement.executeUpdate();
                        statement.close();
                    } else {
                        result = amount;
                        statement = connection.prepareStatement(BalanceTable.INSERT_ENTRY);
                        statement.setDouble(1, result);
                        statement.setString(2, world);
                        statement.setString(3, name);
                        statement.setString(4, currency.getName());
                        statement.executeUpdate();
                        statement.close();
                    }
                    Common.getInstance().writeLog(LogInfo.SET, cause, causeReason, this, amount, currency, newWorld);
                    Common.getInstance().getServerCaller().throwEvent(new EconomyChangeEvent(this.getAccountName(), result));
                    statement.close();
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return format(result);
    }

    /**
     * Checks if we have enough money in a certain balance
     *
     * @param amount       The amount of money to check
     * @param worldName    The World / World group we want to check
     * @param currencyName The currency we want to check
     * @return True if there's enough money. Else false
     */
    public boolean hasEnough(double amount, String worldName, String currencyName) {
        boolean result = false;
        amount = format(amount);
        if (!Common.getInstance().getWorldGroupManager().worldGroupExist(worldName)) {
            worldName = Common.getInstance().getWorldGroupManager().getWorldGroupName(worldName);
        }
        Currency currency = Common.getInstance().getCurrencyManager().getCurrency(currencyName);
        if (currency != null && (getBalance(worldName, currencyName) >= amount || hasInfiniteMoney())) {
            result = true;
        }
        return result;
    }

    /**
     * Returns the world that the player is currently in
     *
     * @return The world name that the player is currently in or any if he is not online/Multiworld system not enabled
     * @deprecated Please use {@link com.greatmancode.craftconomy3.account.Account#getWorldGroupOfPlayerCurrentlyIn()}
     */
    private String getWorldPlayerCurrentlyIn() {
        return Common.getInstance().getServerCaller().getPlayerCaller().getPlayerWorld(getAccountName());
    }

    /**
     * Retrieve the world group of the player
     *
     * @return The worldGroup of the player.
     */
    public String getWorldGroupOfPlayerCurrentlyIn() {
        return Common.getInstance().getWorldGroupManager().getWorldGroupName(getWorldPlayerCurrentlyIn());
    }

    /**
     * Sets the account to have infinite money.
     *
     * @param infinite True if the account should have infinite money. Else false.
     */
    public void setInfiniteMoney(boolean infinite) {
        try {
            Connection connection = Common.getInstance().getDatabaseManager().getDatabase().getConnection();
            PreparedStatement statement = connection.prepareStatement(AccountTable.UPDATE_INFINITEMONEY_ENTRY);
            statement.setBoolean(1, infinite);
            statement.setString(2, getAccountName());
            statement.setBoolean(3, isBankAccount());
            statement.executeUpdate();
            statement.close();
            connection.close();
            infiniteMoney = infinite;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if the account have infinite money
     *
     * @return True if the account have infinite money. Else false.
     */
    public boolean hasInfiniteMoney() {
        return infiniteMoney;
    }

    /**
     * Format the value to be something less problematic.
     * Example: 50.00999999995 will become 50.00
     *
     * @param value The double to format
     * @return The formatted double
     */
    public static double format(double value) {
        if (value == Double.MAX_VALUE) {
            return value;
        }

        long factor = (long) Math.pow(10, 2);
        value = value * factor;
        double tmp = Math.floor(value);
        return tmp / factor;
    }

    /**
     * Check if the ACL is ignored for a bank account. That means that there will be no protection on the account and anybody can deposit/withdraw from it!
     *
     * @return True if the ACL is ignored else false.
     */
    public boolean ignoreACL() {
        return ignoreACL;
    }

    /**
     * Sets if a account should ignore his ACL. Only works on Bank accounts.
     *
     * @param ignore If the ACL is ignored or not
     */
    public void setIgnoreACL(boolean ignore) {
        try {
            Connection connection = Common.getInstance().getDatabaseManager().getDatabase().getConnection();
            PreparedStatement statement = connection.prepareStatement(AccountTable.UPDATE_IGNOREACL_ENTRY);
            statement.setBoolean(1, ignore);
            statement.setString(2, getAccountName());
            statement.setBoolean(3, isBankAccount());
            statement.executeUpdate();
            statement.close();
            connection.close();
            ignoreACL = ignore;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
