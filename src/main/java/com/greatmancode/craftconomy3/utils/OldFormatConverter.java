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
package com.greatmancode.craftconomy3.utils;

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.storage.StorageEngine;
import com.greatmancode.craftconomy3.storage.sql.H2Engine;
import com.greatmancode.craftconomy3.storage.sql.MySQLEngine;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

public class OldFormatConverter {

    private HikariDataSource db;
    private String tablePrefix;

    public void run() throws SQLException, IOException {
        String dbType = Common.getInstance().getMainConfig().getString("System.Database.Type");
        HikariConfig config = new HikariConfig();
        if (dbType.equalsIgnoreCase("mysql")) {
            config.setMaximumPoolSize(10);
            config.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
            config.addDataSourceProperty("serverName", Common.getInstance().getMainConfig().getString("System.Database.Address"));
            config.addDataSourceProperty("port", Common.getInstance().getMainConfig().getString("System.Database.Port"));
            config.addDataSourceProperty("databaseName", Common.getInstance().getMainConfig().getString("System.Database.Db"));
            config.addDataSourceProperty("user", Common.getInstance().getMainConfig().getString("System.Database.Username"));
            config.addDataSourceProperty("password", Common.getInstance().getMainConfig().getString("System.Database.Password"));
            config.addDataSourceProperty("autoDeserialize", true);
            config.setConnectionTimeout(5000);
            db = new HikariDataSource(config);

        } else if (dbType.equalsIgnoreCase("sqlite")) {
            config.setDataSourceClassName("org.sqlite.SQLiteDataSource");
            config.setJdbcUrl("jdbc:sqlite:"+Common.getInstance().getServerCaller().getDataFolder() + "database.db");
            db = new HikariDataSource(config);
        } else {
            Common.getInstance().sendConsoleMessage(Level.SEVERE, "Unknown database type for old format converter!");
            return;
        }
        this.tablePrefix = Common.getInstance().getMainConfig().getString("System.Database.Prefix");

        File accountFile = new File(Common.getInstance().getServerCaller().getDataFolder(), "accounts.xml");

        Connection connection = db.getConnection();

        Common.getInstance().sendConsoleMessage(Level.INFO, "Doing a backup in a xml file before doing the conversion.");
        //Document setup
        JSONObject mainObject = new JSONObject();

        //Currencies
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + tablePrefix + "currency");
        ResultSet set = statement.executeQuery();
        JSONArray array = new JSONArray();
        while (set.next()) {
            JSONObject entry = new JSONObject();
            entry.put("id", set.getInt("id"));
            entry.put("name", set.getString("name"));
            entry.put("plural", set.getString("plural"));
            entry.put("minor", set.getString("minor"));
            entry.put("minorPlural", set.getString("minorPlural"));
            entry.put("sign", set.getString("sign"));
            entry.put("status", set.getBoolean("status"));
            array.add(entry);
        }
        statement.close();
        mainObject.put("currencies", array);

        //World groups
        array = new JSONArray();
        statement = connection.prepareStatement("SELECT * FROM " + tablePrefix + "worldgroup");
        set = statement.executeQuery();
        while (set.next()) {
            JSONObject entry = new JSONObject();
            entry.put("groupName", set.getString("groupName"));
            entry.put("worldList", set.getString("worldList"));
            array.add(entry);
        }
        statement.close();
        mainObject.put("worldgroups", array);

        //Exchange table
        array = new JSONArray();
        statement = connection.prepareStatement("SELECT * FROM " + tablePrefix + "exchange");
        set = statement.executeQuery();
        while (set.next()) {
            JSONObject entry = new JSONObject();
            entry.put("from_currency_id", set.getInt("from_currency_id"));
            entry.put("to_currency_id", set.getInt("to_currency_id"));
            entry.put("amount", set.getDouble("amount"));
            array.add(entry);
        }
        statement.close();
        mainObject.put("exchanges", array);

        //config table
        array = new JSONArray();
        statement = connection.prepareStatement("SELECT * FROM " + tablePrefix + "config");
        set = statement.executeQuery();
        while (set.next()) {
            JSONObject entry = new JSONObject();
            entry.put("name", set.getString("name"));
            entry.put("value", set.getString("value"));
            array.add(entry);
        }
        statement.close();
        mainObject.put("configs", array);

