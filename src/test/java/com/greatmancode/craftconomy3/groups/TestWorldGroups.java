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
package com.greatmancode.craftconomy3.groups;

import java.lang.reflect.Field;
import java.util.List;

import com.greatmancode.craftconomy3.TestInitializator;
import com.greatmancode.craftconomy3.UnitTestCaller;

import org.junit.Before;
import org.junit.Test;
import org.reflections.Reflections;

import static org.junit.Assert.fail;

public class TestWorldGroups {

	@Before
	public void setUp() {
		new TestInitializator();
	}

	@Test
	public void testWorldGroup() throws NoSuchFieldException, IllegalAccessException {
		WorldGroup worldGroup = new WorldGroup("test");
		if (worldGroup.worldExist(UnitTestCaller.worldName)) {
			fail("World test exist.");
		}
		worldGroup.addWorld("test");
		if (worldGroup.worldExist("test")) {
			fail("World test doesn't exist but was added!");
		}
		worldGroup.addWorld(UnitTestCaller.worldName);
		if (!worldGroup.worldExist(UnitTestCaller.worldName)) {
			fail("World waesn't added!");
		}
		worldGroup.removeWorld(UnitTestCaller.worldName);
		if (worldGroup.worldExist(UnitTestCaller.worldName)) {
			fail("World UnitTestWorld exist part 2.");
		}
	}
}
