package com.greatmancode.craftconomy3.commands;

import java.util.Iterator;

import org.reflections.Reflections;

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
			} catch (InstantiationException e) {
				fail(e.getMessage());
			} catch (IllegalAccessException e) {
				fail(e.getMessage());
			}
		}
	}
}
