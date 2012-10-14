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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.database.tables.BalanceTable;
import com.greatmancode.craftconomy3.database.tables.CurrencyTable;

/**
 * Currency Handler
 * 
 * @author greatman
 * 
 */
public class CurrencyManager {

	/**
	 * The default currency database ID
	 */
	public static int defaultCurrencyID;

	private Map<Integer, Currency> currencyList = new HashMap<Integer, Currency>();

	public CurrencyManager() {
		// Let's load all currency in the database
		Iterator<CurrencyTable> iterator = Common.getInstance().getDatabaseManager().getDatabase().select(CurrencyTable.class).execute().find().iterator();
		while (iterator.hasNext()) {
			CurrencyTable entry = iterator.next();
			if (entry.getStatus()) {
				defaultCurrencyID = entry.getId();
			}
			addCurrency(entry.getId(), entry.getName(), entry.getPlural(), entry.getMinor(), entry.getMinorplural(), entry.getHardCap(), entry.getSign(), false);
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
	 * Add a currency in the system
	 * 
	 * @param name The main currency name
	 * @param plural The main currency name in plural
	 * @param minor The minor (cents) part of the currency
	 * @param minorPlural The minor (cents) part of the currency in plural
	 * @param save Do we add it in the database?
	 */

	public void addCurrency(String name, String plural, String minor, String minorPlural, double hardCap, String sign, boolean save) {
		addCurrency(-1, name, plural, minor, minorPlural, hardCap, sign, save);
	}

	/**
	 * Add a currency in the system
	 * 
	 * @param name The main currency name
	 * @param plural The main currency name in plural
	 * @param minor The minor (cents) part of the currency
	 * @param minorPlural The minor (cents) part of the currency in plural
	 * @param save Do we add it in the database? If True, generates a databaseID (Whole new entry)
	 */
	// TODO: A check if the currency already exist.
	public void addCurrency(int databaseID, String name, String plural, String minor, String minorPlural, double hardCap, String sign, boolean save) {
		int newDatabaseID = databaseID;
		if (save) {
			CurrencyTable entry = new CurrencyTable();
			entry.setMinor(minor);
			entry.setMinorplural(minorPlural);
			entry.setName(name);
			entry.setPlural(plural);
			entry.setHardCap(hardCap);
			entry.setSign(sign);
			Common.getInstance().getDatabaseManager().getDatabase().save(entry);
			newDatabaseID = entry.getId();
		}
		currencyList.put(newDatabaseID, new Currency(newDatabaseID, name, plural, minor, minorPlural, hardCap, sign));
	}

	/**
	 * Set a currency as the default one.
	 * @param currencyId The default currency ID.
	 */
	public void setDefault(int currencyId) {
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
	 * @param currencyId The currency ID to delete.
	 */
	public void deleteCurrency(int currencyId) {
		if (currencyList.containsKey(currencyId)) {
			List<BalanceTable> balanceList = Common.getInstance().getDatabaseManager().getDatabase().select(BalanceTable.class).where().equal("currency_id", currencyId).execute().find();
			if (balanceList != null) {
				Iterator<BalanceTable> balanceIterator = balanceList.iterator();
				while (balanceIterator.hasNext()) {
					Common.getInstance().getDatabaseManager().getDatabase().remove(balanceIterator.next());
				}
			}
			currencyList.get(currencyId).delete();
			currencyList.remove(currencyId);
		}
	}
}
