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
package com.greatmancode.craftconomy3;

import java.util.logging.Logger;

import com.alta189.simplesave.exceptions.ConnectionException;
import com.alta189.simplesave.exceptions.TableRegistrationException;
import com.greatmancode.craftconomy3.database.tables.ConfigTable;
import com.greatmancode.tools.ServerType;
import com.greatmancode.tools.database.throwable.InvalidDatabaseConstructor;
import com.greatmancode.tools.interfaces.Loader;
import com.greatmancode.tools.interfaces.UnitTestLoader;

public class TestInitializator {
	private static boolean initialized = false;
	public TestInitializator() {
		if (!initialized) {
			new Common(new UnitTestLoader(), Logger.getLogger("unittest")).onEnable();
			Common.getInstance().getMainConfig().setValue("System.Setup", false);
			try {
				Common.getInstance().initialiseDatabase();
			} catch (TableRegistrationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ConnectionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidDatabaseConstructor invalidDatabaseConstructor) {
				invalidDatabaseConstructor.printStackTrace();
			}
			Common.getInstance().initializeCurrency();
			ConfigTable table = new ConfigTable();
			table.setName("holdings");
			table.setValue("100");
			Common.getInstance().getDatabaseManager().getDatabase().save(table);
			table = new ConfigTable();
			table.setName("bankprice");
			table.setValue("200");
			Common.getInstance().getDatabaseManager().getDatabase().save(table);
			table = new ConfigTable();
			table.setName("longmode");
			table.setValue("long");
			Common.getInstance().getDatabaseManager().getDatabase().save(table);
			Common.getInstance().getCurrencyManager().addCurrency("Dollar", "Dollars", "Coin", "Coins", 0 , "$", true);
			Common.getInstance().getCurrencyManager().setDefault(1);
			Common.getInstance().loadDefaultSettings();
			Common.getInstance().startUp();
			initialized = true;
		}
	}
}
