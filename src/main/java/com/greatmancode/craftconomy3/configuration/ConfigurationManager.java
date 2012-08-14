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

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.currency.CurrencyManager;
import com.greatmancode.craftconomy3.database.tables.ConfigTable;

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
	
	public void loadDefaultSettings() {
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
	
	public int getBankCurrencyId() {
		return bankCurrencyId;
	}
	public boolean isMultiWorld() {
		return multiworld;
	}
	public boolean isLongmode() {
		return longmode;
	}

	public void setLongmode(boolean longmode) {
		this.longmode = longmode;
	}

	public double getBankPrice() {
		return bankPrice;
	}

	public void setBankPrice(double bankPrice) {
		this.bankPrice = bankPrice;
	}

	public double getHoldings() {
		return holdings;
	}

	public void setHoldings(double holdings) {
		this.holdings = holdings;
	}

	public void setConfig(Config config) {
		this.config = config;
	}
}
