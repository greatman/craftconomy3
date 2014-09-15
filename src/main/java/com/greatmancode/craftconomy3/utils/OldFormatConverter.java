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
import com.greatmancode.tools.configuration.Config;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

public class OldFormatConverter {

    private HikariDataSource db;
    private String tablePrefix;

    public void run() throws SQLException {
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

        Config accountFile = Common.getInstance().getConfigurationManager().loadFile(Common.getInstance().getServerCaller().getDataFolder(), "accounts.yml");

        Connection connection = db.getConnection();

        Common.getInstance().sendConsoleMessage(Level.INFO, "Doing a backup in a xml file before doing the conversion.");
        //Document setup
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = null;
        try {
            docBuilder = docFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("craftconomy");
        doc.appendChild(rootElement);

        //Currencies
        Element currenciesElement = doc.createElement("currencies");
        rootElement.appendChild(currenciesElement);
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + tablePrefix + "currency");
        ResultSet set = statement.executeQuery();
        while (set.next()) {
            Element currency = doc.createElement("currency");
            currenciesElement.appendChild(currency);
            Element id = doc.createElement("id");
            Element name = doc.createElement("name");
            Element namePlural = doc.createElement("namePlural");
            Element minor = doc.createElement("minor");
            Element minorPlural = doc.createElement("minorPlural");
            Element sign = doc.createElement("sign");
            Element defaultCurrency = doc.createElement("defaultCurrency");
            id.appendChild(doc.createTextNode(set.getInt("id") + ""));
            name.appendChild(doc.createTextNode(set.getString("name")));
            namePlural.appendChild(doc.createTextNode(set.getString("namePlural")));
            minor.appendChild(doc.createTextNode(set.getString("minor")));
            minorPlural.appendChild(doc.createTextNode(set.getString("minorPlural")));
            sign.appendChild(doc.createTextNode(set.getString("sign")));
            defaultCurrency.appendChild(doc.createTextNode(Boolean.toString(set.getBoolean("status"))));
            currency.appendChild(id);
            currency.appendChild(name);
            currency.appendChild(namePlural);
            currency.appendChild(minor);
            currency.appendChild(minorPlural);
            currency.appendChild(sign);
            currency.appendChild(defaultCurrency);
            currenciesElement.appendChild(currency);
        }
        statement.close();
        rootElement.appendChild(currenciesElement);

        //World groups
        Element worldGroupElement = doc.createElement("worldgroups");
        statement = connection.prepareStatement("SELECT * FROM " + tablePrefix + "worldgroup");
        set = statement.executeQuery();
        while (set.next()) {
            Element worldgroup = doc.createElement("worldgroup");
            Element groupName = doc.createElement("groupName");
            Element worldList = doc.createElement("worldList");
            groupName.appendChild(doc.createTextNode(set.getString("groupName")));
            worldList.appendChild(doc.createTextNode(set.getString("worldList")));
            worldgroup.appendChild(groupName);
            worldgroup.appendChild(worldList);
            worldGroupElement.appendChild(worldgroup);
        }
        statement.close();
        rootElement.appendChild(worldGroupElement);

        //Exchange table
        Element exchangeElement = doc.createElement("exchanges");
        statement = connection.prepareStatement("SELECT * FROM " + tablePrefix + "exchange");
        set = statement.executeQuery();
        while (set.next()) {
            Element exchange = doc.createElement("exchange");
            Element fromCurrency = doc.createElement("fromCurrency");
            Element toCurrency = doc.createElement("toCurrency");
            Element amount = doc.createElement("amount");
            fromCurrency.appendChild(doc.createTextNode(set.getInt("from_currency_id") + ""));
            toCurrency.appendChild(doc.createTextNode(set.getInt("to_currency_id") + ""));
            amount.appendChild(doc.createTextNode(set.getDouble("amount") + ""));
            exchange.appendChild(fromCurrency);
            exchange.appendChild(toCurrency);
            exchange.appendChild(amount);
            exchangeElement.appendChild(exchange);
        }
        statement.close();
        rootElement.appendChild(exchangeElement);

        //config table
        Element configElement = doc.createElement("configs");
        statement = connection.prepareStatement("SELECT * FROM " + tablePrefix + "config");
        set = statement.executeQuery();
        while (set.next()) {
            Element configEntry = doc.createElement("config");
            Element name = doc.createElement("name");
            Element value = doc.createElement("value");
            name.appendChild(doc.createTextNode(set.getString("name")));
            value.appendChild(doc.createTextNode(set.getString("value")));
            configEntry.appendChild(name);
            configEntry.appendChild(value);
            configElement.appendChild(configEntry);
        }
        statement.close();
        rootElement.appendChild(configElement);

        //account table
        Element accountElement = doc.createElement("accounts");
        statement = connection.prepareStatement("SELECT * FROM " + tablePrefix + "account");
        set = statement.executeQuery();
        while (set.next()) {
            Element accountEntry = doc.createElement("config");
            Element name = doc.createElement("name");
            Element infiniteMoney = doc.createElement("infiniteMoney");
            Element ignoreACL = doc.createElement("ignoreACL");
            Element uuid = doc.createElement("uuid");
            name.appendChild(doc.createTextNode(set.getString("name")));
            infiniteMoney.appendChild(doc.createTextNode(set.getBoolean("infiniteMoney") + ""));
            ignoreACL.appendChild(doc.createTextNode(set.getBoolean("ignoreACL") + ""));
            uuid.appendChild(doc.createTextNode(set.getString("uuid")));
            accountEntry.appendChild(name).appendChild(infiniteMoney).appendChild(ignoreACL).appendChild(uuid);
            PreparedStatement internalStatement = connection.prepareStatement("SELECT * FROM " + tablePrefix + "balance WHERE username_id=?");
            internalStatement.setInt(1, set.getInt("id"));
            ResultSet internalSet = internalStatement.executeQuery();
            Element balanceElement = doc.createElement("balances");
            while (internalSet.next()) {
                Element balanceEntry = doc.createElement("balance");
                Element currency_id = doc.createElement("currency_id");
                Element worldName = doc.createElement("worldName");
                Element balance = doc.createElement("balance");
                currency_id.appendChild(doc.createTextNode(internalSet.getInt("currency_id") + ""));
                worldName.appendChild(doc.createTextNode(internalSet.getString("worldName")));
                balance.appendChild(doc.createTextNode(internalSet.getDouble("balance") + ""));
                balanceEntry.appendChild(currency_id).appendChild(worldName).appendChild(balance);
                balanceElement.appendChild(balanceEntry);
            }
            internalSet.close();

            internalStatement = connection.prepareStatement("SELECT * FROM " + tablePrefix +"log WHERE username_id=?");
            internalStatement.setInt(1, set.getInt("id"));
            internalSet = internalStatement.executeQuery();
            
            accountElement.appendChild(accountEntry);
        }
        statement.close();
        rootElement.appendChild(accountElement);

    }
}
