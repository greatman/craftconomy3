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

/**
 * Represents a PayDay
 * @author greatman
 *
 */
public class PayDay implements Runnable {

	private PayDayTable table = new PayDayTable();
	private int delayedId = -1;

	public PayDay(int dbId, String name, boolean disabled, int interval, String account, int status, int currencyId, double value, String worldName) {
		table.setId(dbId);
		table.setName(name);
		table.setDisabled(disabled);
		table.setTime(interval);
		table.setAccount(account);
		table.setStatus(status);
		table.setCurrencyId(currencyId);
		table.setValue(value);
		table.setWorldName(worldName);
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
			if (Common.getInstance().getServerCaller().checkPermission(player, "craftconomy.payday." + table.getName())) {
				list.add(player);
			}
		}

		// Wage
		if (getStatus() == 0) {
			if (!getAccount().equals("")) {
				if (!Common.getInstance().getAccountManager().getAccount(getAccount()).hasEnough(getValue() * list.size(), getWorldName(), Common.getInstance().getCurrencyManager().getCurrency(getCurrencyId()).getName())) {
					Common.getInstance().sendConsoleMessage(Level.INFO, "{{DARK_RED}}Impossible to give a wage for the payday {{WHITE}}" + getName() +"{{DARK_RED}}. The account doesn't have enough money!");
					return;
				}
				Common.getInstance().getAccountManager().getAccount(getAccount()).withdraw(getValue() * list.size(), getWorldName(), Common.getInstance().getCurrencyManager().getCurrency(getCurrencyId()).getName());
			}
			Iterator<String> listIterator = list.iterator();
			while (listIterator.hasNext()) {
				String p = listIterator.next();
				Common.getInstance().getAccountManager().getAccount(p).deposit(getValue() * list.size(), getWorldName(), Common.getInstance().getCurrencyManager().getCurrency(getCurrencyId()).getName());
				Common.getInstance().getServerCaller().sendMessage(p, "{{DARK_GREEN}}Payday! You received " + Common.getInstance().format(getWorldName(), Common.getInstance().getCurrencyManager().getCurrency(getCurrencyId()), getValue() * list.size()));
			}
		}
		// Tax
		else if (getStatus() == 1) {
			Iterator<String> listIterator = list.iterator();
			while(listIterator.hasNext()) {
				String p = listIterator.next();
				if (Common.getInstance().getAccountManager().getAccount(p).hasEnough(getValue(), getWorldName(), Common.getInstance().getCurrencyManager().getCurrency(getCurrencyId()).getName())) {
					Common.getInstance().getAccountManager().getAccount(p).withdraw(getValue(), getWorldName(), Common.getInstance().getCurrencyManager().getCurrency(getCurrencyId()).getName());
					if (!getAccount().equals("")) {
						Common.getInstance().getAccountManager().getAccount(getAccount()).deposit(getValue(), getWorldName(), Common.getInstance().getCurrencyManager().getCurrency(getCurrencyId()).getName());
					}
				} else {
					Common.getInstance().getServerCaller().sendMessage(p, "{{RED}}Not enough money to pay for your taxes!");
				}
			}
		}

	}

	/**
	 * Retrieve the database ID
	 * @return the database ID
	 */
	public int getDatabaseId() {
		return table.getId();
	}

	/**
	 * Retrieve the name of the payday
	 * @return The payday name.
	 */
	public String getName() {
		return table.getName();
	}

	/**
	 * Sets the payday name.
	 * @param name The payday name.
	 */
	public void setName(String name) {
		table.setName(name);
		save();
	}

	/**
	 * Check if the payday is disabled or not.
	 * @return True if the project is disabled else false
	 */
	public boolean isDisabled() {
		return table.isDisabled();
	}

	/**
	 * Sets the project as disabled or not
	 * @param disabled Disabled or not.
	 */
	public void setDisabled(boolean disabled) {
		table.setDisabled(disabled);
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

	/**
	 * Retrieve the interval (in seconds) that the payday will run.
	 * @return The interval in seconds.
	 */
	public int getInterval() {
		return table.getTime();
	}

	/**
	 * Sets the interval of when the payday will run
	 * @param interval The interval
	 * @return True if the value has been changed else false if the value is lower or equal than 0.
	 */
	public boolean setInterval(int interval) {
		boolean result = false;
		if (interval > 0) {
			table.setTime(interval);
			save();
			result = true;
			if (!table.isDisabled()) {
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

	/**
	 * Retrieve the account that this payday is associated with
	 * @return The account
	 */
	public String getAccount() {
		return table.getAccount();
	}

	/**
	 * Set the account that this payday is associated with
	 * @param account The account to accociate to.
	 */
	public void setAccount(String account) {
		table.setAccount(account);
		save();
	}

	/**
	 * Retrieve the status of the payday (0 = wage, 1 = tax)
	 * @return The status of the payday.
	 */
	public int getStatus() {
		return table.getStatus();
	}

	/**
	 * Sets the status of the payday
	 * @param status The status  (0 = wage, 1 = tax)
	 * @return True if the value has been modified else false.
	 */
	public boolean setStatus(int status) {
		boolean result = false;
		if (status == 0 || status == 1) {
			table.setStatus(status);
			save();
			result = true;
		}
		return result;

	}

	/**
	 * Get the currency ID associated with this payday
	 * @return The currency ID.
	 */
	public int getCurrencyId() {
		return table.getCurrencyId();
	}

	/**
	 * Set the currency ID associated with this payday
	 * @param currencyId The currency ID to set to.
	 */
	public void setCurrencyId(int currencyId) {
		table.setCurrencyId(currencyId);
		save();
	}

	/**
	 * Retrieve the value (The amount of money) associated with this payday
	 * @return The amount of money.
	 */
	public double getValue() {
		return table.getValue();
	}

	/**
	 * Sets the value (The amount of money).
	 * @param value The amount of money
	 */
	public void setValue(double value) {
		table.setValue(value);
		save();
	}

	/**
	 * Retrieve the world name associated with this payday.
	 * @return The world name.
	 */
	public String getWorldName() {
		return table.getWorldName();
	}

	/**
	 * Sets the world name
	 * @param worldName The world name.
	 */
	public void setWorldName(String worldName) {
		table.setWorldName(worldName);
		save();
	}

	/**
	 * Retrieve the delayed Id
	 * @return The delayed Id
	 */
	public int getDelayedId() {
		return delayedId;
	}

	private void save() {
		Common.getInstance().getDatabaseManager().getDatabase().save(table);
	}

	private void startDelay() {
		delayedId = Common.getInstance().getServerCaller().schedule(this, table.getTime(), table.getTime());
	}

	/**
	 * Stop the delayer if it's running.
	 */
	public void stopDelay() {
		if (delayedId != -1) {
			Common.getInstance().getServerCaller().cancelSchedule(delayedId);
			delayedId = -1;
		}
	}

	/**
	 * Delete the payday.
	 */
	void delete() {
		Common.getInstance().getDatabaseManager().getDatabase().remove(table);
	}
}
