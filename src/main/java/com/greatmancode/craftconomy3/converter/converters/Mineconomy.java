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
package com.greatmancode.craftconomy3.converter.converters;

import com.alta189.simplesave.Database;
import com.alta189.simplesave.DatabaseFactory;
import com.alta189.simplesave.exceptions.ConnectionException;
import com.alta189.simplesave.exceptions.TableRegistrationException;
import com.alta189.simplesave.mysql.MySQLConfiguration;
import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.converter.Converter;
import com.greatmancode.craftconomy3.database.tables.mineconomy.MineconomyTable;

import java.util.ArrayList;
import java.util.List;

public class Mineconomy extends Converter {

    private Database db;

    public Mineconomy() {
        getDbTypes().add("flatfile");
        getDbTypes().add("mysql");
    }

    @Override
    public List<String> getDbInfo() {
        if (getDbInfoList().size() == 0) {
            if (getSelectedDbType().equalsIgnoreCase("mysql")) {
                getDbInfoList().add("address");
                getDbInfoList().add("port");
                getDbInfoList().add("username");
                getDbInfoList().add("password");
                getDbInfoList().add("database");
            }
        }
        return getDbInfoList();
    }

    @Override
    public boolean connect() {
        boolean result = false;
        if (getSelectedDbType().equalsIgnoreCase("flatfile")) {

        } else if (getSelectedDbType().equalsIgnoreCase("mysql")) {
            try {
                MySQLConfiguration config = new MySQLConfiguration();
                config.setHost(getDbConnectInfo().get("address"));
                config.setUser(getDbConnectInfo().get("username"));
                config.setPassword(getDbConnectInfo().get("password"));
                config.setDatabase(getDbConnectInfo().get("database"));
                config.setPort(Integer.parseInt(getDbConnectInfo().get("port")));
                db = DatabaseFactory.createNewDatabase(config);
                db.setCheckTableOnRegistration(false);
                db.registerTable(MineconomyTable.class);
                db.connect();
                result = true;
            } catch (NumberFormatException e) {
                Common.getInstance().getLogger().severe("Illegal Port!");
            } catch (ConnectionException e) {
                Common.getInstance().getLogger().severe("Error while connecting to Mineconomy database! Error: " + e.getMessage());
            } catch (TableRegistrationException e) {
                Common.getInstance().getLogger().severe("Error while registering table for the Mineconomy database! Error: " + e.getMessage());
            }
        }
        return result;
    }

    @Override
    public String getWarning() {
        return "Multi-currency support of Mineconomy currently not supported. Flatfile also not currently supported";
    }

    @Override
    public boolean importData(String sender) {
        List<User> userList;
        if (getSelectedDbType().equalsIgnoreCase("flatfile")) {
            userList = importFlatfile(sender);
        } else {
            userList = importMySQL(sender);
        }
        addAccountToString(userList);
        addBalance(sender, userList);
        return true;
    }

    private List<User> importMySQL(String sender) {
        List<MineconomyTable> tables = db.select(MineconomyTable.class).execute().find();
        List<User> userList = new ArrayList<User>();
        for (MineconomyTable entry : tables) {
            userList.add(new User(entry.getAccount(), entry.getBalance()));
        }
        return userList;
    }

    private List<User> importFlatfile(String sender) {
        return null;
    }
}
