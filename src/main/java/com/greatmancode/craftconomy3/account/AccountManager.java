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

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.database.tables.AccessTable;
import com.greatmancode.craftconomy3.database.tables.AccountTable;
import com.greatmancode.craftconomy3.database.tables.BalanceTable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides access to a account.
 *
 * @author greatman
 */
public class AccountManager {
    private final Map<String, Account> accountList = new HashMap<String, Account>();
    private final Map<String, Account> bankList = new HashMap<String, Account>();
    /**
     * Retrieve a account. Accounts prefixed with bank: are bank accounts.
     *
     * @param name The name of the account to retrieve
     * @return A economy account
     * @deprecated Use { @link #getAccount(String, boolean) }
     */
    public Account getAccount(String name) {
        //TODO LEGACY SUPPORT
        if (name.startsWith("bank:")) {
            return getAccount(name, true);
        } else {
            return getAccount(name, false);
        }
    }

    public Account getAccount(String name, boolean bankAccount) {
        String newName = name;
        if (!Common.getInstance().getMainConfig().getBoolean("System.Case-sentitive")) {
            newName = name.toLowerCase();
        }
        Account account;
        if (bankAccount && bankList.containsKey(newName)) {
            account = bankList.get(newName);
        } else if (!bankAccount && accountList.containsKey(newName)) {
            account = accountList.get(newName);
        } else {
            account = Common.getInstance().getStorageHandler().getStorageEngine().getAccount(name, bankAccount);
            if (bankAccount) {
                bankList.put(newName, account);
            } else {
                accountList.put(newName, account);
            }
        }
        return account;
    }
    /**
     * Check if a account exist in the database.
     *
     * @param name The name to check
     * @return True if the account exists else false
     * @deprecated Use { @link #exist(String, boolean) }
     */
    public boolean exist(String name) {
        //TODO LEGACY SUPPORT
        if (name.startsWith("bank:")) {
            return exist(name, true);
        } else {
            return exist(name, false);
        }
    }

    public boolean exist(String name, boolean bankAccount) {
        String newName = name;
        if (!Common.getInstance().getMainConfig().getBoolean("System.Case-sentitive")) {
            newName = name.toLowerCase();
        }
        boolean result;
        if (bankAccount) {
            result = bankList.containsKey(newName);
            if (!result) {
                result = Common.getInstance().getStorageHandler().getStorageEngine().accountExist(name, bankAccount);
            }
        } else {
            result = accountList.containsKey(newName);
            if (!result) {
                result = Common.getInstance().getStorageHandler().getStorageEngine().accountExist(name, bankAccount);
            }
        }
        return result;
    }

    /**
     * Delete a account from the system
     *
     * @param name The account name
     * @return True if the account has been deleted. Else false.
     * @deprecated Use { @link #delete(String, boolean) }
     */
    public boolean delete(String name) {
        //TODO LEGACY SUPPORT
        if (name.startsWith("bank:")) {
            return delete(name, true);
        } else {
            return delete(name, false);
        }

    }

    public boolean delete(String name, boolean bankAccount) {
        boolean result = false;
        if (exist(name, bankAccount)) {
            result = Common.getInstance().getStorageHandler().getStorageEngine().deleteAccount(name, bankAccount);
        }
        return result;
    }

    public void clearCache(String name) {
        accountList.remove(name);
    }
}