        //account table
        array = new JSONArray();
        statement = connection.prepareStatement("SELECT * FROM " + tablePrefix + "account");
        set = statement.executeQuery();
        while (set.next()) {
            JSONObject entry = new JSONObject();
            entry.put("name", set.getString("name"));
            entry.put("infiniteMoney", set.getBoolean("infiniteMoney"));
            entry.put("ignoreACL", set.getBoolean("ignoreACL"));
            entry.put("uuid", set.getString("uuid"));

            JSONArray balanceArray = new JSONArray();
            PreparedStatement internalStatement = connection.prepareStatement("SELECT * FROM " + tablePrefix + "balance WHERE username_id=?");
            internalStatement.setInt(1, set.getInt("id"));
            ResultSet internalSet = internalStatement.executeQuery();
            while (internalSet.next()) {
                JSONObject object = new JSONObject();
                object.put("currency_id", internalSet.getInt("currency_id"));
                object.put("worldName", internalSet.getString("worldName"));
                object.put("balance", internalSet.getDouble("balance"));
                balanceArray.add(object);
            }
            internalStatement.close();
            entry.put("balances", balanceArray);

            internalStatement = connection.prepareStatement("SELECT * FROM " + tablePrefix +"log WHERE username_id=?");
            internalStatement.setInt(1, set.getInt("id"));
            internalSet = internalStatement.executeQuery();
            JSONArray logArray = new JSONArray();
            while(internalSet.next()) {
                JSONObject object = new JSONObject();
                object.put("type", internalSet.getObject("type"));
                object.put("cause", internalSet.getObject("cause"));
                object.put("timestamp", internalSet.getTimestamp("timestamp"));
                object.put("causeReason", internalSet.getString("causeReason"));
                object.put("currencyName", internalSet.getString("currencyName"));
                object.put("worldName", internalSet.getString("worldName"));
                logArray.add(object);
            }
            internalStatement.close();
            entry.put("logs", logArray);

            internalStatement = connection.prepareStatement("SELECT * FROM " + tablePrefix +"acl WHERE account_id=?");
            internalStatement.setInt(1, set.getInt("id"));
            internalSet = internalStatement.executeQuery();
            JSONArray aclArray = new JSONArray();
            while(internalSet.next()) {
                JSONObject object = new JSONObject();
                object.put("playerName", internalSet.getString("playerName"));
                object.put("deposit", internalSet.getBoolean("deposit"));
                object.put("withdraw", internalSet.getBoolean("withdraw"));
                object.put("acl", internalSet.getBoolean("acl"));
                object.put("balance", internalSet.getBoolean("balance"));
                object.put("owner", internalSet.getBoolean("owner"));
                aclArray.add(object);

            }
            internalStatement.close();
            entry.put("acls", aclArray);
            array.add(entry);
        }
        statement.close();
        mainObject.put("accounts", array);
        FileWriter writer = new FileWriter(new File(Common.getInstance().getServerCaller().getDataFolder(), "accounts.json"));
        mainObject.writeJSONString(writer);
        //The backup is now saved. Let's drop everything
        /*statement = connection.prepareStatement("DROP TABLE " + tablePrefix + "config");
        statement.execute();
        statement.close();
        statement = connection.prepareStatement("DROP TABLE " + tablePrefix + "acl");
        statement.execute();
        statement.close();
        statement = connection.prepareStatement("DROP TABLE " + tablePrefix + "balance");
        statement.execute();
        statement.close();
        statement = connection.prepareStatement("DROP TABLE " + tablePrefix + "log");
        statement.execute();
        statement.close();
        statement = connection.prepareStatement("DROP TABLE " + tablePrefix + "worldgroup");
        statement.execute();
        statement.close();
        statement = connection.prepareStatement("DROP TABLE " + tablePrefix + "access");
        statement.execute();
        statement.close();
        statement = connection.prepareStatement("DROP TABLE " + tablePrefix + "account");
        statement.execute();
        statement.close();
        statement = connection.prepareStatement("DROP TABLE " + tablePrefix + "currency");
        statement.execute();
        statement.close();

        connection.close();
        step2();*/

    }

    private void step2() throws SQLException {
        String dbType = Common.getInstance().getMainConfig().getString("System.Database.Type");

        if (dbType.equals("sqlite")) {
            Common.getInstance().getMainConfig().setValue("System.Database.Type", "h2");
            dbType = "h2";
        }
        StorageEngine engine = null;
        HikariConfig config = new HikariConfig();
        if (dbType.equalsIgnoreCase("mysql")) {
            engine = new MySQLEngine();

        } else if (dbType.equalsIgnoreCase("h2")) {
            engine = new H2Engine();
        }
        File accountFile = new File(Common.getInstance().getServerCaller().getDataFolder(), "accounts.json");
    }
}
