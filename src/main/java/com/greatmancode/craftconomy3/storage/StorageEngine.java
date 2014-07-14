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
import com.greatmancode.craftconomy3.DisplayFormat;
import com.greatmancode.craftconomy3.LogInfo;
import com.greatmancode.craftconomy3.account.Account;
import com.greatmancode.craftconomy3.account.AccountACLValue;
import com.greatmancode.craftconomy3.account.Balance;
import com.greatmancode.craftconomy3.currency.Currency;

import java.util.List;
import java.util.Map;

/**
 * Created by greatman on 2014-07-13.
 */
public abstract class StorageEngine {


    public abstract void disable();

    /**
     * Retrieve an account from the storage. If it doesn't exist, it must be created
     * @param name The account name
     * @param isBank If the account is a bank or not
     * @return
     */
    public abstract Account getAccount(String name, boolean isBank);


    /**
     * Write a transaction to the Log.
     *
     * @param info          The type of transaction to log.
     * @param cause         The cause of the transaction.
     * @param causeReason   The reason of the cause
     * @param account       The account being impacted by the change
     * @param amount        The amount of money in this transaction.
     * @param currency      The currency associated with this transaction
     * @param worldName     The world name associated with this transaction
     */
    public abstract void saveLog(LogInfo info, Cause cause, String causeReason, Account account, double amount, Currency currency, String worldName);

    /**
     * Retrieve the configuration value
     * @param name The name of the value
     * @return The configuration value or null if the entry was not found
     */
    public abstract String getConfigEntry(String name);

    public abstract void setConfigEntry(String name, String value);

    public abstract List<Balance> getAllBalance(Account account);

    public abstract List<Balance> getAllWorldBalance(Account account, String world);

    public abstract double getBalance(Account account, Currency currency, String world);

    public abstract double setBalance(Account account, double amount, Currency currency, String world);

    public abstract void setInfiniteMoney(Account account, boolean infinite);

    public abstract void setIgnoreACL(Account account, boolean ignoreACL);

    public abstract Map<String, AccountACLValue> retrieveACL(Account account);
}
