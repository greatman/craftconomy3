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
package com.greatmancode.craftconomy3.commands;

import java.util.Iterator;

import org.reflections.Reflections;

import com.greatmancode.craftconomy3.commands.interfaces.CraftconomyCommand;

import junit.framework.TestCase;

public class TestCommands extends TestCase {

	public void testCommands() {
		
		Reflections reflections = new Reflections("com.greatmancode.craftconomy3.commands");
		Iterator<Class<? extends CraftconomyCommand>> allClasses = reflections.getSubTypesOf(CraftconomyCommand.class).iterator();
		while (allClasses.hasNext()) {
			Class<? extends CraftconomyCommand> clazz = allClasses.next();
			try {
				CraftconomyCommand instance = clazz.newInstance();
				if (!(instance.help() instanceof String)) {
					fail("Help is null for: " + clazz.getName());
				}
				if (instance.maxArgs() < 0) {
					fail("Fail maxArgs for class: " + clazz.getName());
				}
				if (instance.minArgs() < 0) {
					fail("Fail minArgs for class: " + clazz.getName());
				}
				if (instance.getPermissionNode() != null) {
					if (!instance.getPermissionNode().contains("craftconomy")) {
						fail("Fail permissionNode for class: " + clazz.getName());
					}
				}
				if (!instance.playerOnly() == false && !instance.playerOnly() == true) {
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
