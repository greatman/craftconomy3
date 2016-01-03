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
package com.greatmancode.craftconomy3;

import java.io.File;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Logger;

import com.greatmancode.craftconomy3.storage.sql.MySQLEngine;
import com.greatmancode.tools.caller.unittest.UnitTestServerCaller;
import com.greatmancode.tools.interfaces.UnitTestLoader;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class TestInitializator {
	public TestInitializator() {
        File file = new UnitTestServerCaller(new UnitTestLoader()).getDataFolder();
        for (File entry : file.listFiles()) {
            entry.delete();
        }
        file.delete();
        try {
            setStaticValue("com.greatmancode.craftconomy3.Common", "initialized", false);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        new Common().onEnable(new UnitTestServerCaller(new UnitTestLoader()), Logger.getLogger("unittest"));
        Common.getInstance().getMainConfig().setValue("System.QuickSetup.Enable", true);
        Common.getInstance().getMainConfig().setValue("System.Logging.Enabled", true);
        if (Boolean.getBoolean("mysql")) {
            Common.getInstance().getMainConfig().setValue("System.Database.Username", "travis");
            Common.getInstance().getMainConfig().setValue("System.Database.Type", "mysql");
            if (System.getProperty("MYSQL_USERNAME") != null) {
                Common.getInstance().getMainConfig().setValue("System.Database.Username", System.getProperty("MYSQL_USERNAME"));
            }
            if (System.getProperty("MYSQL_PASSWORD") != null) {
                Common.getInstance().getMainConfig().setValue("System.Database.Password", System.getProperty("MYSQL_PASSWORD"));
            }
            if (System.getProperty("MYSQL_DATABASE") != null) {
                Common.getInstance().getMainConfig().setValue("System.Database.Db", System.getProperty("MYSQL_DATABASE"));
            }
            setupMySQL();
        }
        try {
            setStaticValue("com.greatmancode.craftconomy3.Common", "initialized", false);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        new Common().onEnable(new UnitTestServerCaller(new UnitTestLoader()), Logger.getLogger("unittest"));
	}

    private void setupMySQL() {
        HikariConfig config = new HikariConfig();
        config.setMaximumPoolSize(10);
        config.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        config.addDataSourceProperty("serverName", Common.getInstance().getMainConfig().getString("System.Database.Address"));
        config.addDataSourceProperty("port", Common.getInstance().getMainConfig().getString("System.Database.Port"));
        config.addDataSourceProperty("databaseName", Common.getInstance().getMainConfig().getString("System.Database.Db"));
        config.addDataSourceProperty("user", Common.getInstance().getMainConfig().getString("System.Database.Username"));
        config.addDataSourceProperty("password", Common.getInstance().getMainConfig().getString("System.Database.Password"));
        config.addDataSourceProperty("autoDeserialize", true);
        config.setConnectionTimeout(5000);
        HikariDataSource db = new HikariDataSource(config);
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = db.getConnection();
            statement = connection.prepareStatement("DROP DATABASE IF EXISTS " +Common.getInstance().getMainConfig().getString("System.Database.Db"));
            statement.executeUpdate();
            statement.close();
            statement = connection.prepareStatement("CREATE DATABASE " + Common.getInstance().getMainConfig().getString("System.Database.Db"));
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        db.close();
    }

    /**
     * Use reflection to change value of any static field.
     * @param className The complete name of the class (ex. java.lang.String)
     * @param fieldName The name of a static field in the class
     * @param newValue The value you want the field to be set to.
     * @throws SecurityException .
     * @throws NoSuchFieldException .
     * @throws ClassNotFoundException .
     * @throws IllegalArgumentException .
     * @throws IllegalAccessException .
     */
    public static void setStaticValue(final String className, final String fieldName, final Object newValue) throws SecurityException, NoSuchFieldException,
            ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
        // Get the private String field
        final Field field = Class.forName(className).getDeclaredField(fieldName);
        // Allow modification on the field
        field.setAccessible(true);
        // Get
        final Object oldValue = field.get(Class.forName(className));
        // Sets the field to the new value
        field.set(oldValue, newValue);
    }
}
