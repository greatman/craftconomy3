package com.greatmancode.craftconomy3.commands;

import java.util.Iterator;

import org.reflections.Reflections;

import junit.framework.TestCase;

public class TestCommands extends TestCase {

	public void testCommands() {
		Reflections reflections = new Reflections("com.greatmancode.craftconomy3.commands.bank");
		Iterator<Class<? extends CraftconomyCommand>> allClasses = reflections.getSubTypesOf(CraftconomyCommand.class).iterator();
		while (allClasses.hasNext()) {
			Class<? extends CraftconomyCommand> clazz = allClasses.next();
			try {
				CraftconomyCommand instance = clazz.newInstance();
				System.out.println("Testing : " + instance.help());
				if (!(instance.help() instanceof String)) {
					fail("Fail!");
				}
			} catch (InstantiationException e) {
				fail(e.getMessage());
			} catch (IllegalAccessException e) {
				fail(e.getMessage());
			}
		}
	}
}
