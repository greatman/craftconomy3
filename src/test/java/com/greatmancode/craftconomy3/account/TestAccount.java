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
package com.greatmancode.craftconomy3.account;

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.TestInitializator;
import com.greatmancode.tools.caller.unittest.UnitTestServerCaller;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestAccount {

	@Before
	public void setUp() {
		new TestInitializator();
	}

    @After
    public void close() { Common.getInstance().onDisable();};

    @Test
	public void testAccount() {
        Account account = Common.getInstance().getAccountManager().getAccount("greatman321", false);
        assertEquals(100, Common.getInstance().getAccountManager().getAccount("greatman321", false).getBalance(UnitTestServerCaller.worldName, Common.getInstance().getCurrencyManager().getDefaultBankCurrency().getName()), 0);
		account.deposit(50.0, UnitTestServerCaller.worldName, Common.getInstance().getCurrencyManager().getDefaultCurrency().getName());
		assertTrue(account.hasEnough(50.0, UnitTestServerCaller.worldName, Common.getInstance().getCurrencyManager().getDefaultCurrency().getName()));
		account.set(0, UnitTestServerCaller.worldName, Common.getInstance().getCurrencyManager().getDefaultCurrency().getName());
		assertTrue(account.hasEnough(0, UnitTestServerCaller.worldName, Common.getInstance().getCurrencyManager().getDefaultCurrency().getName()));
		account.deposit(0.009999999993056037, UnitTestServerCaller.worldName, Common.getInstance().getCurrencyManager().getDefaultCurrency().getName());
		assertFalse(account.hasEnough(0.01, UnitTestServerCaller.worldName, Common.getInstance().getCurrencyManager().getDefaultCurrency().getName()));
		account.deposit(50.00999999934834832463284, UnitTestServerCaller.worldName, Common.getInstance().getCurrencyManager().getDefaultCurrency().getName());
		assertFalse(account.hasEnough(50.01, UnitTestServerCaller.worldName, Common.getInstance().getCurrencyManager().getDefaultCurrency().getName()));
		assertTrue(account.hasEnough(50.00, UnitTestServerCaller.worldName, Common.getInstance().getCurrencyManager().getDefaultCurrency().getName()));

		Account account2 = Common.getInstance().getAccountManager().getAccount("test123", false);
		account.withdraw(0.35, UnitTestServerCaller.worldName, Common.getInstance().getCurrencyManager().getDefaultCurrency().getName());
		account2.deposit(0.35, UnitTestServerCaller.worldName, Common.getInstance().getCurrencyManager().getDefaultCurrency().getName());
		assertTrue(account.hasEnough(49.65, UnitTestServerCaller.worldName, Common.getInstance().getCurrencyManager().getDefaultCurrency().getName()));
		assertTrue(account2.hasEnough(0.35, UnitTestServerCaller.worldName, Common.getInstance().getCurrencyManager().getDefaultCurrency().getName()));

		account2.setInfiniteMoney(true);
		assertTrue(account2.hasEnough(99999999, UnitTestServerCaller.worldName, Common.getInstance().getCurrencyManager().getDefaultCurrency().getName()));
		account2.setInfiniteMoney(false);
		assertFalse(account2.hasEnough(99999999, UnitTestServerCaller.worldName, Common.getInstance().getCurrencyManager().getDefaultCurrency().getName()));

	}
}
