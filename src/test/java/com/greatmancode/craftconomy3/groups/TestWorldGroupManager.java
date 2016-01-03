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
package com.greatmancode.craftconomy3.groups;

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.TestInitializator;
import com.greatmancode.tools.caller.unittest.UnitTestServerCaller;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestWorldGroupManager {

	@Before
	public void setUp() {
		new TestInitializator();
	}

    @After
    public void close() { Common.getInstance().onDisable();};

    @Test
	public void testWorldGroupManager() {
		assertFalse(Common.getInstance().getWorldGroupManager().worldGroupExist("testeur"));

		Common.getInstance().getWorldGroupManager().addWorldGroup("testeur");
		assertTrue(Common.getInstance().getWorldGroupManager().worldGroupExist("testeur"));

		Common.getInstance().getWorldGroupManager().addWorldToGroup("testeur", UnitTestServerCaller.worldName);
		assertEquals("testeur", Common.getInstance().getWorldGroupManager().getWorldGroupName(UnitTestServerCaller.worldName));
		assertEquals(WorldGroupsManager.DEFAULT_GROUP_NAME, Common.getInstance().getWorldGroupManager().getWorldGroupName(UnitTestServerCaller.worldName2));
		Common.getInstance().getWorldGroupManager().removeGroup("testeur");
		assertEquals(WorldGroupsManager.DEFAULT_GROUP_NAME, Common.getInstance().getWorldGroupManager().getWorldGroupName(UnitTestServerCaller.worldName));
		assertEquals(WorldGroupsManager.DEFAULT_GROUP_NAME, Common.getInstance().getWorldGroupManager().getWorldGroupName(UnitTestServerCaller.worldName2));

	}

}
