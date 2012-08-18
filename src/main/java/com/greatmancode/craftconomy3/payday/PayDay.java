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

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.database.tables.PayDayTable;

public class PayDay {

	private PayDayTable table = new PayDayTable();
	
	public PayDay(int dbId, String name, boolean disabled, int interval, String account, int status, int currency_id, double value, String worldName) {
		table.id = dbId;
		table.name = name;
		table.disabled = disabled;
		table.interval = interval;
		table.account = account;
		table.status = status;
		table.currency_id = currency_id;
		table.value = value;
		table.worldName = worldName;
	}
	
	public int getDatabaseId() {
		return table.id;
	}
	
	public String getName() {
		return table.name;
	}
	
	public void setName(String name) {
		table.name = name;
		save();
	}
	public boolean isDisabled() {
		return table.disabled;
	}
	
	public void setDisabled(boolean disabled) {
		table.disabled = disabled;
		save();
	}
	
	public int getInterval() {
		return table.interval;
	}
	
	/**
	 * Sets the interval of when the payday will run
	 * @param interval The interval
	 * @return True if the value has been changed else false if the value is lower or equal than 0.
	 */
	public boolean setInterval(int interval) {
		boolean result = false;
		if (interval > 0) {
			table.interval = interval;
			save();
			result = true;
		}
		return result;
	}
	
	public String getAccount() {
		return table.account;
	}
	
	public void setAccount(String account) {
		table.account = account;
		save();
	}
	
	public int getStatus() {
		return table.status;
	}
	
	public boolean setStatus(int status) {
		boolean result = false;
		if (status == 0 || status == 1) {
			table.status = status;
			save();
			result = true;
		}
		return result;
		
	}
	
	public int getCurrencyId() {
		return table.currency_id;
	}
	
	public void setCurrencyId(int currencyId) {
		table.currency_id = currencyId;
		save();
	}
	
	public double getValue() {
		return table.value;
	}
	
	public void setValue(double value) {
		table.value = value;
		save();
	}
	
	public String getWorldName() {
		return table.worldName;
	}
	
	public void setWorldName(String worldName) {
		table.worldName = worldName;
		save();
	}
	
	private void save() {
		Common.getInstance().getDatabaseManager().getDatabase().save(table);
	}
}
