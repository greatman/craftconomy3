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
package com.greatmancode.craftconomy3.currency;

import junit.framework.TestCase;

public class TestCurrency extends TestCase {
//TODO: Null tests
	public void testGetName() {
		Currency currency = new Currency(1, "test", "plural", "minor", "minorplural", 0.0, "$");
		if (!currency.getName().equals("test")) {
			fail("getName fail");
		}
	}

	public void testSetName() {
		Currency currency = new Currency(1, "test", "plural", "minor", "minorplural", 0.0, "$");
		currency.setName("testing");
		if (!currency.getName().equals("testing")) {
			fail("setName fail");
		}
	}

	public void testGetPlural() {
		Currency currency = new Currency(1, "test", "plural", "minor", "minorplural", 0.0, "$");
		if (!currency.getPlural().equals("plural")) {
			fail("getPlural fail");
		}
	}

	public void testSetPlural() {
		Currency currency = new Currency(1, "test", "plural", "minor", "minorplural", 0.0, "$");
		currency.setPlural("plurall");
		if (!currency.getPlural().equals("plurall")) {
			fail("setPlural fail");
		}
	}

	public void testGetMinor() {
		Currency currency = new Currency(1, "test", "plural", "minor", "minorplural", 0.0, "$");
		if (!currency.getMinor().equals("minor")) {
			fail("getMinor fail");
		}
	}

	public void testSetMinor() {
		Currency currency = new Currency(1, "test", "plural", "minor", "minorplural", 0.0, "$");
		currency.setMinor("minors");
		if (!currency.getMinor().equals("minors")) {
			fail("setMinor fail");
		}
	}

	public void testGetMinorPlural() {
		Currency currency = new Currency(1, "test", "plural", "minor", "minorplural", 0.0, "$");
		if (!currency.getMinorPlural().equals("minorplural")) {
			fail("getMinorPlural fail");
		}
	}

	public void testSetMinorPlural() {
		Currency currency = new Currency(1, "test", "plural", "minor", "minorplural", 0.0, "$");
		currency.setMinorPlural("minorPlurals");
		if (!currency.getMinorPlural().equals("minorPlurals")) {
			fail("setMinorPlural fail");
		}
	}

	public void testGetDatabaseID() {
		Currency currency = new Currency(1, "test", "plural", "minor", "minorplural", 0.0, "$");
		if (currency.getDatabaseID() != 1) {
			fail("getDatabaseID fail");
		}
	}

	public void testSetHardCap() {
		Currency currency = new Currency(1, "test", "plural", "minor", "minorplural", 0.0, "$");
		currency.setHardCap(300.0);
		if (currency.getHardCap() != 300.0) {
			fail("setHardCap fail");
		}
	}

	public void testGetHardCap() {
		Currency currency = new Currency(1, "test", "plural", "minor", "minorplural", 5.0, "$");
		if (currency.getHardCap() != 5.0) {
			fail("getHardCap fail");
		}
	}

	public void testSetSign() {
		Currency currency = new Currency(1, "test", "plural", "minor", "minorplural", 0.0, "$");
		currency.setSign("asdf");
		if (!currency.getSign().equals("asdf")) {
			fail("setSign fail");
		}
	}

	public void testGetSign() {
		Currency currency = new Currency(1, "test", "plural", "minor", "minorplural", 0.0, "$");
		if (!currency.getSign().equals("$")) {
			fail("getSign fail");
		}
	}

}
