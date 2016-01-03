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

import com.greatmancode.craftconomy3.currency.Currency;

import junit.framework.TestCase;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestBalance {

    @Test
	public void testBalance() {
		new Balance(null, null, 0.0);
	}

    @Test
	public void testGetWorld() {
		Balance balance = new Balance("uberWorld", null, 0.0);
        assertEquals("uberWorld", balance.getWorld());
	}

    @Test
	public void testSetWorld() {
		Balance balance = new Balance("uberWorld", null, 0.0);
		balance.setWorld("uberWorld2");
        assertEquals("uberWorld2", balance.getWorld());
		balance.setWorld(null);
        assertNull(balance.getWorld());
	}

    @Test
	public void testGetCurrency() {
		Currency currency = new Currency("test", "plural", "minor", "minorplural", "$");
		Balance balance = new Balance("uberWorld", currency, 0.0);
        assertEquals(currency, balance.getCurrency());
	}

    @Test
	public void testSetCurrency() {
		Currency currency = new Currency("test", "plural", "minor", "minorplural","$");
		Balance balance = new Balance("uberWorld", null, 0.0);
		balance.setCurrency(currency);
        assertEquals(currency, balance.getCurrency());
	}

    @Test
	public void testGetBalance() {
		Balance balance = new Balance("uberWorld", null, 50.0);
        assertEquals(50.0, balance.getBalance(), 0);
	}

    @Test
	public void testSetBalance() {
		Balance balance = new Balance("uberWorld", null, 0.0);
		balance.setBalance(500.20);
        assertEquals(500.20, balance.getBalance(), 0);
	}
}
