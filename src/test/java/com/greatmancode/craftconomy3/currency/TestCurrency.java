/*
 * This file is part of Craftconomy3.
 *
 * Copyright (c) 2011-2014, Greatman <http://github.com/greatman/>
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
package com.greatmancode.craftconomy3.currency;

import junit.framework.TestCase;

public class TestCurrency extends TestCase {
	//TODO: Null tests
	public void testGetName() {
		Currency currency = new Currency("test", "plural", "minor", "minorplural", "$");
		assertEquals("test", currency.getName());
	}

	public void testSetName() {
		Currency currency = new Currency("test", "plural", "minor", "minorplural", "$");
		currency.setName("testing");
		assertEquals("testing", currency.getName());
	}

	public void testGetPlural() {
		Currency currency = new Currency("test", "plural", "minor", "minorplural", "$");
		assertEquals("plural", currency.getPlural());
	}

	public void testSetPlural() {
		Currency currency = new Currency("test", "plural", "minor", "minorplural", "$");
		currency.setPlural("plurall");
		assertEquals("plurall", currency.getPlural());
	}

	public void testGetMinor() {
		Currency currency = new Currency("test", "plural", "minor", "minorplural", "$");
		assertEquals("minor", currency.getMinor());
	}

	public void testSetMinor() {
		Currency currency = new Currency("test", "plural", "minor", "minorplural", "$");
		currency.setMinor("minors");
		assertEquals("minors", currency.getMinor());
	}

	public void testGetMinorPlural() {
		Currency currency = new Currency("test", "plural", "minor", "minorplural", "$");
		assertEquals("minorplural", currency.getMinorPlural());
	}

	public void testSetMinorPlural() {
		Currency currency = new Currency("test", "plural", "minor", "minorplural", "$");
		currency.setMinorPlural("minorPlurals");
		assertEquals("minorPlurals", currency.getMinorPlural());
	}

	public void testSetSign() {
		Currency currency = new Currency("test", "plural", "minor", "minorplural", "$");
		currency.setSign("asdf");
		assertEquals("asdf", currency.getSign());
	}

	public void testGetSign() {
		Currency currency = new Currency("test", "plural", "minor", "minorplural", "$");
		assertEquals("$", currency.getSign());
	}
}
