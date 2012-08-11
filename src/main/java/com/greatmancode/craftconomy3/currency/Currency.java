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
 * @author greatman
 * 
 */
public class Currency {

	private CurrencyTable entry = new CurrencyTable();

	public Currency(int databaseID, String name, String plural, String minor, String minorPlural) {
		entry.name = name;
		entry.plural = plural;
		entry.minor = minor;
		entry.minorplural = minorPlural;
		entry.id = databaseID;
	}

	/**
	 * Get the currency name
	 * @return The currency Name
	 */
	public String getName() {
		return entry.name;
	}

	/**
	 * Set the currency name
	 * @param name The currency name to set to.
	 */
	public void setName(String name) {
		entry.name = name;
		save();
	}

	/**
	 * Get the currency name in plural
	 * @return The currency name in plural
	 */
	public String getPlural() {
		return entry.plural;
	}

	/**
	 * Set the currency name in plural
	 * @param plural The currency name in plural to set to.
	 */
	public void setPlural(String plural) {
		entry.plural = plural;
		save();
	}

	/**
	 * Get the currency minor name
	 * @return The currency minor name
	 */
	public String getMinor() {
		return entry.minor;
	}

	/**
	 * Set the currency minor name
	 * @param minor The currency minor name to set to
	 */
	public void setMinor(String minor) {
		entry.minor = minor;
		save();
	}

	/**
	 * Get the currency minor name in plural
	 * @return The currency minor name in plural
	 */
	public String getMinorPlural() {
		return entry.minorplural;
	}

	/**
	 * Set the currency minor name in plural
	 * @param minorPlural The currency minor name in plural to set to
	 */
	public void setMinorPlural(String minorPlural) {
		entry.minorplural = minorPlural;
		save();
	}

	/**
	 * Retrieve the database ID of this currency
	 * @return The database ID
	 */
	public int getDatabaseID() {
		return entry.id;
	}

	/**
	 * Save the currency information.
	 */
	private void save() {
		Common.getInstance().getDatabaseManager().getDatabase().save(entry);
	}

}
