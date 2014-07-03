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

    /**
     * Retrieve a account. Accounts prefixed with bank: are bank accounts.
     *
     * @param name The name of the account to retrieve
     * @return A economy account
     */
    public Account getAccount(String name) {
        String newName = name;
        if (!Common.getInstance().getMainConfig().getBoolean("System.Case-sentitive")) {
            newName = name.toLowerCase();
        }
        Account account;
        if (accountList.containsKey(newName)) {
            account = accountList.get(newName);
        } else {
            account = new Account(newName);
            accountList.put(newName, account);
        }
        return account;
    }

    /**
     * Check if a account exist in the database.
     *
     * @param name The name to check
     * @return True if the account exists else false
     */
    public boolean exist(String name) {
        String newName = name;
        if (!Common.getInstance().getMainConfig().getBoolean("System.Case-sentitive")) {
            newName = name.toLowerCase();
        }
        return accountList.containsKey(newName) || Common.getInstance().getDatabaseManager().getDatabase().select(AccountTable.class).where().equal("name", newName).execute().findOne() != null;
    }

    /**
     * Delete a account from the system
     *
     * @param name The account name
     * @return True if the account has been deleted. Else false.
     */
    public boolean delete(String name) {
        boolean result = false;
        if (exist(name)) {
            Account account = getAccount(name);
            AccountTable accountTable = Common.getInstance().getDatabaseManager().getDatabase().select(AccountTable.class).where().contains("name", name).execute().findOne();
            List<BalanceTable> balanceTableList = Common.getInstance().getDatabaseManager().getDatabase().select(BalanceTable.class).where().equal("username_id", accountTable.getId()).execute().find();
            if (balanceTableList != null) {
                for (BalanceTable aBalanceTableList : balanceTableList) {
                    Common.getInstance().getDatabaseManager().getDatabase().remove(aBalanceTableList);
                }
            }
            if (account.isBankAccount()) {
                List<AccessTable> accessTableList = Common.getInstance().getDatabaseManager().getDatabase().select(AccessTable.class).where().equal("account_id", accountTable.getId()).execute().find();
                if (accessTableList != null) {
                    for (AccessTable anAccessTableList : accessTableList) {
                        Common.getInstance().getDatabaseManager().getDatabase().remove(anAccessTableList);
                    }
                }
            }
            Common.getInstance().getDatabaseManager().getDatabase().remove(accountTable);
            accountList.remove(name);
            result = true;
        }
        return result;
    }

    public void clearCache(String name) {
        accountList.remove(name);
    }
}
