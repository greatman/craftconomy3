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
package com.greatmancode.craftconomy3.commands;

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.TestInitializator;
import com.greatmancode.tools.commands.interfaces.CommandExecutor;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.reflections.Reflections;

import static org.junit.Assert.fail;

public class TestLoadedCommands {
	@Before
	public void setUp() {
		new TestInitializator();
	}

    @After
    public void close() { Common.getInstance().onDisable();};

    @Test
	public void testCommands() {

		Reflections reflections = new Reflections("com.greatmancode.craftconomy3.commands");
        for (Class<? extends CommandExecutor> clazz : reflections.getSubTypesOf(CommandExecutor.class)) {
            try {
                CommandExecutor instance = clazz.newInstance();
                if (instance.help() == null) {
                    fail("Help is null for: " + clazz.getName());
                }
                if (instance.maxArgs() < 0) {
                    fail("Fail maxArgs for class: " + clazz.getName());
                }
                if (instance.minArgs() < 0) {
                    fail("Fail minArgs for class: " + clazz.getName());
                }
                if (instance.maxArgs() < instance.minArgs()) {
                    fail("Fail maxArgs less than minArgs for class:" + clazz.getName());
                }
                if (instance.getPermissionNode() != null) {
                    if (!instance.getPermissionNode().contains("craftconomy")) {
                        fail("Fail permissionNode for class: " + clazz.getName());
                    }
                }
                if (!instance.playerOnly() && instance.playerOnly()) {
                    fail("Fail playerOnly. Should never get this..");
                }
            } catch (InstantiationException e) {
                fail(e.getMessage());
            } catch (IllegalAccessException e) {
                fail(e.getMessage());
            }
        }
	}
}
