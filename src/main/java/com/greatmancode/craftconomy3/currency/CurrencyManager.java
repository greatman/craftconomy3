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
package com.greatmancode.craftconomy3.currency;

import com.greatmancode.craftconomy3.Common;

import java.util.*;

/**
 * Currency Handler
 *
 * @author greatman
 */
public class CurrencyManager {
    /**
     * The default currency database ID
     */
    private static Currency defaultCurrency;
    private static Currency defaultBankCurrency;
    private final Map<String, Currency> currencyList;

    public CurrencyManager() {
        // Let's load all currency in the database
        currencyList = Common.getInstance().getStorageHandler().getStorageEngine().getAllCurrencies();
        for (Map.Entry<String, Currency> currencyEntry : currencyList.entrySet()) {
            if (currencyEntry.getValue().getStatus()) {
                defaultCurrency = currencyEntry.getValue();
            }
            if (currencyEntry.getValue().isPrimaryBankCurrency()) {
                defaultBankCurrency = currencyEntry.getValue();
            }
        }
        Common.getInstance().addMetricsGraph("NumberCurrency", currencyList.size() + "");
    }

    /**
     * Get a currency
     *
     * @param name The name of the currency
     * @return A currency instance if the currency is found else null
     */
    public Currency getCurrency(String name) {
        Currency result;
        if (!currencyList.containsKey(name)) {
            result = Common.getInstance().getStorageHandler().getStorageEngine().getCurrency(name);
            if (result != null) {
                currencyList.put(result.getName(), result);
            }
        } else {
            result = currencyList.get(name);
        }
        return result;
    }

    /**
     * Get the list of currency in the system
     *
     * @return A list of all the currency in the system
     */
    public List<String> getCurrencyNames() {
        return Common.getInstance().getStorageHandler().getStorageEngine().getAllCurrencyNames();
    }

    /**
     * Add a currency in the system
     *
     * @param name        The main currency name
     * @param plural      The main currency name in plural
     * @param minor       The minor (cents) part of the currency
     * @param minorPlural The minor (cents) part of the currency in plural
     * @param sign        The sign of the currency
     * @param save        Do we add it in the database?
     * @return a Currency instance
     */
    // TODO: A check if the currency already exist.
    public Currency addCurrency(String name, String plural, String minor, String minorPlural, String sign, boolean save) {
        return addCurrency(name, plural, minor, minorPlural, sign, save, false);
    }


    private Currency addCurrency(String name, String plural, String minor, String minorPlural, String sign, boolean save, boolean status) {
        Currency currency = new Currency(name, plural, minor, minorPlural, sign, status);
        if (save) {
            Common.getInstance().getStorageHandler().getStorageEngine().saveCurrency(name, currency);
        }
        currencyList.put(currency.getName(), currency);
        return currency;
    }

    /**
     * Set a currency as the default one.
     *
     * @param currency The currency to set to default
     */
    public void setDefault(Currency currency) {
        if (currencyList.containsKey(currency.getName())) {
            Common.getInstance().getStorageHandler().getStorageEngine().setDefaultCurrency(currency);
            defaultCurrency = currency;
            currency.setDefault(true);
            for (Map.Entry<String, Currency> currencyEntry : currencyList.entrySet()) {
                if (!currencyEntry.getValue().equals(currency)) {
                    currency.setDefault(false);
                }
            }
        }
    }

    /**
     * Delete a currency.
     *
     * @param currency The currency to delete
     */
    public void deleteCurrency(Currency currency) {
        if (currencyList.containsKey(currency.getName())) {
            Common.getInstance().getStorageHandler().getStorageEngine().deleteCurrency(currency);
            currencyList.remove(currency.getName());
        }
    }

    /**
     * Retrieve the default currency
     *
     * @return The default currency
     */
    public Currency getDefaultCurrency() {
        return defaultCurrency;
    }

    protected void updateEntry(String oldName, Currency currency) {
        currencyList.remove(oldName);
        currencyList.put(currency.getName(), currency);
    }

    public void setDefaultBankCurrency(Currency currency) {
        if (currencyList.containsKey(currency.getName())) {
            Common.getInstance().getStorageHandler().getStorageEngine().setDefaultBankCurrency(currency);
            defaultBankCurrency = currency;
            currency.setBankCurrency(true);
            for (Map.Entry<String, Currency> currencyEntry : currencyList.entrySet()) {
                if (!currencyEntry.getValue().equals(currency)) {
                    currency.setBankCurrency(false);
                }
            }
        }
    }

    public Currency getDefaultBankCurrency() {
        return defaultBankCurrency;
    }
}
