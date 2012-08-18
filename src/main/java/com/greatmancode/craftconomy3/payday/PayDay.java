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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.database.tables.PayDayTable;

public class PayDay implements Runnable {

	private PayDayTable table = new PayDayTable();
	private int delayedId = -1;

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
		if (!disabled) {
			startDelay();
		}
	}

	@Override
	public void run() {
		Iterator<String> iterator = Common.getInstance().getServerCaller().getOnlinePlayers().iterator();
		List<String> list = new ArrayList<String>();
		while (iterator.hasNext()) {
			String player = iterator.next();
			if (Common.getInstance().getServerCaller().checkPermission(player, "craftconomy.payday." + table.name)) {
				list.add(player);
			}
		}

		// Wage
		if (getStatus() == 0) {
			if (!getName().equals("")) {
				if (!Common.getInstance().getAccountManager().getAccount(getName()).hasEnough(getValue() * list.size(), getWorldName(), Common.getInstance().getCurrencyManager().getCurrency(getCurrencyId()).getName())) {
					Common.getInstance().sendConsoleMessage(Level.INFO, "{{DARK_RED}}Impossible to give a wage for the payday {{WHITE}}" + getName() +"{{DARK_RED}}. The account doesn't have enough money!");
					return;
				}
				Common.getInstance().getAccountManager().getAccount(getName()).withdraw(getValue() * list.size(), getWorldName(), Common.getInstance().getCurrencyManager().getCurrency(getCurrencyId()).getName());
				Iterator<String> listIterator = list.iterator();
				while (listIterator.hasNext()) {
					String p = listIterator.next();
					Common.getInstance().getAccountManager().getAccount(p).deposit(getValue() * list.size(), getWorldName(), Common.getInstance().getCurrencyManager().getCurrency(getCurrencyId()).getName());
					Common.getInstance().getServerCaller().sendMessage(p, "{{DARK_GREEN}}Payday! You received " + Common.getInstance().format(getWorldName(), Common.getInstance().getCurrencyManager().getCurrency(getCurrencyId()), getValue() * list.size()));
				}
			}

		}
		// Tax
		else if (getStatus() == 1) {
			Iterator<String> listIterator = list.iterator();
			while(listIterator.hasNext()) {
				String p = listIterator.next();
				if (Common.getInstance().getAccountManager().getAccount(p).hasEnough(getValue(), getWorldName(), Common.getInstance().getCurrencyManager().getCurrency(getCurrencyId()).getName())) {
					Common.getInstance().getAccountManager().getAccount(p).withdraw(getValue(), getWorldName(), Common.getInstance().getCurrencyManager().getCurrency(getCurrencyId()).getName());
					if (!getName().equals("")) {
						Common.getInstance().getAccountManager().getAccount(getName()).deposit(getValue(), getWorldName(), Common.getInstance().getCurrencyManager().getCurrency(getCurrencyId()).getName());
					}
				} else {
					Common.getInstance().getServerCaller().sendMessage(p, "{{RED}}Not enough money to pay for your taxes!");
				}
			}
		}

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
		if (!disabled) {
			if (delayedId == -1) {
				startDelay();
			}

		} else {
			if (delayedId != -1) {
				stopDelay();
			}
		}

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
			if (!table.disabled) {
				if (delayedId == -1) {
					startDelay();
				}

			} else {
				if (delayedId != -1) {
					stopDelay();
				}
			}
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

	public int getDelayedId() {
		return delayedId;
	}

	private void save() {
		Common.getInstance().getDatabaseManager().getDatabase().save(table);
	}

	private void startDelay() {
		delayedId = Common.getInstance().getServerCaller().schedule(this, table.interval, table.interval);
	}

	public void stopDelay() {
		if (delayedId != -1) {
			Common.getInstance().getServerCaller().cancelSchedule(delayedId);
			delayedId = -1;
		}
	}

	void delete() {
		Common.getInstance().getDatabaseManager().getDatabase().remove(table);
	}
}
