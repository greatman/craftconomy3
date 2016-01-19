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
import com.greatmancode.craftconomy3.DisplayFormat;
import com.greatmancode.craftconomy3.TestInitializator;
import com.greatmancode.craftconomy3.commands.config.ConfigBankPriceCommand;
import com.greatmancode.craftconomy3.commands.config.ConfigFormatCommand;
import com.greatmancode.craftconomy3.commands.config.ConfigHoldingsCommand;
import org.junit.After;
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

    @After
    public void close() { Common.getInstance().onDisable();};

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
        command.execute(TEST_USER, new String[] {"signfront"});
        assertEquals(DisplayFormat.SIGNFRONT, Common.getInstance().getDisplayFormat());
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
        Common.getInstance().getAccountManager().getAccount(TEST_ACCOUNT3, false);
        assertEquals(200, Common.getInstance().getAccountManager().getAccount(TEST_ACCOUNT3, false).getBalance("default", Common.getInstance().getCurrencyManager().getDefaultCurrency().getName()), 0);

        command.execute(TEST_USER, new String[] {"-10"});
        assertEquals(200, Common.getInstance().getDefaultHoldings(), 0);
        Common.getInstance().getAccountManager().getAccount(TEST_ACCOUNT4, false);
        assertEquals(200, Common.getInstance().getAccountManager().getAccount(TEST_ACCOUNT4, false).getBalance("default", Common.getInstance().getCurrencyManager().getDefaultCurrency().getName()), 0);

        command.execute(TEST_USER, new String[] {"adjbf"});
        assertEquals(200, Common.getInstance().getDefaultHoldings(), 0);
        Common.getInstance().getAccountManager().getAccount(TEST_ACCOUNT2, false);
        assertEquals(200, Common.getInstance().getAccountManager().getAccount(TEST_ACCOUNT2, false).getBalance("default", Common.getInstance().getCurrencyManager().getDefaultCurrency().getName()), 0);

        command.execute(TEST_USER, new String[] {"0"});
        assertEquals(0, Common.getInstance().getDefaultHoldings(), 0);
        Common.getInstance().getAccountManager().getAccount(TEST_ACCOUNT, false);
        assertEquals(0, Common.getInstance().getAccountManager().getAccount(TEST_ACCOUNT, false).getBalance("default", Common.getInstance().getCurrencyManager().getDefaultCurrency().getName()), 0);
    }
}
