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

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.converter.Converter;
import com.greatmancode.craftconomy3.database.tables.iconomy.IConomyTable;
import com.greatmancode.craftconomy3.database.tables.mineconomy.MineconomyTable;
import com.greatmancode.tools.database.DatabaseManager;
import com.greatmancode.tools.database.interfaces.DatabaseType;
import com.greatmancode.tools.database.throwable.InvalidDatabaseConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Mineconomy extends Converter {

    private DatabaseManager db;

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
                db = new DatabaseManager(DatabaseType.MYSQL,getDbConnectInfo().get("address"),Integer.parseInt(getDbConnectInfo().get("port")),getDbConnectInfo().get("username"),getDbConnectInfo().get("password"),getDbConnectInfo().get("database"),"", Common.getInstance().getServerCaller());
                db.getDatabase().getConnection().close();
                result = true;
            } catch (InvalidDatabaseConstructor invalidDatabaseConstructor) {
                Common.getInstance().getLogger().severe("Invalid database constructor! Error:" + invalidDatabaseConstructor.getMessage());
                db = null;
            } catch (SQLException e) {
                Common.getInstance().getLogger().severe("Error testing the connection! Error:" + e.getMessage());
                db = null;
            } catch (NumberFormatException e) {
                Common.getInstance().getLogger().severe("Illegal Port!");
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
        try {
            Connection connection = db.getDatabase().getConnection();
            PreparedStatement statement = connection.prepareStatement(MineconomyTable.SELECT_ENTRY);
            ResultSet set = statement.executeQuery();
            List<User> userList = new ArrayList<User>();
            while (set.next()) {
                userList.add(new User(set.getString("username"), set.getDouble("balance")));
            }
            addAccountToString(userList);
            addBalance(sender, userList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Conncetion connection =
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
