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
package com.greatmancode.craftconomy3.configuration;

/**
 * Represents a Configuration handler
 * @author greatman
 * 
 */
public abstract class Config {

	/**
	 * Retrieve a integer from the configuration
	 * @param path The path to the integer we want to retrieve
	 * @return A integer
	 */
	public abstract int getInt(String path);

	/**
	 * Retrieve a long from the configuration
	 * @param path The path to the long we want to retrieve
	 * @return A Long
	 */
	public abstract long getLong(String path);

	/**
	 * Retrieve a double from the configuration
	 * @param path The path to the double we want to retrieve
	 * @return A double
	 */
	public abstract double getDouble(String path);

	/**
	 * Retrieve a String from the configuration
	 * @param path The path to the String we want to retrieve
	 * @return A String
	 */
	public abstract String getString(String path);

	/**
	 * Retrieve a Boolean from the configuration
	 * @param path The path to the Boolean we want to retrieve
	 * @return A Boolean
	 */
	public abstract boolean getBoolean(String path);
	
	/**
	 * Set a value in the configuration.
	 * @param path The path to the value we want to modify
	 * @param value The new value.
	 */
	public abstract void setValue(String path, Object value);
}
