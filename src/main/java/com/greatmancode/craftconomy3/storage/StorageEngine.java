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
package com.greatmancode.craftconomy3.storage;

import com.greatmancode.craftconomy3.Cause;
import com.greatmancode.craftconomy3.LogInfo;
import com.greatmancode.craftconomy3.account.Account;
import com.greatmancode.craftconomy3.account.AccountACLValue;
import com.greatmancode.craftconomy3.account.Balance;
import com.greatmancode.craftconomy3.commands.currency.CurrencyRatesCommand;
import com.greatmancode.craftconomy3.commands.money.LogCommand;
import com.greatmancode.craftconomy3.commands.money.TopCommand;
import com.greatmancode.craftconomy3.currency.Currency;
import com.greatmancode.craftconomy3.groups.WorldGroup;
import com.greatmancode.craftconomy3.utils.NoExchangeRate;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class StorageEngine {

    /**
     * Disable the storage engine.
     */
    public abstract void disable();

    /**
     * Retrieve an account from the storage. If it doesn't exist, it must be created
     *
     * @param name   The account name
     * @param isBank If the account is a bank or not
     * @return
     */
    public abstract Account getAccount(String name, boolean isBank);

    /**
     * Retrieve an account from the storage. Doesn't care if it doesn't exist.
     *
     * @param uuid The UUID of the player
     * @return An Account if an account was found, else null
     */
    public abstract Account getAccount(UUID uuid);

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
    public abstract void saveLog(LogInfo info, Cause cause, String causeReason, Account account, double amount, Currency currency, String worldName);

    public abstract void saveLog(LogInfo info, Cause cause, String causeReason, Account account, double amount, Currency currency, String worldName, Timestamp timestamp);

    /**
     * Retrieve the configuration value
     *
     * @param name The name of the value
     * @return The configuration value or null if the entry was not found
     */
    public abstract String getConfigEntry(String name);

    /**
     * Set a configuration value in the database
     *
     * @param name  The name of the value
     * @param value The actual value
     */
    public abstract void setConfigEntry(String name, String value);

    /**
     * Retrieve all the balance of an account
     *
     * @param account The account to retrieve the balance from
     * @return A list of { @link Balance }
     */
    public abstract List<Balance> getAllBalance(Account account);

    /**
     * Retrieve all balance from a world
     *
     * @param account The account to retrieve the balance from
     * @param world   The world group to retrieve the balance from
     * @return A list of { @link Balance }
     */
    public abstract List<Balance> getAllWorldBalance(Account account, String world);

    /**
     * Retrieve the balance of an account
     *
     * @param account  The account to retrieve the balance from
     * @param currency The currency
     * @param world    The world group
     * @return the balance
     */
    public abstract double getBalance(Account account, Currency currency, String world);

    /**
     * Set the balance of the account
     *
     * @param account  The account that the balance is set in.
     * @param amount   The amount of money being placed
     * @param currency The Currency
     * @param world    The world group
     * @return The balance
     */
    public abstract double setBalance(Account account, double amount, Currency currency, String world);

    /**
     * Set if the account have infinite money
     *
     * @param account  The account to modify
     * @param infinite If the account have infinite money or not
     */
    public abstract void setInfiniteMoney(Account account, boolean infinite);

    /**
     * Set if the account must ignore the ACL
     *
     * @param account   The account to modify
     * @param ignoreACL If the account must ignore the ACL or not
     */
    public abstract void setIgnoreACL(Account account, boolean ignoreACL);

    /**
     * Retrieve the ACL listing for a bank account
     *
     * @param account The bank account
     * @return A Map of all the ACL entries
     */
    public abstract Map<String, AccountACLValue> retrieveACL(Account account);

    /**
     * Save the ACL setting of a bank account
     *
     * @param account  The account
     * @param name     The player name
     * @param deposit  If the player can deposit
     * @param withdraw If the player can withdraw
     * @param acl      If the player can modify the ACL
     * @param show     If the player can show the balance
     * @param owner    If the player is the owner
     * @return
     */
    public abstract AccountACLValue saveACL(Account account, String name, boolean deposit, boolean withdraw, boolean acl, boolean show, boolean owner);

    /**
     * Retrieve the exchange rate between 2 currencies
     *
     * @param currency      The currency to convert from
     * @param otherCurrency The currency to convert to
     * @return The exchange rate.
     * @throws NoExchangeRate If there's no exchange rate, this event is thrown.
     */
    public abstract double getExchangeRate(Currency currency, Currency otherCurrency) throws NoExchangeRate;

    /**
     * Set the exchange between 2 currencies in the backend
     *
     * @param currency      The currency to convert from
     * @param otherCurrency The currency to convert to
     * @param amount        The exchange rate (Example: 1.3 will make 1 Currency transform to 1.3 otherCurrency)
     */
    public abstract void setExchangeRate(Currency currency, Currency otherCurrency, double amount);

    /**
     * Save a currency in the backend
     *
     * @param currency The currency to save
     */
    public abstract void saveCurrency(Currency currency);

    /**
     * Delete a currency from the storage
     *
     * @param currency The currency to delete
     */
    public abstract void deleteCurrency(Currency currency);

    /**
     * Update the username in the account
     *
     * @param name The name of the player
     * @param uuid The UUID of the player
     */
    public abstract void updateUsername(String name, UUID uuid);

    /**
     * Set the UUID of the account if it exists
     *
     * @param name The player name
     * @param uuid the UUID of the player
     */
    public abstract void updateUUID(String name, UUID uuid);

    public abstract Map<String, WorldGroup> getWorldGroups();

    public abstract void removeWorldGroup(String group);

    public abstract String[] getBankAccountList(String sender);

    public abstract List<LogCommand.LogEntry> getLog(Account user, int page);

    public abstract List<TopCommand.TopEntry> getTopEntry(int page, Currency currency, String world);

    public abstract List<CurrencyRatesCommand.CurrencyRateEntry> getCurrencyExchanges();

    public abstract void cleanLog(Timestamp timestamp);

    public abstract boolean deleteAccount(String name, boolean bankAccount);

    public abstract boolean accountExist(String name, boolean bankAccount);

    public abstract void saveWorldGroup(String name, String worldList);

    public abstract List<String> getAllCurrencyNames();

    public abstract void setDefaultCurrency(Currency currency);

    public abstract Currency getCurrency(String name);

    public abstract Map<String,Currency> getAllCurrencies();

    public abstract String retrieveWorldGroupWorlds(String name);
}
