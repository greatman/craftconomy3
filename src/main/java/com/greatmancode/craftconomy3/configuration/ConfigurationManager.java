/*
 * This file is part of Craftconomy3.
 *
 * Copyright (c) 2011-2013, Greatman <http://github.com/greatman/>
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

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import com.greatmancode.craftconomy3.BukkitCaller;
import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.DisplayFormat;
import com.greatmancode.craftconomy3.SpoutCaller;
import com.greatmancode.craftconomy3.UnitTestCaller;
import com.greatmancode.craftconomy3.currency.CurrencyManager;
import com.greatmancode.craftconomy3.database.tables.ConfigTable;
import com.greatmancode.craftconomy3.database.tables.PayDayTable;

/**
 * Configuration Loader. Load the configuration with the Server configuration manager.
 * @author greatman
 */
public class ConfigurationManager {
	private Config config = null;
	private int bankCurrencyId;
	private boolean multiworld;
	private String displayFormat;
	private double bankPrice, holdings;

	public ConfigurationManager() {

	}

	public Config loadFile(File folder, String fileName) {
		Config file = null;
		if (Common.getInstance().getServerCaller() instanceof BukkitCaller || Common.getInstance().getServerCaller() instanceof UnitTestCaller) {
			file = new BukkitConfig(folder, fileName);
		} else if (Common.getInstance().getServerCaller() instanceof SpoutCaller) {
			file = new SpoutConfig(folder, fileName);
		}
		return file;
	}

