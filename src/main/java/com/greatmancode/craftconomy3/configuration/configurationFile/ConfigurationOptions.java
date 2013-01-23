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
package com.greatmancode.craftconomy3.configuration.configurationFile;

/**
 * Various settings for controlling the input and output of a {@link Configuration}
 */
public class ConfigurationOptions {
	private char pathSeparator = '.';
	private boolean copyDefaults = false;
	private final Configuration configuration;

	protected ConfigurationOptions(Configuration configuration) {
		this.configuration = configuration;
	}

	/**
	 * Returns the {@link Configuration} that this object is responsible for.
	 * @return Parent configuration
	 */
	public Configuration configuration() {
		return configuration;
	}

	/**
	 * Gets the char that will be used to separate {@link ConfigurationSection}s
	 * <p/>
	 * This value does not affect how the {@link Configuration} is stored, only in
	 * how you access the data. The default value is '.'.
	 * @return Path separator
	 */
	public char pathSeparator() {
		return pathSeparator;
	}

	/**
	 * Sets the char that will be used to separate {@link ConfigurationSection}s
	 * <p/>
	 * This value does not affect how the {@link Configuration} is stored, only in
	 * how you access the data. The default value is '.'.
	 * @param value Path separator
	 * @return This object, for chaining
	 */
	public ConfigurationOptions pathSeparator(char value) {
		this.pathSeparator = value;
		return this;
	}

	/**
	 * Checks if the {@link Configuration} should copy values from its default {@link Configuration} directly.
	 * <p/>
	 * If this is true, all values in the default Configuration will be directly copied,
	 * making it impossible to distinguish between values that were set and values that
	 * are provided by default. As a result, {@link ConfigurationSection#contains(java.lang.String)} will always
	 * return the same value as {@link ConfigurationSection#isSet(java.lang.String)}.
	 * The default value is false.
	 * @return Whether or not defaults are directly copied
	 */
	public boolean copyDefaults() {
		return copyDefaults;
	}

	/**
	 * Sets if the {@link Configuration} should copy values from its default {@link Configuration} directly.
	 * <p/>
	 * If this is true, all values in the default Configuration will be directly copied,
	 * making it impossible to distinguish between values that were set and values that
	 * are provided by default. As a result, {@link ConfigurationSection#contains(java.lang.String)} will always
	 * return the same value as {@link ConfigurationSection#isSet(java.lang.String)}.
	 * The default value is false.
	 * @param value Whether or not defaults are directly copied
	 * @return This object, for chaining
	 */
	public ConfigurationOptions copyDefaults(boolean value) {
		this.copyDefaults = value;
		return this;
	}
}
