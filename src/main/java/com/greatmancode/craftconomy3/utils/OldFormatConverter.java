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
package com.greatmancode.craftconomy3.utils;

import com.greatmancode.craftconomy3.Cause;
import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.LogInfo;
import com.greatmancode.craftconomy3.account.Account;
import com.greatmancode.craftconomy3.currency.Currency;
import com.greatmancode.craftconomy3.storage.StorageEngine;
import com.greatmancode.craftconomy3.storage.sql.H2Engine;
import com.greatmancode.craftconomy3.storage.sql.MySQLEngine;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class OldFormatConverter {

    private HikariDataSource db;
    private String tablePrefix;

    public void run() throws SQLException, IOException, ParseException {
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
            config.setDriverClassName("org.sqlite.JDBC");
            config.setJdbcUrl("jdbc:sqlite:"+ Common.getInstance().getServerCaller().getDataFolder() + File.separator +  "database.db");
            config.setConnectionTestQuery("SELECT 1");
            db = new HikariDataSource(config);
        } else {
            Common.getInstance().sendConsoleMessage(Level.SEVERE, "Unknown database type for old format converter!");
            return;
        }
        Connection connection = db.getConnection();
        this.tablePrefix = Common.getInstance().getMainConfig().getString("System.Database.Prefix");

        File accountFile = new File(Common.getInstance().getServerCaller().getDataFolder(), "accounts.json");



        Common.getInstance().sendConsoleMessage(Level.INFO, "Doing a backup in a xml file before doing the conversion.");
        //Document setup
        JSONObject mainObject = new JSONObject();

        Common.getInstance().sendConsoleMessage(Level.INFO, "Saving currency table");
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
        Common.getInstance().sendConsoleMessage(Level.INFO, "Saving world group table");
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
        Common.getInstance().sendConsoleMessage(Level.INFO, "Saving exchange table");
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
        Common.getInstance().sendConsoleMessage(Level.INFO, "Saving config table");
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
        Common.getInstance().sendConsoleMessage(Level.INFO, "Saving account table");
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
                object.put("type", getObject(internalSet.getBlob("type" )).toString());
                object.put("cause", getObject(internalSet.getBlob("cause")).toString());
                object.put("timestamp", internalSet.getTimestamp("timestamp").getTime());
                object.put("causeReason", internalSet.getString("causeReason"));
                object.put("currencyName", internalSet.getString("currencyName"));
                object.put("worldName", internalSet.getString("worldName"));
                object.put("amount", internalSet.getDouble("amount"));
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
        Common.getInstance().sendConsoleMessage(Level.INFO, "Writing json file");
        FileWriter writer = new FileWriter(accountFile);
        writer.write(mainObject.toJSONString());
        writer.flush();
        writer.close();
        Common.getInstance().sendConsoleMessage(Level.INFO, "File written! Dropping all tables");
        //The backup is now saved. Let's drop everything
        statement = connection.prepareStatement("DROP TABLE " + tablePrefix + "config");
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
        statement = connection.prepareStatement("DROP TABLE " + tablePrefix + "exchange");
        statement.execute();
        statement.close();
        statement = connection.prepareStatement("DROP TABLE " + tablePrefix + "account");
        statement.execute();
        statement.close();
        statement = connection.prepareStatement("DROP TABLE " + tablePrefix + "currency");
        statement.execute();
        statement.close();
        statement = connection.prepareStatement("DROP TABLE " + tablePrefix + "payday");
        statement.execute();
        statement.close();

        connection.close();
        step2();

    }

    public void step2() throws SQLException, IOException, ParseException {
        Common.getInstance().sendConsoleMessage(Level.INFO, "Converting step 2: Inserting the data back in all the new tables");
        Common.getInstance().sendConsoleMessage(Level.INFO, "Creating the new tables");
        String dbType = Common.getInstance().getMainConfig().getString("System.Database.Type");

        if (dbType.equals("sqlite")) {
            Common.getInstance().sendConsoleMessage(Level.INFO, "You are using SQLite! This is now deprecated. Selecting H2 instead.");
            Common.getInstance().getMainConfig().setValue("System.Database.Type", "h2");
            dbType = "h2";
        }
        StorageEngine engine = null;
        HikariConfig config = new HikariConfig();
        if (dbType.equalsIgnoreCase("mysql")) {
            engine = new MySQLEngine();

        } else if (dbType.equalsIgnoreCase("h2")) {
            engine = new H2Engine();
        } else {
            throw new UnsupportedOperationException("Unknown database!");
        }
        engine.disableAutoCommit();
        Common.getInstance().sendConsoleMessage(Level.INFO, "Loading backup json file");
        File accountFile = new File(Common.getInstance().getServerCaller().getDataFolder(), "accounts.json");

        System.out.println(accountFile.exists());

        JSONObject jsonObject = (JSONObject) new JSONParser().parse(new FileReader(accountFile));
        Map<Integer, String> currenciesMap = new HashMap<>();

        Common.getInstance().sendConsoleMessage(Level.INFO,"Saving currencies");
        //Create the currency table
        JSONArray currencyArray = (JSONArray) jsonObject.get("currencies");
        Iterator<JSONObject> iterator = currencyArray.iterator();
        while (iterator.hasNext()) {
            JSONObject obj = iterator.next();
            currenciesMap.put(((Long) obj.get("id")).intValue(), (String)obj.get("name"));
            Currency currency = new Currency((String)obj.get("name"), (String)obj.get("plural"), (String)obj.get("minor"), (String)obj.get("minorPlural"), (String)obj.get("sign"));
            try {
                Class clazz = currency.getClass();
                Method setDefault = clazz.getDeclaredMethod("setDefault", boolean.class);
                setDefault.setAccessible(true);
                setDefault.invoke(currency, (boolean) obj.get("status"));
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
            engine.saveCurrency("", currency);
        }

        Common.getInstance().sendConsoleMessage(Level.INFO, "Saving world groups...");
        JSONArray worldgroup = (JSONArray) jsonObject.get("worldgroups");
        iterator = worldgroup.iterator();
        while (iterator.hasNext()) {
            JSONObject obj = iterator.next();
            engine.saveWorldGroup((String)obj.get("groupName"), (String)obj.get("worldList"));
        }

        Common.getInstance().sendConsoleMessage(Level.INFO, "Saving Exchange rates");
        JSONArray exchangeArray = (JSONArray) jsonObject.get("exchanges");
        iterator = exchangeArray.iterator();
        while (iterator.hasNext()) {
            JSONObject obj = iterator.next();
            int id_from = ((Long) obj.get("from_currency_id")).intValue();
            int id_to = ((Long) obj.get("to_currency_id")).intValue();
            engine.setExchangeRate(engine.getCurrency(currenciesMap.get(id_from)), engine.getCurrency(currenciesMap.get(id_to)), (double)obj.get("amount"));
        }

        Common.getInstance().sendConsoleMessage(Level.INFO, "Saving configs");
        JSONArray configArray = (JSONArray) jsonObject.get("configs");
        iterator = configArray.iterator();
        while (iterator.hasNext()) {
            JSONObject obj = iterator.next();
            if (!obj.get("name").equals("dbVersion")) {
                engine.setConfigEntry((String)obj.get("name"), (String)obj.get("value"));
            }
        }

        Common.getInstance().sendConsoleMessage(Level.INFO, "Saving accounts. This may take a long time.");
        boolean log = false;
        if (Common.getInstance().getMainConfig().getBoolean("System.Logging.Enabled")) {
            Common.getInstance().getMainConfig().setValue("System.Logging.Enabled", false);
            log = true;
        }
        JSONArray accounts = (JSONArray) jsonObject.get("accounts");
        iterator = accounts.iterator();
        while (iterator.hasNext()) {
            JSONObject obj = iterator.next();
            String name = (String) obj.get("name");
            Account account = null;
            if (name.startsWith("bank:")) {
                account = engine.getAccount(name.split(":")[1], true, false);
            } else {
                account = engine.getAccount(name, false, false);
            }
            engine.setIgnoreACL(account, (Boolean) obj.get("ignoreACL"));
            engine.setInfiniteMoney(account, (Boolean) obj.get("infiniteMoney"));
            if (obj.get("uuid") != null) {
                engine.updateUUID(account.getAccountName(), UUID.fromString((String) obj.get("uuid")));
            }

            JSONArray balances = (JSONArray) obj.get("balances");
            Iterator<JSONObject> internalIterator = balances.iterator();
            while (internalIterator.hasNext()) {
                JSONObject internalObj = internalIterator.next();
                Currency currency = engine.getCurrency(currenciesMap.get(((Long) internalObj.get("currency_id")).intValue()));
                if (currency != null) {
                    engine.setBalance(account, (double)internalObj.get("balance"), currency, (String)internalObj.get("worldName"));
                }
            }

            JSONArray logs = (JSONArray) obj.get("logs");
            internalIterator = logs.iterator();
            while (internalIterator.hasNext()) {
                JSONObject internalObj = internalIterator.next();
                engine.saveLog(LogInfo.valueOf((String) internalObj.get("type")), Cause.valueOf((String) internalObj.get("cause")),(String)internalObj.get("causeReason"),account, (double)internalObj.get("amount"),engine.getCurrency((String) internalObj.get("currencyName")),(String)internalObj.get("worldName"), new Timestamp((long)internalObj.get("timestamp")));
            }

            JSONArray acls = (JSONArray) obj.get("acls");
            internalIterator = acls.iterator();
            while (internalIterator.hasNext()) {
                JSONObject internalObj = internalIterator.next();
                engine.saveACL(account, (String)internalObj.get("playerName"), (boolean)internalObj.get("deposit"), (boolean)internalObj.get("withdraw"), (boolean) internalObj.get("acl"), (boolean) internalObj.get("balance"), (boolean) internalObj.get("owner"));
            }

        }
        engine.commit();
        engine.enableAutoCommit();
        if (log) {
            Common.getInstance().getMainConfig().setValue("System.Logging.Enabled", true);
        }
        Common.getInstance().sendConsoleMessage(Level.INFO, "Converting done!");
    }

    private Object getObject(Blob blob) throws SQLException {
        try
        {
            ObjectInputStream is = null;
            is = new ObjectInputStream(blob.getBinaryStream());
            Object o = null;
            try {
                return is.readObject();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (Exception ignored) {
                }
            }
        } catch ( IOException e )
        {
            e.printStackTrace();
        }
        return null;
    }
}
