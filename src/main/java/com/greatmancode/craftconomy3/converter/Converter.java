/*
 * This file is part of Craftconomy3.
 *
 * Copyright (c) 2011-2013, Greatman <http://github.com/greatman/>
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
package com.greatmancode.craftconomy3.converter;

import com.alta189.simplesave.sqlite.SQLiteDatabase;
import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.database.tables.AccountTable;
import com.greatmancode.craftconomy3.groups.WorldGroupsManager;

import java.util.*;

/**
 * Represents a Converter
 *
 * @author greatman
 */
public abstract class Converter {
    public static final int ALERT_EACH_X_ACCOUNT = 10;
    private StringBuilder stringBuilder = new StringBuilder();
    /**
     * Contains the type of Database (flatfile, sqlite, etc.) supported by the originating plugin
     */
    private final List<String> dbTypes = new ArrayList<String>();
    /**
     * Contains the selected Db Type.
     */
    private String selectedDbType;
    /**
     * Contains all the required fields about the selected database type
     */
    private final List<String> dbInfo = new ArrayList<String>();
    /**
     * Contains all the information about the required fields entered by the user.
     */
    private final Map<String, String> dbConnectInfo = new HashMap<String, String>();

    /**
     * Retrieve a list of all the database type.
     *
     * @return A list of database type.
     */
    public List<String> getDbTypes() {
        return dbTypes;
    }

    /**
     * Retrieve the information required about the selected input data method.
     *
     * @return the information required about the selected input data method.
     */
    protected List<String> getDbInfoList() {
        return dbInfo;
    }

    /**
     * Sets the selected database type.
     *
     * @param dbType The database type selected
     * @return True if the database type has been saved else false (A invalid type)
     */
    public boolean setDbType(String dbType) {
        boolean result = false;
        if (dbTypes.contains(dbType)) {
            setSelectedDbType(dbType);
            result = true;
        }
        return result;
    }

    /**
     * Retrieve the list of the possible input data method
     *
     * @return A list of the possible input data method
     */
    public abstract List<String> getDbInfo();

    /**
     * Sets a field information for the selected database type
     *
     * @param field The field name.
     * @param value The value of the field.
     * @return True if the field has been saved else false (A invalid field)
     */
    public boolean setDbInfo(String field, String value) {
        boolean result = false;
        if (dbInfo.contains(field)) {
            dbConnectInfo.put(field, value);
            result = true;
        }
        return result;
    }

    public Map<String, String> getDbConnectInfo() {
        return dbConnectInfo;
    }

    /**
     * Checks if we filled all the required fields
     *
     * @return True if all fields has been filled else false.
     */
    public boolean allSet() {
        return dbInfo.size() == dbConnectInfo.size();
    }

    /**
     * Connects to the database
     *
     * @return True if the connection is successful else false.
     */
    public abstract boolean connect();

    /**
     * Import all the data into Craftconomy
     *
     * @param sender The name of the sender so we can send status update.
     * @return True if everything went well else false.
     */
    public abstract boolean importData(String sender);

    /**
     * Get the selected database type
     *
     * @return the selected database type
     */
    public String getSelectedDbType() {
        return selectedDbType;
    }

    /**
     * Set the database type
     *
     * @param selectedDbType the database type
     */
    public void setSelectedDbType(String selectedDbType) {
        if (dbTypes.contains(selectedDbType)) {
            this.selectedDbType = selectedDbType;
        }
    }

    public String getWarning() {
        return null;
    }

    /**
     * Add the given accounts to the system
     *
     * @param userList2 Account list
     */
    protected void addAccountToString(List<User> userList2) {
        stringBuilder = new StringBuilder();
        List<User> userList = new ArrayList<User>(userList2);
        stringBuilder.append("INSERT INTO ").append(Common.getInstance().getMainConfig().getString("System.Database.Prefix")).append("account(name) VALUES ");
        Iterator<User> iterator = userList.iterator();
        boolean first = true, isCaseSentitive = Common.getInstance().getMainConfig().getBoolean("System.Case-sentitive"), isSQLite = Common.getInstance().getDatabaseManager().getDatabase() instanceof SQLiteDatabase;
        while (iterator.hasNext()) {
            if (isSQLite && !first) {
                stringBuilder = new StringBuilder();
                stringBuilder.append("INSERT INTO ").append(Common.getInstance().getMainConfig().getString("System.Database.Prefix")).append("account(name) VALUES ");
            }
            User user = iterator.next();
            if (isCaseSentitive) {
                stringBuilder.append("('").append(user.user).append("')");
            } else {
                stringBuilder.append("('").append(user.user.toLowerCase()).append("')");
            }

            if (!isSQLite) {
                if (iterator.hasNext()) {
                    stringBuilder.append(",");
                }
            } else {
                Common.getInstance().getDatabaseManager().getDatabase().directQuery(stringBuilder.toString());
                first = false;
            }
        }
        if (!isSQLite) {
            Common.getInstance().getDatabaseManager().getDatabase().directQuery(stringBuilder.toString());
        }
    }

    /**
     * Add the balance of the imported accounts
     *
     * @param sender   The command sender
     * @param userList the user list
     */
    protected void addBalance(String sender, List<User> userList) {
        int i = 0;
        stringBuilder = new StringBuilder();

        stringBuilder.append("INSERT INTO ").append(Common.getInstance().getMainConfig().getString("System.Database.Prefix")).append("balance(username_id, currency_id, worldName,balance) VALUES ");
        Iterator<User> iterator = userList.iterator();
        boolean first = true, isSQLite = Common.getInstance().getDatabaseManager().getDatabase() instanceof SQLiteDatabase;
        while (iterator.hasNext()) {
            if (i % ALERT_EACH_X_ACCOUNT == 0) {
                Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, i + " {{DARK_GREEN}}of {{WHITE}} " + userList.size() + " {{DARK_GREEN}}accounts ready to be imported.");
            }
            if (isSQLite && !first) {
                stringBuilder = new StringBuilder();
                stringBuilder.append("INSERT INTO ").append(Common.getInstance().getMainConfig().getString("System.Database.Prefix")).append("balance(username_id, currency_id, worldName,balance) VALUES ");
            }
            User user = iterator.next();
            AccountTable account = Common.getInstance().getDatabaseManager().getDatabase().select(AccountTable.class).where().equal("name", user.user).execute().findOne();
            stringBuilder.append("(").append(account.getId()).append(",").append(Common.getInstance().getCurrencyManager().getDefaultCurrency().getDatabaseID()).append(",'").append(WorldGroupsManager.DEFAULT_GROUP_NAME).append("',").append(user.balance).append(")");
            if (!isSQLite) {
                if (iterator.hasNext()) {
                    stringBuilder.append(",");
                }
            } else {
                Common.getInstance().getDatabaseManager().getDatabase().directQuery(stringBuilder.toString());
                first = false;
            }
            i++;
        }
        if (!isSQLite) {
            Common.getInstance().getDatabaseManager().getDatabase().directQuery(stringBuilder.toString());
        }
        Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, userList.size() + " {{DARK_GREEN}}accounts converted! Enjoy!");
    }

    /**
     * Represents a economy user
     */
    protected class User {
        /**
         * the user name
         */
        public String user;
        /**
         * the account balance
         */
        public double balance;

        public User(String user, double balance) {
            this.user = user;
            this.balance = balance;
        }
    }
}
