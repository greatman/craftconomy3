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
package com.greatmancode.craftconomy3.currency;

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.TestInitializator;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestCurrency {

    @Before
    public void setUp() {
        new TestInitializator();
    }

    @After
    public void close() { Common.getInstance().onDisable();};

	//TODO: Null tests
	@Test
    public void testGetName() {
		Currency currency = new Currency("test", "plural", "minor", "minorplural", "$");
		assertEquals("test", currency.getName());
	}

    @Test
	public void testSetName() {
		Currency currency = new Currency("test", "plural", "minor", "minorplural", "$");
		currency.setName("testing");
		assertEquals("testing", currency.getName());
	}

    @Test
	public void testGetPlural() {
		Currency currency = new Currency("test", "plural", "minor", "minorplural", "$");
		assertEquals("plural", currency.getPlural());
	}

    @Test
	public void testSetPlural() {
		Currency currency = new Currency("test", "plural", "minor", "minorplural", "$");
		currency.setPlural("plurall");
		assertEquals("plurall", currency.getPlural());
	}

    @Test
	public void testGetMinor() {
		Currency currency = new Currency("test", "plural", "minor", "minorplural", "$");
		assertEquals("minor", currency.getMinor());
	}

    @Test
	public void testSetMinor() {
		Currency currency = new Currency("test", "plural", "minor", "minorplural", "$");
		currency.setMinor("minors");
		assertEquals("minors", currency.getMinor());
	}

    @Test
	public void testGetMinorPlural() {
		Currency currency = new Currency("test", "plural", "minor", "minorplural", "$");
		assertEquals("minorplural", currency.getMinorPlural());
	}

    @Test
	public void testSetMinorPlural() {
		Currency currency = new Currency("test", "plural", "minor", "minorplural", "$");
		currency.setMinorPlural("minorPlurals");
		assertEquals("minorPlurals", currency.getMinorPlural());
	}

    @Test
	public void testSetSign() {
		Currency currency = new Currency("test", "plural", "minor", "minorplural", "$");
		currency.setSign("asdf");
		assertEquals("asdf", currency.getSign());
	}

    @Test
	public void testGetSign() {
		Currency currency = new Currency("test", "plural", "minor", "minorplural", "$");
		assertEquals("$", currency.getSign());
	}
}
