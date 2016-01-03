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

import java.util.Map;

/**
 * Used with bank accounts. Takes care of the access control of a bank account.
 *
 * @author greatman
 */
public class AccountACL {
    private final Map<String, AccountACLValue> aclList;
    private final Account account;

    public AccountACL(Account account) {
        this.account = account;
        aclList = Common.getInstance().getStorageHandler().getStorageEngine().retrieveACL(account);
    }

    /**
     * Checks if a player can deposit money
     *
     * @param name The player name
     * @return True if the player can deposit money, else false
     */
    public boolean canDeposit(String name) {
        if (getParent().ignoreACL()) {
            return true;
        }
        String newName = name.toLowerCase();
        boolean result = false;
        if (aclList.containsKey(newName)) {
            result = aclList.get(newName).canDeposit();
        }
        return result;
    }

    /**
     * Checks if a player can withdraw money
     *
     * @param name The player name
     * @return True if the player can withdraw money, else false.
     */
    public boolean canWithdraw(String name) {
        if (getParent().ignoreACL()) {
            return true;
        }
        String newName = name.toLowerCase();
        boolean result = false;
        if (aclList.containsKey(newName)) {
            result = aclList.get(newName).canWithdraw();
        }
        return result;
    }

    /**
     * Checks if a player can modify the ACL
     *
     * @param name The player name
     * @return True if the player can modify the ACL, else false.
     */
    public boolean canAcl(String name) {
        if (getParent().ignoreACL()) {
            return false;
        }
        String newName = name.toLowerCase();
        boolean result = false;
        if (aclList.containsKey(newName)) {
            result = aclList.get(newName).canAcl();
        }
        return result;
    }

    /**
     * Checks if a player can show the balance of the account
     *
     * @param name The player name
     * @return True if the player can show the balance of the account, else false.
     */
    public boolean canShow(String name) {
        if (getParent().ignoreACL()) {
            return true;
        }
        String newName = name.toLowerCase();
        boolean result = false;
        if (aclList.containsKey(newName)) {
            result = aclList.get(newName).canBalance();
        }
        return result;
    }

    /**
     * Set if a player can deposit money in the account
     *
     * @param name    The Player name
     * @param deposit Can deposit or not
     */
    public void setDeposit(String name, boolean deposit) {
        String newName = name.toLowerCase();
        if (aclList.containsKey(newName)) {
            AccountACLValue value = aclList.get(newName);
            set(newName, deposit, value.canWithdraw(), value.canAcl(), value.canBalance(), value.isOwner());
        } else {
            set(newName, deposit, false, false, false, false);
        }
    }

    /**
     * Set if a player can withdraw money in the account
     *
     * @param name     The Player name
     * @param withdraw Can withdraw or not
     */
    public void setWithdraw(String name, boolean withdraw) {
        String newName = name.toLowerCase();
        if (aclList.containsKey(name)) {
            AccountACLValue value = aclList.get(newName);
            set(newName, value.canDeposit(), withdraw, value.canAcl(), value.canBalance(), value.isOwner());
        } else {
            set(newName, false, withdraw, false, false, false);
        }
    }

    /**
     * Set if a player can modify the ACL list
     *
     * @param name The player name
     * @param acl  can modify the ACL or not
     */
    public void setAcl(String name, boolean acl) {
        String newName = name.toLowerCase();
        if (aclList.containsKey(newName)) {
            AccountACLValue value = aclList.get(newName);
            set(newName, value.canDeposit(), value.canWithdraw(), acl, value.canBalance(), value.isOwner());
        } else {
            set(newName, false, false, acl, false, false);
        }
    }

    /**
     * Set if a player can show the bank balance.
     *
     * @param name The player name
     * @param show can show the bank balance or not.
     */
    public void setShow(String name, boolean show) {
        String newName = name.toLowerCase();
        if (aclList.containsKey(newName)) {
            AccountACLValue value = aclList.get(newName);
            set(newName, value.canDeposit(), value.canWithdraw(), value.canAcl(), show, value.isOwner());
        } else {
            set(newName, false, false, false, show, false);
        }
    }

    /**
     * Set a player in the ACL list
     *
     * @param name     The Player
     * @param deposit  Can deposit or not
     * @param withdraw Can withdraw or not
     * @param acl      Can modify the ACL or not
     * @param show     Can show the balance
     * @param owner    If he is the owner
     */
    public void set(String name, boolean deposit, boolean withdraw, boolean acl, boolean show, boolean owner) {
        String newName = name.toLowerCase();
        aclList.put(name, Common.getInstance().getStorageHandler().getStorageEngine().saveACL(account, newName, deposit, withdraw, acl, show, owner));
    }

    /**
     * Checks if the player is the bank owner. This is not affected by the ACL ignore.
     *
     * @param name The player name to check
     * @return True if the player is the owner of the account. Else false.
     */
    public boolean isOwner(String name) {
        boolean result = false;
        if (aclList.containsKey(name)) {
            result = aclList.get(name).isOwner();
        }
        return result;
    }

    /**
     * Returns the related account
     *
     * @return The related account
     */
    public Account getParent() {
        return account;
    }
}