	/**
	 * Initialize the Configuration manager. SHOULD NOT BE USED AT ALL. THIS IS RESERVED FOR THE CC3 CONFIG FILE.
	 */
	public void initialize(File folder, String fileName) {
		config = loadFile(folder, fileName);
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
		ConfigTable dbVersion = Common.getInstance().getDatabaseManager().getDatabase().select(ConfigTable.class).where().equal(ConfigTable.NAME_FIELD, "dbVersion").execute().findOne();
		if (dbVersion == null) {
			alertOldDbVersion("0", 1);
			List<PayDayTable> payday = Common.getInstance().getDatabaseManager().getDatabase().select(PayDayTable.class).execute().find();
			if (payday != null) {
				Iterator<PayDayTable> iterator = payday.iterator();
				while (iterator.hasNext()) {
					PayDayTable entry = iterator.next();
					entry.setName(entry.getName().toLowerCase());
					Common.getInstance().getDatabaseManager().getDatabase().save(entry);
				}
			}
			dbVersion = new ConfigTable();
			dbVersion.setName("dbVersion");
			dbVersion.setValue(1 + "");
			Common.getInstance().getDatabaseManager().getDatabase().save(dbVersion);
			Common.getInstance().getLogger().info("Updated to Revision 1!");
		}
		if (dbVersion.getValue().equalsIgnoreCase("")) {
			alertOldDbVersion(dbVersion.getValue(), 1);
			dbVersion.setValue(1 + "");
			Common.getInstance().getDatabaseManager().getDatabase().save(dbVersion);
			Common.getInstance().getLogger().info("Really updated to Revision 1.");
		}
		if (dbVersion.getValue().equalsIgnoreCase("1")) {
			alertOldDbVersion(dbVersion.getValue(), 2);
			// Testing to see if in the config we have true or false as the display value
			ConfigTable display = Common.getInstance().getDatabaseManager().getDatabase().select(ConfigTable.class).where().equal(ConfigTable.NAME_FIELD, "longmode").execute().findOne();
			if (display.getValue().equals("true") || display.getValue().equalsIgnoreCase("false")) {
				if (display.getValue().equals("true")) {
					display.setValue("long");
				}
				if (display.getValue().equals("false")) {
					display.setValue("short");
				}
				Common.getInstance().getDatabaseManager().getDatabase().save(display);
				dbVersion.setValue(2 + "");
				Common.getInstance().getDatabaseManager().getDatabase().save(dbVersion);
				Common.getInstance().getLogger().info("Updated to Revision 2!");
			}
		}
		if (dbVersion.getValue().equalsIgnoreCase("2")) {
			alertOldDbVersion(dbVersion.getValue(), 3);
			Common.getInstance().getLogger().info("Checking if the display format is valid.");
			ConfigTable display = Common.getInstance().getDatabaseManager().getDatabase().select(ConfigTable.class).where().equal(ConfigTable.NAME_FIELD, "longmode").execute().findOne();
			if (!display.getValue().equalsIgnoreCase("long") && !display.getValue().equalsIgnoreCase("small") && !display.getValue().equalsIgnoreCase("sign") && !display.getValue().equalsIgnoreCase("majoronly")) {
				Common.getInstance().getLogger().info("Display format is invalid. Saving a valid one.");
				display.setValue("long");
				Common.getInstance().getDatabaseManager().getDatabase().save(display);

			}
			dbVersion.setValue(3 + "");
			Common.getInstance().getDatabaseManager().getDatabase().save(dbVersion);
			Common.getInstance().getLogger().info("Updated to Revision 3!");
		}
		if (dbVersion.getValue().equalsIgnoreCase("3")) {
			alertOldDbVersion(dbVersion.getValue(), 4);
			Common.getInstance().getLogger().info("Converting worlds to the new format.");
			Common.getInstance().initializeWorldGroup();
			ResultSet result = Common.getInstance().getDatabaseManager().getDatabase().directQueryWithResult("SELECT DISTINCT worldName FROM cc3_balance");
			try {
				while(result.next()) {
					String entry = result.getString("worldName");
					Common.getInstance().getWorldGroupManager().addWorldGroup(entry);
					Common.getInstance().getWorldGroupManager().addWorldToGroup(entry, entry);
				}
				result.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			dbVersion.setValue(4 + "");
			Common.getInstance().getDatabaseManager().getDatabase().save(dbVersion);
			Common.getInstance().getLogger().info("Updated to Revision 4!");
		}
	}

	private void alertOldDbVersion(String currentVersion, int newVersion) {
		Common.getInstance().getLogger().info("Your database is out of date! (Version " + currentVersion + "). Updating it to Revision " + newVersion + ".");
	}

	/**
	 * Load the settings from the database.
	 */
	public void loadDefaultSettings() {
		dbUpdate();
		holdings = Double.parseDouble(Common.getInstance().getDatabaseManager().getDatabase().select(ConfigTable.class).where().equal(ConfigTable.NAME_FIELD, "holdings").execute().findOne().getValue());
		bankPrice = Double.parseDouble(Common.getInstance().getDatabaseManager().getDatabase().select(ConfigTable.class).where().equal(ConfigTable.NAME_FIELD, "bankprice").execute().findOne().getValue());
		displayFormat = Common.getInstance().getDatabaseManager().getDatabase().select(ConfigTable.class).where().equal(ConfigTable.NAME_FIELD, "longmode").execute().findOne().getValue();
		ConfigTable currencyId = Common.getInstance().getDatabaseManager().getDatabase().select(ConfigTable.class).where().equal(ConfigTable.NAME_FIELD, "bankcurrency").execute().findOne();
		if (currencyId != null) {
			bankCurrencyId = Integer.parseInt(currencyId.getValue());
		}

		// Test if the currency is good. Else we revert it to the default value.
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
	 * Get the display format for balance.
	 * @return True or false.
	 */
	public DisplayFormat getDisplayFormat() {
		return DisplayFormat.valueOf(displayFormat.toUpperCase());
	}

	/**
	 * Modify the longMode setting.
	 * @param displayFormat the display format we want to use.
	 */
	public void setDisplayFormat(String displayFormat) {
		this.displayFormat = displayFormat;
		ConfigTable table = Common.getInstance().getDatabaseManager().getDatabase().select(ConfigTable.class).where().equal(ConfigTable.NAME_FIELD, "longmode").execute().findOne();
		table.setValue(displayFormat);
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
		ConfigTable table = Common.getInstance().getDatabaseManager().getDatabase().select(ConfigTable.class).where().equal(ConfigTable.NAME_FIELD, "bankprice").execute().findOne();
		table.setValue(String.valueOf(bankPrice));
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
		ConfigTable table = Common.getInstance().getDatabaseManager().getDatabase().select(ConfigTable.class).where().equal(ConfigTable.NAME_FIELD, "holdings").execute().findOne();
		table.setValue(String.valueOf(holdings));
		Common.getInstance().getDatabaseManager().getDatabase().save(table);
	}
}
