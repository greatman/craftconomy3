/*
 * This file is part of Craftconomy3.
 *
 * Copyright (c) 2011-2012, Greatman <http://github.com/greatman/>
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
import com.greatmancode.craftconomy3.database.tables.CurrencyTable;

/**
 * Represents a currency
 * 
 * @author greatman
 * 
 */
public class Currency {

	private CurrencyTable entry = new CurrencyTable();

	public Currency(int databaseID, String name, String plural, String minor, String minorPlural, double hardCap, String sign) {
		entry.setName(name);
		entry.setPlural(plural);
		entry.setMinor(minor);
		entry.setMinorplural(minorPlural);
		entry.setId(databaseID);
		entry.setHardCap(hardCap);
		entry.setSign(sign);
	}

	/**
	 * Get the currency name
	 * 
	 * @return The currency Name
	 */
	public String getName() {
		return entry.getName();
	}

	/**
	 * Set the currency name
	 * 
	 * @param name The currency name to set to.
	 */
	public void setName(String name) {
		entry.setName(name);
		save();
	}

	/**
	 * Get the currency name in plural
	 * 
	 * @return The currency name in plural
	 */
	public String getPlural() {
		return entry.getPlural();
	}

	/**
	 * Set the currency name in plural
	 * 
	 * @param plural The currency name in plural to set to.
	 */
	public void setPlural(String plural) {
		entry.setPlural(plural);
		save();
	}

	/**
	 * Get the currency minor name
	 * 
	 * @return The currency minor name
	 */
	public String getMinor() {
		return entry.getMinor();
	}

	/**
	 * Set the currency minor name
	 * 
	 * @param minor The currency minor name to set to
	 */
	public void setMinor(String minor) {
		entry.setMinor(minor);
		save();
	}

	/**
	 * Get the currency minor name in plural
	 * 
	 * @return The currency minor name in plural
	 */
	public String getMinorPlural() {
		return entry.getMinorplural();
	}

	/**
	 * Set the currency minor name in plural
	 * 
	 * @param minorPlural The currency minor name in plural to set to
	 */
	public void setMinorPlural(String minorPlural) {
		entry.setMinorplural(minorPlural);
		save();
	}

	/**
	 * Retrieve the database ID of this currency
	 * 
	 * @return The database ID
	 */
	public int getDatabaseID() {
		return entry.getId();
	}

	/**
	 * Set the hard cap of the currency (NOT IMPLEMENTED)
	 * @param cap the hard cap.
	 */
	public void setHardCap(double cap) {
		entry.setHardCap(cap);
		save();
	}

	/**
	 * Retrieve the hard cap of the currency.
	 * @return The hard cap
	 */
	public double getHardCap() {
		return entry.getHardCap();
	}

	/**
	 * Sets the sign of the currency (Example $ for Dollars)
	 * 
	 * @param sign The Sign of the Currency.
	 */
	public void setSign(String sign) {
		entry.setSign(sign);
		save();
	}

	/**
	 * Retrieve the sign of the currency (Example $ for Dollars)
	 * 
	 * @return
	 */
	public String getSign() {
		return entry.getSign();
	}

	/**
	 * Save the currency information.
	 */
	private void save() {
		Common.getInstance().getDatabaseManager().getDatabase().save(entry);
	}

	/**
	 * Delete the currency from the database.
	 */
	void delete() {
		Common.getInstance().getDatabaseManager().getDatabase().remove(entry);
	}

	/**
	 * Set the default flag to true.
	 */
	void setDefault() {
		entry.setStatus(true);
		save();
	}

}
