/*
 * This file is part of Craftconomy3.
 *
 * Copyright (c) 2011-2012, Greatman <http://github.com/greatman/>
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

public class Tools {

	public static boolean isInteger(String number) {
		boolean result = false;
		try {
			Integer.parseInt(number);
			result = true;
		} catch (NumberFormatException e) {
		}
		return result;
	}

	public static boolean isLong(String number) {
		boolean result = false;
		try {
			Long.parseLong(number);
			result = true;
		} catch (NumberFormatException e) {
		}
		return result;
	}

	public static boolean isDouble(String number) {
		boolean result = false;
		try {
			Double.parseDouble(number);
			result = true;
		} catch (NumberFormatException e) {
		}
		return result;
	}

	public static boolean isValidDouble(String number) {
		boolean valid = false;
		if (isDouble(number)) {
			if (isPositive(Double.parseDouble(number))) {
				valid = true;
			}
		}
		return valid;
	}

	public static boolean isPositive(double number) {
		return number > 0.00;
	}
}
