package com.greatmancode.craftconomy3.commands;

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.DisplayFormat;
import com.greatmancode.craftconomy3.TestInitializator;
import com.greatmancode.craftconomy3.commands.config.ConfigBankPriceCommand;
import com.greatmancode.craftconomy3.commands.config.ConfigFormatCommand;
import com.greatmancode.craftconomy3.commands.config.ConfigHoldingsCommand;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestConfigCommands {

    private static final String TEST_USER = "testuser39";
    private static final String TEST_ACCOUNT = "testuser30";
    private static final String TEST_ACCOUNT2 = "Testuser31";
    private static final String TEST_ACCOUNT3 = "Testuser32";
    private static final String TEST_ACCOUNT4 = "Testuser34";
    @Before
    public void setUp() {
        new TestInitializator();
    }

    @Test
    public void testBankPriceCommand() {
        ConfigBankPriceCommand command = new ConfigBankPriceCommand();
        command.execute(TEST_USER, new String[] {"200"});
        assertEquals(200, Common.getInstance().getBankPrice(), 0);
        command.execute(TEST_USER, new String[] {"-10"});
        assertEquals(200, Common.getInstance().getBankPrice(), 0);
        command.execute(TEST_USER, new String[] {"adjbf"});
        assertEquals(200, Common.getInstance().getBankPrice(), 0);
        command.execute(TEST_USER, new String[] {"0"});
        assertEquals(0, Common.getInstance().getBankPrice(), 0);
    }

    @Test
    public void testFormatCommand() {
        ConfigFormatCommand command = new ConfigFormatCommand();
        command.execute(TEST_USER, new String[] {"long"});
        assertEquals(DisplayFormat.LONG, Common.getInstance().getDisplayFormat());
        command.execute(TEST_USER, new String[] {"sign"});
        assertEquals(DisplayFormat.SIGN, Common.getInstance().getDisplayFormat());
        command.execute(TEST_USER, new String[] {"majoronly"});
        assertEquals(DisplayFormat.MAJORONLY, Common.getInstance().getDisplayFormat());
        command.execute(TEST_USER, new String[] {"small"});
        assertEquals(DisplayFormat.SMALL, Common.getInstance().getDisplayFormat());
        command.execute(TEST_USER, new String[] {"0ewhf"});
        assertEquals(DisplayFormat.SMALL, Common.getInstance().getDisplayFormat());
    }

    @Test
    public void testHoldingsCommand() {
        ConfigHoldingsCommand command = new ConfigHoldingsCommand();

        command.execute(TEST_USER, new String[] {"200"});
        assertEquals(200, Common.getInstance().getDefaultHoldings(), 0);
        Common.getInstance().getAccountManager().getAccount(TEST_ACCOUNT3);
        assertEquals(200, Common.getInstance().getAccountManager().getAccount(TEST_ACCOUNT3).getBalance("default", Common.getInstance().getCurrencyManager().getDefaultCurrency().getName()), 0);

        command.execute(TEST_USER, new String[] {"-10"});
        assertEquals(200, Common.getInstance().getDefaultHoldings(), 0);
        Common.getInstance().getAccountManager().getAccount(TEST_ACCOUNT4);
        assertEquals(200, Common.getInstance().getAccountManager().getAccount(TEST_ACCOUNT4).getBalance("default", Common.getInstance().getCurrencyManager().getDefaultCurrency().getName()), 0);

        command.execute(TEST_USER, new String[] {"adjbf"});
        assertEquals(200, Common.getInstance().getDefaultHoldings(), 0);
        Common.getInstance().getAccountManager().getAccount(TEST_ACCOUNT2);
        assertEquals(200, Common.getInstance().getAccountManager().getAccount(TEST_ACCOUNT2).getBalance("default", Common.getInstance().getCurrencyManager().getDefaultCurrency().getName()), 0);

        command.execute(TEST_USER, new String[] {"0"});
        assertEquals(0, Common.getInstance().getDefaultHoldings(), 0);
        Common.getInstance().getAccountManager().getAccount(TEST_ACCOUNT);
        assertEquals(0, Common.getInstance().getAccountManager().getAccount(TEST_ACCOUNT).getBalance("default", Common.getInstance().getCurrencyManager().getDefaultCurrency().getName()), 0);
    }
}
