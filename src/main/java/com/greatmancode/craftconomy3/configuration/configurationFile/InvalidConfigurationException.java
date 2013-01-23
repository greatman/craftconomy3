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
 * Exception thrown when attempting to load an invalid {@link Configuration}
 */
@SuppressWarnings("serial")
public class InvalidConfigurationException extends Exception {
	/**
	 * Creates a new instance of InvalidConfigurationException without a message or cause.
	 */
	public InvalidConfigurationException() {
	}

	/**
	 * Constructs an instance of InvalidConfigurationException with the specified message.
	 * @param msg The details of the exception.
	 */
	public InvalidConfigurationException(String msg) {
		super(msg);
	}

	/**
	 * Constructs an instance of InvalidConfigurationException with the specified cause.
	 * @param cause The cause of the exception.
	 */
	public InvalidConfigurationException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructs an instance of InvalidConfigurationException with the specified message and cause.
	 * @param cause The cause of the exception.
	 * @param msg The details of the exception.
	 */
	public InvalidConfigurationException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
