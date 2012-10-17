package com.greatmancode.craftconomy3.utils;

import junit.framework.TestCase;

public class TestTools extends TestCase {

	public void testConstructor() {
		try
		{
			new Tools();
			fail("Tools instancied!");
		}
		catch(UnsupportedOperationException e) {
		}
	}
	public void testIsInteger() {
		assertTrue(Tools.isInteger(Integer.MAX_VALUE + ""));
		assertTrue(Tools.isInteger(Integer.MIN_VALUE + ""));
		assertTrue(Tools.isInteger("0"));
		assertTrue(Tools.isInteger(Short.MAX_VALUE + ""));
		assertTrue(Tools.isInteger(Short.MIN_VALUE + ""));
		assertTrue(Tools.isInteger(Byte.MAX_VALUE + ""));
		assertTrue(Tools.isInteger(Byte.MIN_VALUE + ""));
		assertFalse(Tools.isInteger(Long.MAX_VALUE + ""));
		assertFalse(Tools.isInteger(Long.MIN_VALUE + ""));
		assertFalse(Tools.isInteger(Float.MAX_VALUE + ""));
		assertFalse(Tools.isInteger(Float.MIN_VALUE + ""));
		assertFalse(Tools.isInteger(Double.MAX_VALUE + ""));
		assertFalse(Tools.isInteger(Double.MIN_VALUE + ""));
		assertFalse(Tools.isInteger("test"));
	}

	public void testIsLong() {
		assertTrue(Tools.isLong(Integer.MAX_VALUE + ""));
		assertTrue(Tools.isLong(Integer.MIN_VALUE + ""));
		assertTrue(Tools.isLong("0"));
		assertTrue(Tools.isLong(Short.MAX_VALUE + ""));
		assertTrue(Tools.isLong(Short.MIN_VALUE + ""));
		assertTrue(Tools.isLong(Byte.MAX_VALUE + ""));
		assertTrue(Tools.isLong(Byte.MIN_VALUE + ""));
		assertTrue(Tools.isLong(Long.MAX_VALUE + ""));
		assertTrue(Tools.isLong(Long.MIN_VALUE + ""));
		assertFalse(Tools.isLong(Float.MAX_VALUE + ""));
		assertFalse(Tools.isLong(Float.MIN_VALUE + ""));
		assertFalse(Tools.isLong(Double.MAX_VALUE + ""));
		assertFalse(Tools.isLong(Double.MIN_VALUE + ""));
		assertFalse(Tools.isLong("test"));
	}

	public void testIsDouble() {
		assertTrue(Tools.isDouble(Integer.MAX_VALUE + ""));
		assertTrue(Tools.isDouble(Integer.MIN_VALUE + ""));
		assertTrue(Tools.isDouble("0"));
		assertTrue(Tools.isDouble(Short.MAX_VALUE + ""));
		assertTrue(Tools.isDouble(Short.MIN_VALUE + ""));
		assertTrue(Tools.isDouble(Byte.MAX_VALUE + ""));
		assertTrue(Tools.isDouble(Byte.MIN_VALUE + ""));
		assertTrue(Tools.isDouble(Long.MAX_VALUE + ""));
		assertTrue(Tools.isDouble(Long.MIN_VALUE + ""));
		assertTrue(Tools.isDouble(Float.MAX_VALUE + ""));
		assertTrue(Tools.isDouble(Float.MIN_VALUE + ""));
		assertTrue(Tools.isDouble(Double.MAX_VALUE + ""));
		assertTrue(Tools.isDouble(Double.MIN_VALUE + ""));
		assertFalse(Tools.isDouble("test"));
	}

	public void testIsBoolean() {
		assertFalse(Tools.isBoolean(Integer.MAX_VALUE + ""));
		assertFalse(Tools.isBoolean(Integer.MIN_VALUE + ""));
		assertFalse(Tools.isBoolean("0"));
		assertFalse(Tools.isBoolean(Short.MAX_VALUE + ""));
		assertFalse(Tools.isBoolean(Short.MIN_VALUE + ""));
		assertFalse(Tools.isBoolean(Byte.MAX_VALUE + ""));
		assertFalse(Tools.isBoolean(Byte.MIN_VALUE + ""));
		assertFalse(Tools.isBoolean(Long.MAX_VALUE + ""));
		assertFalse(Tools.isBoolean(Long.MIN_VALUE + ""));
		assertFalse(Tools.isBoolean(Float.MAX_VALUE + ""));
		assertFalse(Tools.isBoolean(Float.MIN_VALUE + ""));
		assertFalse(Tools.isBoolean(Double.MAX_VALUE + ""));
		assertFalse(Tools.isBoolean(Double.MIN_VALUE + ""));
		assertFalse(Tools.isBoolean("test"));
		assertTrue(Tools.isBoolean("true"));
		assertTrue(Tools.isBoolean("false"));
	}

	public void testIsValidDouble() {
		assertTrue(Tools.isValidDouble(Integer.MAX_VALUE + ""));
		assertFalse(Tools.isValidDouble(Integer.MIN_VALUE + ""));
		assertTrue(Tools.isValidDouble("0"));
		assertTrue(Tools.isValidDouble(Short.MAX_VALUE + ""));
		assertFalse(Tools.isValidDouble(Short.MIN_VALUE + ""));
		assertTrue(Tools.isValidDouble(Byte.MAX_VALUE + ""));
		assertFalse(Tools.isValidDouble(Byte.MIN_VALUE + ""));
		assertTrue(Tools.isValidDouble(Long.MAX_VALUE + ""));
		assertFalse(Tools.isValidDouble(Long.MIN_VALUE + ""));
		assertTrue(Tools.isValidDouble(Float.MAX_VALUE + ""));
		//assertFalse(Tools.isValidDouble(Float.MIN_VALUE + ""));
		assertTrue(Tools.isValidDouble(Double.MAX_VALUE + ""));
		//assertFalse(Tools.isValidDouble(Double.MIN_VALUE + ""));
		assertFalse(Tools.isValidDouble("test"));
	}

	public void testIsPositive() {
		//fail("Not yet implemented");
	}

}
