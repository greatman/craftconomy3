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
package com.greatmancode.craftconomy3.converter.converters;

import com.alta189.simplesave.Database;
import com.alta189.simplesave.DatabaseFactory;
import com.alta189.simplesave.exceptions.ConnectionException;
import com.alta189.simplesave.exceptions.TableRegistrationException;
import com.alta189.simplesave.mysql.MySQLConfiguration;
import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.converter.Converter;
import com.greatmancode.craftconomy3.database.tables.feconomy.FeconomyTable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Converter for Feconomy. Currently only MySQL
 *
 * @author greatman
 */
public class Feconomy extends Converter {
    private Database db = null;

    public Feconomy() {
        getDbTypes().add("mysql");
    }

    @Override
    public List<String> getDbInfo() {

        if (getDbInfoList().size() == 0) {
            getDbInfoList().add("address");
            getDbInfoList().add("port");
            getDbInfoList().add("username");
            getDbInfoList().add("password");
            getDbInfoList().add("database");
        }
        return getDbInfoList();
    }

    @Override
    public boolean connect() {
        boolean result = false;
        loadMySQL();

        if (db != null) {

            try {
                db.registerTable(FeconomyTable.class);
                db.setCheckTableOnRegistration(false);
                db.connect();
                result = true;
            } catch (TableRegistrationException e) {
                Common.getInstance().getLogger().severe("Unable to register Feconomy tables. Reason: " + e.getMessage());
            } catch (ConnectionException e) {
                Common.getInstance().getLogger().severe("Unable to connect to Feconomy database. Reason: " + e.getMessage());
            }
        }
        return result;
    }

    /**
     * Allow to load a MySQL database.
     */
    private void loadMySQL() {
        try {
            MySQLConfiguration config = new MySQLConfiguration();
            config.setHost(getDbConnectInfo().get("address"));
            config.setUser(getDbConnectInfo().get("username"));
            config.setPassword(getDbConnectInfo().get("password"));
            config.setDatabase(getDbConnectInfo().get("database"));
            config.setPort(Integer.parseInt(getDbConnectInfo().get("port")));
            db = DatabaseFactory.createNewDatabase(config);
        } catch (NumberFormatException e) {
            Common.getInstance().getLogger().severe("Illegal Port!");
        }
    }

    @Override
    public boolean importData(String sender) {
        boolean result = false;
        result = importDatabase(sender);
        return result;
    }

    /**
     * Import accounts from the database.
     *
     * @param sender The command sender so we can send back messages.
     * @return True if the convert is done. Else false.
     */
    private boolean importDatabase(String sender) {
        List<FeconomyTable> icoList = db.select(FeconomyTable.class).execute().find();
        if (icoList != null && icoList.size() > 0) {
            Iterator<FeconomyTable> icoListIterator = icoList.iterator();
            List<User> userList = new ArrayList<User>();
            while (icoListIterator.hasNext()) {
                FeconomyTable entry = icoListIterator.next();
                userList.add(new User(entry.getName(), entry.getMoney()));
            }
            addAccountToString(userList);
            addBalance(sender, userList);
        }
        try {
            db.close();
        } catch (ConnectionException e) {
            Common.getInstance().getLogger().severe("Unable to disconnect from the Feconomy database! Message: " + e.getMessage());
        }
        return true;
    }
}
