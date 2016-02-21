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
package com.greatmancode.craftconomy3.converter;

import com.greatmancode.craftconomy3.Common;
import lombok.Getter;

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
    private final List<String> dbTypes = new ArrayList<>();
    /**
     * Contains the selected Db Type.
     */
    private String selectedDbType;
    /**
     * Contains all the required fields about the selected database type
     */
    private final List<String> dbInfo = new ArrayList<>();
    /**
     * Contains all the information about the required fields entered by the user.
     */
    private final Map<String, String> dbConnectInfo = new HashMap<>();

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
     * @param sender The sender so we can send messages back to him
     * @param userList2 Account list
     */
    protected void addAccountToString(String sender, List<User> userList2) {
        Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, "{{DARK_RED}}Converting accounts. This may take a while.");
        Common.getInstance().getStorageHandler().getStorageEngine().saveImporterUsers(userList2);
        Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, userList2.size() + " {{DARK_GREEN}}accounts converted! Enjoy!");
    }

    /**
     * Represents a economy user
     */
    @Getter
    public class User {
        /**
         * the user name
         */
        private String user;
        /**
         * the account balance
         */
        private double balance;

        private String uuid;

        public User(String user, double balance) {
            this.user = user;
            this.balance = balance;
        }

        public User(String user, String uuid, double balance) {
            this.user = user;
            this.uuid = uuid;
            this.balance = balance;
        }
    }
}
