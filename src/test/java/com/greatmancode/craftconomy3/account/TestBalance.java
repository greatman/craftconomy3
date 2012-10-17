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
