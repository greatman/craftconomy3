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

import com.greatmancode.craftconomy3.converter.Converter;
import com.greatmancode.craftconomy3.storage.sql.tables.mineconomy.MineconomyTable;
import com.greatmancode.tools.utils.Tools;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Mineconomy extends Converter {

    private HikariDataSource db;

    public Mineconomy() {
        getDbTypes().add("flatfile");
        getDbTypes().add("mysql");
    }

    @Override
    public List<String> getDbInfo() {
        if (getDbInfoList().size() == 0) {
            if ("mysql".equals(getSelectedDbType())) {
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
        if ("flatfile".equals(getSelectedDbType())) {

        } else if ("mysql".equals(getSelectedDbType())) {
            HikariConfig config = new HikariConfig();
            config.setMaximumPoolSize(10);
            config.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
            config.addDataSourceProperty("serverName", getDbConnectInfo().get("address"));
            config.addDataSourceProperty("port", getDbConnectInfo().get("port"));
            config.addDataSourceProperty("databaseName", getDbConnectInfo().get("database"));
            config.addDataSourceProperty("user", getDbConnectInfo().get("username"));
            config.addDataSourceProperty("password", getDbConnectInfo().get("password"));
            config.addDataSourceProperty("autoDeserialize", true);
            db = new HikariDataSource(config);
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
        if ("flatfile".equals(getSelectedDbType())) {
            userList = importFlatfile(sender);
        } else {
            userList = importMySQL(sender);
        }
        addAccountToString(sender, userList);
        return true;
    }

    private List<User> importMySQL(String sender) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = db.getConnection();
            statement = connection.prepareStatement(MineconomyTable.SELECT_ENTRY);
            ResultSet set = statement.executeQuery();
            List<User> userList = new ArrayList<>();
            while (set.next()) {
                userList.add(new User(set.getString("account"), set.getDouble("balance")));
            }
            return userList;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Tools.closeJDBCStatement(statement);
            Tools.closeJDBCConnection(connection);
        }
        return null;
    }

    private List<User> importFlatfile(String sender) {
        return null;
    }
}
