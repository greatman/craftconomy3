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
package com.greatmancode.craftconomy3.payday;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.database.tables.PayDayTable;

/**
 * The PayDay Manager.
 * @author greatman
 *
 */
public class PayDayManager {

	private Map<Integer, PayDay> paydayList = new HashMap<Integer, PayDay>();

	public PayDayManager() {
		Iterator<PayDayTable> iterator = Common.getInstance().getDatabaseManager().getDatabase().select(PayDayTable.class).execute().find().iterator();
		while (iterator.hasNext()) {
			PayDayTable entry = iterator.next();
			addPayDay(entry.getId(), entry.getName(), entry.isDisabled(), entry.getTime(), entry.getAccount(), entry.getStatus(), entry.getCurrencyId(), entry.getValue(), entry.getWorldName(), false);
		}
	}

	/**
	 * Retrieve a PayDay entry.
	 * @param name The name of the PayDay entry
	 * @return A PayDay instance or null if not found.
	 */
	public PayDay getPayDay(String name) {
		String newName = name.toLowerCase();
		PayDay entry = null;
		PayDayTable dbEntry = Common.getInstance().getDatabaseManager().getDatabase().select(PayDayTable.class).where().equal("name", newName).execute().findOne();
		if (dbEntry != null) {
			entry = getPayDay(dbEntry.getId());
		}
		return entry;
	}

	/**
	 * Retrieve a PayDay entry.
	 * @param dbId The database ID.
	 * @return A PayDay instance or null if not found
	 */
	public PayDay getPayDay(int dbId) {
		PayDay entry = null;
		if (paydayList.containsKey(dbId)) {
			entry = paydayList.get(dbId);
		}
		return entry;
	}
	
	/**
	 * Add a PayDay in the system
	 * @param name The name of the payday
	 * @param disabled Is it disabled or not?
	 * @param interval At what interval (In seconds) the payday should run?
	 * @param account Do we give/withdraw money from a account? (Empty if none)
	 * @param status 0 = Wage 1 = Tax
	 * @param currencyId The currency ID accociated with this payday
	 * @param value The amount of money we should give/take
	 * @param worldName The world name we give/take money from. Will default to any if the multiworld system is not enabled.
	 * @param save Do we add it to the database?
	 */
	public void addPayDay(String name, boolean disabled, int interval, String account, int status, int currencyId, double value, String worldName, boolean save) {
		addPayDay(-1, name, disabled, interval, account, status, currencyId, value, worldName, save);
	}

	/**
	 * Add a PayDay in the system
	 * @param dbId The database ID.
	 * @param name The name of the payday
	 * @param disabled Is it disabled or not?
	 * @param interval At what interval (In seconds) the payday should run?
	 * @param account Do we give/withdraw money from a account? (Empty if none)
	 * @param status 0 = Wage 1 = Tax
	 * @param currencyId The currency ID accociated with this payday
	 * @param value The amount of money we should give/take
	 * @param worldName The world name we give/take money from. Will default to any if the multiworld system is not enabled.
	 * @param save Do we add it to the database?
	 */
	public void addPayDay(int dbId, String name, boolean disabled, int interval, String account, int status, int currencyId, double value, String worldName, boolean save) {
		String newName = name.toLowerCase();
		int newId = dbId;
		if (save) {
			PayDayTable table = new PayDayTable();
			table.setName(newName);
			table.setDisabled(disabled);
			table.setTime(interval);
			table.setAccount(account);
			table.setStatus(status);
			table.setCurrencyId(currencyId);
			table.setValue(value);
			table.setWorldName(worldName);
			Common.getInstance().getDatabaseManager().getDatabase().save(table);
			newId = table.getId();
		}
		paydayList.put(newId, new PayDay(newId, newName, disabled, interval, account, status, currencyId, value, worldName));

	}
	
	/**
	 * Delete a PayDay from the system
	 * @param dbId the database ID to remove
	 * @return True if the entry has been removed else false.
	 */
	public boolean deletePayDay(int dbId) {
		boolean result = false;
		if (paydayList.containsKey(dbId)) {
			PayDay entry = paydayList.get(dbId);
			entry.stopDelay();
			entry.delete();
			paydayList.remove(dbId);
			result = true;
		}
		return result;
	}
	
	/**
	 * Receive a COPY of the payday List to keep internal sane list.
	 * @return A copy of the payday List.
	 */
	@SuppressWarnings("unchecked")
	public Map<Integer, PayDay> getPayDayList() {
		return (Map<Integer, PayDay>) ((HashMap<Integer, PayDay>) paydayList).clone();
	}

}
