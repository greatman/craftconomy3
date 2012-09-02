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
package com.greatmancode.craftconomy3.configuration;

import java.util.Iterator;
import java.util.List;

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.currency.CurrencyManager;
import com.greatmancode.craftconomy3.database.tables.ConfigTable;
import com.greatmancode.craftconomy3.database.tables.PayDayTable;

/**
 * Configuration Loader. Load the configuration with the Server configuration manager.
 * @author greatman
 * 
 */
public class ConfigurationManager {

	private Config config = null;
	private int bankCurrencyId;
	private boolean longmode, multiworld;
	private double bankPrice, holdings;
	public ConfigurationManager() {

	}

	/**
	 * Initialize the Configuration manager
	 */
	public void initialize() {
		if (Common.isBukkit()) {

			config = new BukkitConfig();
		} else {
			config = new SpoutConfig();
		}
	}

	/**
	 * Retrieve the configuration handler
	 * @return The configuration handler.
	 */
	public Config getConfig() {
		return config;

	}
	
	/**
	 * Run on each launch to be sure the database is on the latest revision.
	 */
	public void dbUpdate() {
		ConfigTable dbVersion = Common.getInstance().getDatabaseManager().getDatabase().select(ConfigTable.class).where().equal("name", "dbVersion").execute().findOne();
		if (dbVersion == null) {
			Common.getInstance().getLogger().info("Your database is out of date! (Version 0). Updating it to Revision 1.");
			List<PayDayTable> payday = Common.getInstance().getDatabaseManager().getDatabase().select(PayDayTable.class).execute().find();
			if (payday != null) {
				Iterator<PayDayTable> iterator = payday.iterator();
				while (iterator.hasNext()) {
					PayDayTable entry = iterator.next();
					entry.name = entry.name.toLowerCase();
					Common.getInstance().getDatabaseManager().getDatabase().save(entry);
				}
			}
			dbVersion = new ConfigTable();
			dbVersion.name = "dbVersion";
			dbVersion.value = "1";
			Common.getInstance().getDatabaseManager().getDatabase().save(dbVersion);
			Common.getInstance().getLogger().info("Updated to Revision 1!");
		}
	}
	/**
	 * Load the settings from the database.
	 */
	public void loadDefaultSettings() {
		dbUpdate();
		holdings = Double.parseDouble(Common.getInstance().getDatabaseManager().getDatabase().select(ConfigTable.class).where().equal("name", "holdings").execute().findOne().value);
		bankPrice = Double.parseDouble(Common.getInstance().getDatabaseManager().getDatabase().select(ConfigTable.class).where().equal("name", "bankprice").execute().findOne().value);
		longmode = Boolean.parseBoolean(Common.getInstance().getDatabaseManager().getDatabase().select(ConfigTable.class).where().equal("name", "longmode").execute().findOne().value);
		multiworld = Boolean.parseBoolean(Common.getInstance().getDatabaseManager().getDatabase().select(ConfigTable.class).where().equal("name", "multiworld").execute().findOne().value);
		ConfigTable currencyId = Common.getInstance().getDatabaseManager().getDatabase().select(ConfigTable.class).where().equal("name", "bankcurrency").execute().findOne();
		if (currencyId != null) {
			bankCurrencyId = Integer.parseInt(currencyId.value);
		}
		
		//Test if the currency is good. Else we revert it to the default value.
		if (Common.getInstance().getCurrencyManager().getCurrency(bankCurrencyId) == null) {
			bankCurrencyId = CurrencyManager.defaultCurrencyID;
		}
	}
	
	/**
	 * Retrieve the Bank creation currency ID. Currently unused.
	 * @return The bank creation currency ID.
	 */
	public int getBankCurrencyId() {
		return bankCurrencyId;
	}
	
	/**
	 * Check if the plugin is set in MultiWorld mode or not.
	 * @return True if the system is set to be MultiWorld else false.
	 */
	public boolean isMultiWorld() {
		return multiworld;
	}
	
	/**
	 * Check if we should use the long format mode or the small one.
	 * @return True or false.
	 */
	public boolean isLongmode() {
		return longmode;
	}

	/**
	 * Modify the longMode setting.
	 * @param longmode True if we want long format else false.
	 */
	public void setLongmode(boolean longmode) {
		this.longmode = longmode;
		ConfigTable table = Common.getInstance().getDatabaseManager().getDatabase().select(ConfigTable.class).where().equal("name", "longmode").execute().findOne();
		table.value = String.valueOf(longmode);
		Common.getInstance().getDatabaseManager().getDatabase().save(table);
	}

	/**
	 * Retrieve the price to create a bank account.
	 * @return The price to create a bank acocunt.
	 */
	public double getBankPrice() {
		return bankPrice;
	}

	/**
	 * Sets the price to create a bank account.
	 * @param bankPrice The new Bank creation price.
	 */
	public void setBankPrice(double bankPrice) {
		this.bankPrice = bankPrice;
		ConfigTable table = Common.getInstance().getDatabaseManager().getDatabase().select(ConfigTable.class).where().equal("name", "bankprice").execute().findOne();
		table.value = String.valueOf(bankPrice);
		Common.getInstance().getDatabaseManager().getDatabase().save(table);
	}

	/**
	 * Retrieve the initial holdings in a account.
	 * @return The initial holdings.
	 */
	public double getHoldings() {
		return holdings;
	}

	/**
	 * Sets the initial holdings in a account.
	 * @param holdings The initials holdings to set to.
	 */
	public void setHoldings(double holdings) {
		this.holdings = holdings;
		ConfigTable table = Common.getInstance().getDatabaseManager().getDatabase().select(ConfigTable.class).where().equal("name", "holdings").execute().findOne();
		table.value = String.valueOf(longmode);
		Common.getInstance().getDatabaseManager().getDatabase().save(table);
	}
}
