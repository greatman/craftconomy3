/**
 * This file is part of Craftconomy3.
 *
 * Copyright (c) 2011-2016, Greatman <http://github.com/greatman/>
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
import com.greatmancode.craftconomy3.TestInitializator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TestConfigurationManager {
	@Before
	public void setUp() {
		new TestInitializator();
	}

    @After
    public void close() { Common.getInstance().onDisable();};

    @Test
	public void test() {
		assertNotNull(Common.getInstance().getConfigurationManager());
		assertNotNull(Common.getInstance().getMainConfig());
		assertEquals(false, Common.getInstance().getMainConfig().getBoolean("System.Setup"));
		Common.getInstance().getMainConfig().setValue("System.Setup", true);
		assertEquals(true, Common.getInstance().getMainConfig().getBoolean("System.Setup"));
		Common.getInstance().getMainConfig().setValue("System.Setup", false);
        if (Boolean.getBoolean("mysql")) {
            assertEquals("mysql", Common.getInstance().getMainConfig().getString("System.Database.Type"));
        } else {
            assertEquals("h2", Common.getInstance().getMainConfig().getString("System.Database.Type"));
        }
		assertEquals(3306, Common.getInstance().getMainConfig().getInt("System.Database.Port"));
		Common.getInstance().getMainConfig().setValue("test", 30);
		assertEquals(30, Common.getInstance().getMainConfig().getLong("test"));
		Common.getInstance().getMainConfig().setValue("test", 30.40);
		assertEquals(30.40, Common.getInstance().getMainConfig().getDouble("test"), 0);
	}
}
