/**
 * This file is part of Craftconomy3.
 *
 * Copyright (c) 2011-2016, Greatman <http://github.com/greatman/>
 * Copyright (c) 2017, Aztorius <http://github.com/Aztorius/>
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
import com.greatmancode.craftconomy3.TestCommandSender;
import com.greatmancode.craftconomy3.TestInitializator;
import com.greatmancode.craftconomy3.commands.money.BalanceCommand;
import com.greatmancode.craftconomy3.commands.money.CreateCommand;
import com.greatmancode.craftconomy3.commands.money.DeleteCommand;
import com.greatmancode.craftconomy3.commands.money.GiveCommand;
import com.greatmancode.tools.commands.PlayerCommandSender;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by greatman on 2014-07-02.
 */
public class TestMoneyCommands {

    private static PlayerCommandSender TEST_USER;
    private static PlayerCommandSender TEST_USER2;

    @Before
    public void setUp() {
        new TestInitializator();
        TEST_USER = createTestUser("testuser39");
        TEST_USER2 = createTestUser("testuser40");
    }

    @After
    public void close() { Common.getInstance().onDisable();}
    
    @Test
    public void testBalanceCommand() {
        Common.getInstance().getAccountManager().getAccount(TEST_USER.getName(), false);
        BalanceCommand command = new BalanceCommand(null);
        command.execute(TEST_USER,new String[]{TEST_USER.getName()});
        command.execute(TEST_USER, new String[]{"unknown"});
    }

    @Test
    public void testCreateCommand() {
        CreateCommand command = new CreateCommand(null);
        command.execute(TEST_USER, new String[] {"testaccount"});
        assertTrue(Common.getInstance().getAccountManager().exist("testaccount", false));
        command.execute(TEST_USER, new String[] {"testaccount"});
    }

    @Test
    public void testDeleteCommand() {
        Common.getInstance().getAccountManager().getAccount("testaccount", false);
        DeleteCommand command = new DeleteCommand(null);
        command.execute(TEST_USER, new String[] {"testaccount"});
        assertFalse(Common.getInstance().getAccountManager().exist("testaccount", false));
    }

    @Test
    public void testGiveCommand() {
        Common.getInstance().getAccountManager().getAccount(TEST_USER.getName(), false);
        double initialValue = Common.getInstance().getAccountManager().getAccount(TEST_USER.getName(), false).getBalance("default", Common.getInstance().getCurrencyManager().getDefaultCurrency().getName());
        GiveCommand command = new GiveCommand(null);
        command.execute(TEST_USER, new String[]{TEST_USER.getName(), "200"});
        assertEquals(initialValue + 200, Common.getInstance().getAccountManager().getAccount(TEST_USER.getName(), false).getBalance("default", Common.getInstance().getCurrencyManager().getDefaultCurrency().getName()),0);
        command.execute(TEST_USER, new String[] {TEST_USER.getName(), "di3"});
        assertEquals(initialValue + 200, Common.getInstance().getAccountManager().getAccount(TEST_USER.getName(), false).getBalance("default", Common.getInstance().getCurrencyManager().getDefaultCurrency().getName()),0);
        command.execute(TEST_USER, new String[]{TEST_USER2.getName(), "200"});
        assertEquals(initialValue + 200, Common.getInstance().getAccountManager().getAccount(TEST_USER.getName(), false).getBalance("default", Common.getInstance().getCurrencyManager().getDefaultCurrency().getName()),0);
        command.execute(TEST_USER, new String[]{TEST_USER.getName(), "200", "Dollar"});
        assertEquals(initialValue + 400, Common.getInstance().getAccountManager().getAccount(TEST_USER.getName(), false).getBalance("default", Common.getInstance().getCurrencyManager().getDefaultCurrency().getName()),0);
        command.execute(TEST_USER, new String[] {TEST_USER.getName(), "200", "fake"});
        assertEquals(initialValue + 400, Common.getInstance().getAccountManager().getAccount(TEST_USER.getName(), false).getBalance("default", Common.getInstance().getCurrencyManager().getDefaultCurrency().getName()),0);
    }
    
    private PlayerCommandSender createTestUser(String name){
        UUID uuid = UUID.randomUUID();
        return new PlayerCommandSender<>(name,uuid,new TestCommandSender(uuid,name));
    }
}
