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
package com.greatmancode.craftconomy3.account;

import com.greatmancode.craftconomy3.Common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides access to a account.
 *
 * @author greatman
 */
public class AccountManager {
    private final Map<String, Account> accountList = new HashMap<>();
    private final Map<String, Account> bankList = new HashMap<>();

    /**
     * Retrieve a account. Accounts prefixed with bank: are bank accounts.
     *
     * @param name The name of the account to retrieve
     * @param bankAccount If the account is a bank account
     * @return A economy account
     */
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
            account = Common.getInstance().getStorageHandler().getStorageEngine().getAccount(newName, bankAccount);
            if (bankAccount) {
                bankList.put(newName, account);
            } else {
                accountList.put(newName, account);
            }
        }
        return account;
    }

    @Deprecated
    public Account getAccount(String name) {
        if (name.startsWith("bank:")) {
            return getAccount(name.split("bank:")[1], true);
        } else {
            return getAccount(name, false);
        }
    }

    /**
     * Check if a account exist in the database.
     *
     * @param name The name to check
     * @param bankAccount If the account is a bank account
     * @return True if the account exists else false
     */
    public boolean exist(String name, boolean bankAccount) {
        String newName = name;
        if (!Common.getInstance().getMainConfig().getBoolean("System.Case-sentitive")) {
            newName = name.toLowerCase();
        }
        boolean result;
        if (bankAccount) {
            result = bankList.containsKey(newName);
            if (!result) {
                result = Common.getInstance().getStorageHandler().getStorageEngine().accountExist(newName, bankAccount);
            }
        } else {
            result = accountList.containsKey(newName);
            if (!result) {
                result = Common.getInstance().getStorageHandler().getStorageEngine().accountExist(newName, bankAccount);
            }
        }
        return result;
    }

    @Deprecated
    public boolean exist(String name) {
        if (name.startsWith("bank:")) {
            return exist(name.split("bank:")[1], true);
        } else {
            return exist(name, false);
        }
    }
    /**
     * Delete a account from the system
     *
     * @param name The account name
     * @param bankAccount If the account is a bank account
     * @return True if the account has been deleted. Else false.
     */
    public boolean delete(String name, boolean bankAccount) {
        boolean result = false;
        if (exist(name, bankAccount)) {
            result = Common.getInstance().getStorageHandler().getStorageEngine().deleteAccount(name, bankAccount);
            if (bankAccount) {
                bankList.remove(name);
            } else {
                accountList.remove(name);
            }
        }
        return result;
    }

    /**
     * Clear the account cache from this user. Useful due to the UUID feature in minecraft.
     * @param name The name of the player/account.
     */
    public void clearCache(String name) {
        accountList.remove(name);
    }

    /**
     * Retrieve a list of all the accounts
     * @param bank If we want a bank list or not
     * @return A List of accounts
     */
    public List<String> getAllAccounts(boolean bank) {
        return Common.getInstance().getStorageHandler().getStorageEngine().getAllAccounts(bank);
    }
}
