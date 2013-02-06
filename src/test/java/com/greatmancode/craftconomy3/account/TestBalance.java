/*
 * This file is part of Craftconomy3.
 *
 * Copyright (c) 2011-2013, Greatman <http://github.com/greatman/>
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

public class TestBalance extends TestCase {
	public void testBalance() {
		Balance balance = new Balance(null, null, 0.0);
	}

	public void testGetWorld() {
		Balance balance = new Balance("uberWorld", null, 0.0);
		if (!balance.getWorld().equals("uberWorld")) {
			fail("getWorld fail.");
		}
	}

	public void testSetWorld() {
		Balance balance = new Balance("uberWorld", null, 0.0);
		balance.setWorld("uberWorld2");
		if (!balance.getWorld().equals("uberWorld2")) {
			fail("setWorld fail.");
		}
		balance.setWorld(null);
		if (balance.getWorld() != null) {
			fail("setWorld fail on null");
		}
	}

	public void testGetCurrency() {
		Currency currency = new Currency(1, "test", "plural", "minor", "minorplural", 0.0, "$");
		Balance balance = new Balance("uberWorld", currency, 0.0);
		if (!balance.getCurrency().equals(currency)) {
			fail("Fail getCurrency");
		}
	}

	public void testSetCurrency() {
		Currency currency = new Currency(1, "test", "plural", "minor", "minorplural", 0.0, "$");
		Balance balance = new Balance("uberWorld", null, 0.0);
		balance.setCurrency(currency);
		if (!balance.getCurrency().equals(currency)) {
			fail("Fail getCurrency");
		}
	}

	public void testGetBalance() {
		Balance balance = new Balance("uberWorld", null, 50.0);
		if (balance.getBalance() != 50.0) {
			fail("Fail getBalance()");
		}
	}

	public void testSetBalance() {
		Balance balance = new Balance("uberWorld", null, 0.0);
		balance.setBalance(500.20);
		if (balance.getBalance() != 500.20) {
			fail("Fail setBalance");
		}
	}
}
