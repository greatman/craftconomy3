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
package com.greatmancode.craftconomy3.utils;

/**
 * General tools for Craftconomy
 * @author greatman
 */
public class Tools {
	protected Tools() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Checks if a string is a valid integer
	 * @param number The string to check
	 * @return True if the string is a valid integer else false.
	 */
	public static boolean isInteger(String number) {
		boolean result = false;
		try {
			Integer.parseInt(number);
			result = true;
		} catch (NumberFormatException e) {
		}
		return result;
	}

	/**
	 * Checks if a string is a valid long
	 * @param number The string to check
	 * @return True if the string is a valid long else false.
	 */
	public static boolean isLong(String number) {
		boolean result = false;
		try {
			Long.parseLong(number);
			result = true;
		} catch (NumberFormatException e) {
		}
		return result;
	}

	/**
	 * Checks if a string is a valid Double
	 * @param number The string to check.
	 * @return True if the string is a valid double else false.
	 */
	public static boolean isDouble(String number) {
		boolean result = false;
		if (number != null) {
			try {
				Double.parseDouble(number);
				result = true;
			} catch (NumberFormatException e) {
			}
		}
		return result;
	}

	/**
	 * Checks if a string is a valid boolean
	 * @param number The string to check
	 * @return True if the string is equals to either true or false . Else false.
	 */
	public static boolean isBoolean(String number) {
		boolean result = false;
		if (number != null && (number.equalsIgnoreCase("true") || number.equalsIgnoreCase("false"))) {
			result = true;
		}
		return result;
	}

	/**
	 * Checks if a string is a valid double.
	 * @param number The string to check
	 * @return True if the number is a valid double (positive) else false.
	 */
	public static boolean isValidDouble(String number) {
		boolean valid = false;
		if (isDouble(number) && isPositive(Double.parseDouble(number))) {
			valid = true;
		}
		return valid;
	}

	/**
	 * Checks if a double is positive.
	 * @param number The number to check
	 * @return True if the number is positive else false.
	 */
	public static boolean isPositive(double number) {
		return number >= 0.00;
	}
}
