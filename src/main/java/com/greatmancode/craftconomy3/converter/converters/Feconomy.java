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
package com.greatmancode.craftconomy3.converter.converters;

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.converter.Converter;
import com.greatmancode.tools.utils.Tools;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Converter for Feconomy. Currently only MySQL
 *
 * @author greatman
 */
public class Feconomy extends Converter {
    private Connection connect;

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
        try {
            connect = DriverManager
                    .getConnection("jdbc:mysql://" + getDbConnectInfo().get("address") + ":" + getDbConnectInfo().get("port") + "/" + getDbConnectInfo().get("database") + "?"
                            + "user=" + getDbConnectInfo().get("username") + "&password=" + getDbConnectInfo().get("password"));
            result = true;
        } catch (SQLException e) {
            Common.getInstance().getLogger().severe("Unable to connect to Feconomy database. Reason: " + e.getMessage());
        }
        return result;
    }

    @Override
    public boolean importData(String sender) {
        return importDatabase(sender);
    }

    /**
     * Import accounts from the database.
     *
     * @param sender The command sender so we can send back messages.
     * @return True if the convert is done. Else false.
     */
    private boolean importDatabase(String sender) {
        PreparedStatement statement = null;
        try {
            statement = connect.prepareStatement("SELECT * FROM fe_accounts");
            ResultSet rs = statement.executeQuery();
            List<User> userList = new ArrayList<>();
            while (rs.next()) {
                userList.add(new User(rs.getString("name"), rs.getDouble("money")));
            }
            rs.close();
            addAccountToString(sender, userList);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Tools.closeJDBCStatement(statement);
            Tools.closeJDBCConnection(connect);
        }
        return true;
    }
}
