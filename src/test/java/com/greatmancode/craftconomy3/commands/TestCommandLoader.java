package com.greatmancode.craftconomy3.commands;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.TestInitializator;

public class TestCommandLoader
{

	@Before
	public void setUp() {
		new TestInitializator();
	}
	
	@Test
	public void testCommandExist(){
		assertFalse(Common.getInstance().getCommandManager().commandExist("oaishfoisdhfosidhfosdf"));
		assertTrue(Common.getInstance().getCommandManager().commandExist("money"));
	}
	
	@Test
	public void testgetCommandHandler() {
		assertNotNull(Common.getInstance().getCommandManager().getCommandHandler("money"));
		assertNull(Common.getInstance().getCommandManager().getCommandHandler("oahfoiahfoisdhfoisdhf"));
	}

}
