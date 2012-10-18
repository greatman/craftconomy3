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

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.TestInitializator;

public class TestConfigurationManager {

	@Before
	public void setUp() {
		new TestInitializator();
	}
	@Test
	public void test() {
		if (Common.getInstance().getConfigurationManager() == null) {
			fail("ConfigurationManager didin't load!");
		}
		
		if(Common.getInstance().getConfigurationManager().getConfig() == null) {
			fail("getConfig() is null!");
		}
		
		Common.getInstance().getConfigurationManager().getConfig().setValue("System.Setup", false);
		if (Common.getInstance().getConfigurationManager().getConfig().getBoolean("System.Setup")) {
			fail("Craftconomy in setup mode. Should be false! setValue fail!");
		}
		
		if(!Common.getInstance().getConfigurationManager().getConfig().getString("System.Database.Type").equals("sqlite")) {
			fail("Database isin't SQLITE! Error with getString()");
		}
		
		if (Common.getInstance().getConfigurationManager().getConfig().getInt("System.Database.Port") != 3306) {
			fail("Port isin't 3306! Error with getInt()");
		}
		
		
	}

}
