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
import com.greatmancode.craftconomy3.commands.money.BalanceCommand;
import com.greatmancode.craftconomy3.commands.money.CreateCommand;
import com.greatmancode.craftconomy3.commands.money.DeleteCommand;
import com.greatmancode.craftconomy3.commands.money.GiveCommand;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by greatman on 2014-07-02.
 */
public class TestMoneyCommands {

    private static final String TEST_USER = "testuser39";
    private static final String TEST_USER2 = "testuser40";

    @Before
    public void setUp() {
        new TestInitializator();
    }

    @After
    public void close() { Common.getInstance().onDisable();};

    @Test
    public void testBalanceCommand() {
        Common.getInstance().getAccountManager().getAccount(TEST_USER, false);
        BalanceCommand command = new BalanceCommand();
        command.execute(TEST_USER,new String[]{TEST_USER});
        command.execute(TEST_USER, new String[]{"unknown"});
    }

    @Test
    public void testCreateCommand() {
        CreateCommand command = new CreateCommand();
        command.execute(TEST_USER, new String[] {"testaccount"});
        assertTrue(Common.getInstance().getAccountManager().exist("testaccount", false));
        command.execute(TEST_USER, new String[] {"testaccount"});
    }

    @Test
    public void testDeleteCommand() {
        Common.getInstance().getAccountManager().getAccount("testaccount", false);
        DeleteCommand command = new DeleteCommand();
        command.execute(TEST_USER, new String[] {"testaccount"});
        assertFalse(Common.getInstance().getAccountManager().exist("testaccount", false));
    }

    @Test
    public void testGiveCommand() {
        Common.getInstance().getAccountManager().getAccount(TEST_USER, false);
        double initialValue = Common.getInstance().getAccountManager().getAccount(TEST_USER, false).getBalance("default", Common.getInstance().getCurrencyManager().getDefaultCurrency().getName());
        GiveCommand command = new GiveCommand();
        command.execute(TEST_USER, new String[]{TEST_USER, "200"});
        assertEquals(initialValue + 200, Common.getInstance().getAccountManager().getAccount(TEST_USER, false).getBalance("default", Common.getInstance().getCurrencyManager().getDefaultCurrency().getName()),0);
        command.execute(TEST_USER, new String[] {TEST_USER, "di3"});
        assertEquals(initialValue + 200, Common.getInstance().getAccountManager().getAccount(TEST_USER, false).getBalance("default", Common.getInstance().getCurrencyManager().getDefaultCurrency().getName()),0);
        command.execute(TEST_USER, new String[]{TEST_USER2, "200"});
        assertEquals(initialValue + 200, Common.getInstance().getAccountManager().getAccount(TEST_USER, false).getBalance("default", Common.getInstance().getCurrencyManager().getDefaultCurrency().getName()),0);
        command.execute(TEST_USER, new String[]{TEST_USER, "200", "Dollar"});
        assertEquals(initialValue + 400, Common.getInstance().getAccountManager().getAccount(TEST_USER, false).getBalance("default", Common.getInstance().getCurrencyManager().getDefaultCurrency().getName()),0);
        command.execute(TEST_USER, new String[] {TEST_USER, "200", "fake"});
        assertEquals(initialValue + 400, Common.getInstance().getAccountManager().getAccount(TEST_USER, false).getBalance("default", Common.getInstance().getCurrencyManager().getDefaultCurrency().getName()),0);
    }
}
