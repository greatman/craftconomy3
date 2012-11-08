package com.greatmancode.craftconomy3.commands;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.TestInitializator;

public class TestLoadedCommands {

	@Before
	public void setUp() {
		new TestInitializator();
	}
	
	@Test
	public void testBankBalanceCommand() {
		Common.getInstance().getCommandManager().getCommandHandler("bank").execute("console", "balance", null);
	}

}
