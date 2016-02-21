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
import com.greatmancode.craftconomy3.utils.NoExchangeRate;

/**
 * Represents a currency
 *
 * @author greatman
 */
public class Currency {
    private String name, plural, minor, minorPlural, sign;
    private boolean status, bankCurrency;

    /**
     * Initialize a currency
     *
     * @param name        The name of the currency
     * @param plural      The plural name of the currency
     * @param minor       The minor name of the currency
     * @param minorPlural The plural minor name of the currency.
     * @param sign        The sign of the currency. (Example: $)
     */
    public Currency(String name, String plural, String minor, String minorPlural, String sign) {
        this(name, plural, minor, minorPlural, sign, false);
    }

    public Currency(String name, String plural, String minor, String minorPlural, String sign, boolean status) {
        this(name,plural,minor,minorPlural,sign,status,false);
    }

    public Currency(String name, String plural, String minor, String minorPlural, String sign, boolean status, boolean bankCurrency) {
        this.name = name;
        this.plural = plural;
        this.minor = minor;
        this.minorPlural = minorPlural;
        this.sign = sign;
        this.status = status;
        this.bankCurrency = bankCurrency;
    }



    /**
     * Get the currency name
     *
     * @return The currency Name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the currency name
     *
     * @param name The currency name to set to.
     */
    public void setName(String name) {
        String oldname = this.name;
        this.name = name;
        //TODO Reset the main map
        save(oldname);
    }

    /**
     * Get the currency name in plural
     *
     * @return The currency name in plural
     */
    public String getPlural() {
        return plural;
    }

    /**
     * Set the currency name in plural
     *
     * @param plural The currency name in plural to set to.
     */
    public void setPlural(String plural) {
        this.plural = plural;
        save();
    }

    /**
     * Get the currency minor name
     *
     * @return The currency minor name
     */
    public String getMinor() {
        return minor;
    }

    /**
     * Set the currency minor name
     *
     * @param minor The currency minor name to set to
     */
    public void setMinor(String minor) {
        this.minor = minor;
        save();
    }

    /**
     * Get the currency minor name in plural
     *
     * @return The currency minor name in plural
     */
    public String getMinorPlural() {
        return minorPlural;
    }

    /**
     * Set the currency minor name in plural
     *
     * @param minorPlural The currency minor name in plural to set to
     */
    public void setMinorPlural(String minorPlural) {
        this.minorPlural = minorPlural;
        save();
    }

    /**
     * Sets the sign of the currency (Example $ for Dollars)
     *
     * @param sign The Sign of the Currency.
     */
    public void setSign(String sign) {
        this.sign = sign;
        save();
    }

    /**
     * Retrieve the sign of the currency (Example $ for Dollars)
     *
     * @return The sign.
     */
    public String getSign() {
        return sign;
    }

    /**
     * Returns the exchange rate between 2 currency.
     *
     * @param otherCurrency The other currency to exchange to
     * @return The exchange rate or Double.MIN_VALUE if no exchange information are found.
     * @throws com.greatmancode.craftconomy3.utils.NoExchangeRate If there's no exchange rate between the 2 currencies.
     */
    public double getExchangeRate(Currency otherCurrency) throws NoExchangeRate {
        return Common.getInstance().getStorageHandler().getStorageEngine().getExchangeRate(this, otherCurrency);
    }

    /**
     * Set the exchange rate between 2 currency
     *
     * @param otherCurrency The other currency
     * @param amount        THe exchange rate.
     */
    public void setExchangeRate(Currency otherCurrency, double amount) {
        Common.getInstance().getStorageHandler().getStorageEngine().setExchangeRate(this, otherCurrency, amount);
    }

    /**
     * Save the currency information.
     */
    private void save() {
        save(getName());
    }

    /**
     * Save the currency information. Used while changing the main currency name.
     * @param oldName The old currency name.
     */
    private void save(String oldName) {
        Common.getInstance().getStorageHandler().getStorageEngine().saveCurrency(oldName, this);
        Common.getInstance().getCurrencyManager().updateEntry(oldName, this);
    }

    /**
     * Delete the currency from the database.
     */
    void delete() {
        Common.getInstance().getStorageHandler().getStorageEngine().deleteCurrency(this);
    }

    /**
     * Set the default flag to true.
     * @param status If this currency is the default one or not
     */
    protected void setDefault(boolean status) {
        this.status = status;
    }

    public boolean getStatus() {
        return status;
    }

    public boolean isPrimaryBankCurrency() {
        return bankCurrency;
    }

    protected void setBankCurrency(boolean bankCurrency) {
        this.bankCurrency = bankCurrency;
    }

    @Override
    public String toString() {
        return "Currency{" +
                "name='" + name + '\'' +
                ", plural='" + plural + '\'' +
                ", minor='" + minor + '\'' +
                ", minorPlural='" + minorPlural + '\'' +
                ", sign='" + sign + '\'' +
                ", status=" + status +
                ", bankCurrency=" + bankCurrency +
                '}';
    }
}
