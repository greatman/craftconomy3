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
package com.greatmancode.craftconomy3.database;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.TestInitializator;
import com.greatmancode.craftconomy3.UnitTestLoader;

public class TestDatabaseManager {

	
	@Before
	public void setUp() {
		new TestInitializator();
	}
	@Test
	public void testDatabaseManager() {
		if (Common.getInstance().getDatabaseManager() == null) {
			fail("DatabaseManager not loaded.");
		}
	}

	@Test
	public void testGetDatabase() {
		//fail("Not yet implemented");
	}

}
