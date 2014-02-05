/*
 * This file is part of Craftconomy3.
 *
 * Copyright (c) 2011-2014, Greatman <http://github.com/greatman/>
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

import java.lang.reflect.Field;
import java.util.logging.Logger;

import com.alta189.simplesave.exceptions.ConnectionException;
import com.alta189.simplesave.exceptions.TableRegistrationException;
import com.greatmancode.craftconomy3.database.tables.ConfigTable;
import com.greatmancode.tools.caller.unittest.UnitTestServerCaller;
import com.greatmancode.tools.database.throwable.InvalidDatabaseConstructor;
import com.greatmancode.tools.interfaces.UnitTestLoader;

public class TestInitializator {
	private static boolean initialized = false;
	public TestInitializator() {
		if (!initialized) {
			new Common().onEnable(new UnitTestServerCaller(new UnitTestLoader()), Logger.getLogger("unittest"));
            Common.getInstance().getMainConfig().setValue("System.QuickSetup.Enable", true);
            Common.getInstance().getMainConfig().setValue("System.Logging.Enabled", true);
            try {
                setStaticValue("com.greatmancode.craftconomy3.Common", "initialized", false);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            new Common().onEnable(new UnitTestServerCaller(new UnitTestLoader()), Logger.getLogger("unittest"));
			initialized = true;
		}
	}

    /**
     * Use reflection to change value of any static field.
     * @param className The complete name of the class (ex. java.lang.String)
     * @param fieldName The name of a static field in the class
     * @param newValue The value you want the field to be set to.
     * @throws SecurityException .
     * @throws NoSuchFieldException .
     * @throws ClassNotFoundException .
     * @throws IllegalArgumentException .
     * @throws IllegalAccessException .
     */
    public static void setStaticValue(final String className, final String fieldName, final Object newValue) throws SecurityException, NoSuchFieldException,
            ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
        // Get the private String field
        final Field field = Class.forName(className).getDeclaredField(fieldName);
        // Allow modification on the field
        field.setAccessible(true);
        // Get
        final Object oldValue = field.get(Class.forName(className));
        // Sets the field to the new value
        field.set(oldValue, newValue);
    }
}
