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
package com.greatmancode.craftconomy3.currency;

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.database.tables.BalanceTable;
import com.greatmancode.craftconomy3.database.tables.CurrencyTable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Currency Handler
 *
 * @author greatman
 */
public class CurrencyManager {
    /**
     * The default currency database ID
     */
    private static int defaultCurrencyID;
    private final Map<Integer, Currency> currencyList = new HashMap<Integer, Currency>();

    public CurrencyManager() {
        // Let's load all currency in the database
        try {
            Connection connection = Common.getInstance().getDatabaseManager().getDatabase().getConnection();
            PreparedStatement statement = connection.prepareStatement(CurrencyTable.SELECT_ALL_ENTRY);
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                if (set.getBoolean("status")) {
                    defaultCurrencyID = set.getInt("id");
                }
                addCurrency(set.getInt("id"), set.getString("name"), set.getString("plural"), set.getString("minor"), set.getString("minorPlural"), set.getDouble("hardCap"), set.getString("sign"), false, set.getBoolean("status"));
            }
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get a currency
     *
     * @param id The Database ID
     * @return A Currency instance if the currency is found else null
     */
    public Currency getCurrency(int id) {
        Currency result = null;
        if (currencyList.containsKey(id)) {
            result = currencyList.get(id);
        }
        return result;
    }

    /**
     * Get a currency
     *
     * @param name The name of the currency
     * @return A currency instance if the currency is found else null
     */
    public Currency getCurrency(String name) {
        Currency result = null;
        CurrencyTable dbResult = Common.getInstance().getDatabaseManager().getDatabase().select(CurrencyTable.class).where().equal("name", name).execute().findOne();
        if (dbResult != null) {
            result = getCurrency(dbResult.getId());
        }
        return result;
    }

    /**
     * Get the list of currency in the system
     *
     * @return A list of all the currency in the system
     */
    public List<String> getCurrencyNames() {
        List<String> list = new ArrayList<String>();
        for (CurrencyTable currency : Common.getInstance().getDatabaseManager().getDatabase().select(CurrencyTable.class).execute().find()) {
            list.add(currency.getName());
        }
        return list;
    }

    /**
     * Add a currency in the system
     *
     * @param name        The main currency name
     * @param plural      The main currency name in plural
     * @param minor       The minor (cents) part of the currency
     * @param minorPlural The minor (cents) part of the currency in plural
     * @param hardCap     The hardcap of the plugin (Unused)
     * @param sign        The sign of the currency
     * @param save        Do we add it in the database?
     */
    public void addCurrency(String name, String plural, String minor, String minorPlural, double hardCap, String sign, boolean save) {
        addCurrency(-1, name, plural, minor, minorPlural, hardCap, sign, save);
    }

    /**
     * Add a currency in the system
     *
     * @param databaseID  The database ID
     * @param name        The main currency name
     * @param plural      The main currency name in plural
     * @param minor       The minor (cents) part of the currency
     * @param minorPlural The minor (cents) part of the currency in plural
     * @param hardCap     The hardcap of the plugin (Unused)
     * @param sign        The sign of the currency
     * @param save        Do we add it in the database?
     */
    // TODO: A check if the currency already exist.
    public void addCurrency(int databaseID, String name, String plural, String minor, String minorPlural, double hardCap, String sign, boolean save) {
        addCurrency(databaseID, name, plural, minor, minorPlural, hardCap, sign, save, false);
    }


    private void addCurrency(int databaseID, String name, String plural, String minor, String minorPlural, double hardCap, String sign, boolean save, boolean status) {
        int newDatabaseID = databaseID;
        if (save) {
            CurrencyTable entry = new CurrencyTable();
            entry.setMinor(minor);
            entry.setMinorplural(minorPlural);
            entry.setName(name);
            entry.setPlural(plural);
            entry.setHardCap(hardCap);
            entry.setSign(sign);
            entry.setStatus(status);
            Common.getInstance().getDatabaseManager().getDatabase().save(entry);
            newDatabaseID = entry.getId();
        }
        currencyList.put(newDatabaseID, new Currency(newDatabaseID, name, plural, minor, minorPlural, hardCap, sign, status));
    }

    /**
     * Set a currency as the default one.
     *
     * @param currencyId The default currency ID.
     */
    public void setDefault(Currency currency) {
        if (currencyList.containsKey(currencyId)) {
            CurrencyTable entry = Common.getInstance().getDatabaseManager().getDatabase().select(CurrencyTable.class).where().equal("status", true).execute().findOne();
            if (entry != null) {
                entry.setStatus(false);
                Common.getInstance().getDatabaseManager().getDatabase().save(entry);
            }
            currencyList.get(currencyId).setDefault();
            defaultCurrencyID = currencyId;
        }
    }

    /**
     * Delete a currency.
     *
     * @param currencyId The currency ID to delete.
     */
    public void deleteCurrency(Currency currency) {
        if (currencyList.containsKey(currencyId)) {
            List<BalanceTable> balanceList = Common.getInstance().getDatabaseManager().getDatabase().select(BalanceTable.class).where().equal("currency_id", currencyId).execute().find();
            if (balanceList != null) {
                for (BalanceTable aBalanceList : balanceList) {
                    Common.getInstance().getDatabaseManager().getDatabase().remove(aBalanceList);
                }
            }
            currencyList.get(currencyId).delete();
            currencyList.remove(currencyId);
        }
    }

    /**
     * Retrieve the default currency
     *
     * @return The default currency
     */
    public Currency getDefaultCurrency() {
        return getCurrency(defaultCurrencyID);
    }
}
