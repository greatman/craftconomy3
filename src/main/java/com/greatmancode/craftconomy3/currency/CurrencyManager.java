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

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.database.tables.BalanceTable;
import com.greatmancode.craftconomy3.database.tables.CurrencyTable;

/**
 * Currency Handler
 * @author greatman
 * 
 */
public class CurrencyManager {

	/**
	 * The default currency database ID
	 */
	public static int defaultCurrencyID;

	private HashMap<Integer, Currency> currencyList = new HashMap<Integer, Currency>();

	public CurrencyManager() {
		// Let's load all currency in the database
		Iterator<CurrencyTable> iterator = Common.getInstance().getDatabaseManager().getDatabase().select(CurrencyTable.class).execute().find().iterator();
		while (iterator.hasNext()) {
			CurrencyTable entry = iterator.next();
			if (entry.status == true) {
				defaultCurrencyID = entry.id;
			}
			addCurrency(entry.id, entry.name, entry.plural, entry.minor, entry.minorplural, false);
		}
	}

	/**
	 * Get a currency
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
	 * @param name The name of the currency
	 * @return A currency instance if the currency is found else null
	 */
	public Currency getCurrency(String name) {
		Currency result = null;
		CurrencyTable DBresult = Common.getInstance().getDatabaseManager().getDatabase().select(CurrencyTable.class).where().equal("name", name).execute().findOne();
		if (DBresult != null) {
			result = getCurrency(DBresult.id);
		}
		return result;
	}

	/**
	 * Add a currency in the system
	 * @param name The main currency name
	 * @param plural The main currency name in plural
	 * @param minor The minor (cents) part of the currency
	 * @param minorPlural The minor (cents) part of the currency in plural
	 * @param save Do we add it in the database?
	 */
	
	public void addCurrency(String name, String plural, String minor, String minorPlural, boolean save) {
		addCurrency(-1, name, plural, minor, minorPlural, save);
	}

	/**
	 * Add a currency in the system
	 * @param name The main currency name
	 * @param plural The main currency name in plural
	 * @param minor The minor (cents) part of the currency
	 * @param minorPlural The minor (cents) part of the currency in plural
	 * @param save Do we add it in the database? If True, generates a databaseID (Whole new entry)
	 */
	//TODO: A check if the currency already exist.
	public void addCurrency(int databaseID, String name, String plural, String minor, String minorPlural, boolean save) {
		if (save) {
			CurrencyTable entry = new CurrencyTable();
			entry.minor = minor;
			entry.minorplural = minorPlural;
			entry.name = name;
			entry.plural = plural;
			Common.getInstance().getDatabaseManager().getDatabase().save(entry);
			databaseID = entry.id;
		}
		currencyList.put(databaseID, new Currency(databaseID, name, plural, minor, minorPlural));
	}
	
	public void setDefault(int currencyId) {
		if (currencyList.containsKey(currencyId)) {
			CurrencyTable entry = Common.getInstance().getDatabaseManager().getDatabase().select(CurrencyTable.class).where().equal("status", true).execute().findOne();
			if (entry != null) {
				entry.status = false;
				Common.getInstance().getDatabaseManager().getDatabase().save(entry);
			}
			defaultCurrencyID = currencyId;
		}
	}
	
	public void deleteCurrency(int currencyId) {
		if (currencyList.containsKey(currencyId)) {
			List<BalanceTable> balanceList = Common.getInstance().getDatabaseManager().getDatabase().select(BalanceTable.class).where().equal("currency_id", currencyId).execute().find();
			if (balanceList != null) {
				Iterator<BalanceTable> balanceIterator = balanceList.iterator();
				while (balanceIterator.hasNext()) {
					Common.getInstance().getDatabaseManager().getDatabase().remove(balanceIterator.next());
				}
			}
			CurrencyTable table = new CurrencyTable();
			Currency currency = getCurrency(currencyId);
			table.id = currencyId;
			table.name = currency.getName();
			table.plural = currency.getPlural();
			table.minor = currency.getMinor();
			table.minorplural = currency.getMinorPlural();
			Common.getInstance().getDatabaseManager().getDatabase().remove(table);
			currencyList.remove(currencyId);
		}
	}
}
