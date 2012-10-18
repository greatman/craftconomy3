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
package com.greatmancode.craftconomy3;

import com.alta189.simplesave.exceptions.ConnectionException;
import com.alta189.simplesave.exceptions.TableRegistrationException;
import com.greatmancode.craftconomy3.database.tables.ConfigTable;

public class TestInitializator{

	private static boolean initialized = false;
	public TestInitializator() {
		if (!initialized) {
			new UnitTestLoader().onEnable();
			Common.getInstance().getConfigurationManager().getConfig().setValue("System.Setup", false);
			try {
				Common.getInstance().initialiseDatabase();
			} catch (TableRegistrationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ConnectionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
			table.setValue("true");
			Common.getInstance().getDatabaseManager().getDatabase().save(table);
			table = new ConfigTable();
			table.setName("multiworld");
			table.setValue("false");
			Common.getInstance().getDatabaseManager().getDatabase().save(table);
			Common.getInstance().startUp();
			initialized = true;
		}
	}

}
