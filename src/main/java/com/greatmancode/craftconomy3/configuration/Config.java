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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import com.greatmancode.craftconomy3.Common;

/**
 * Represents a Configuration handler
 * @author greatman
 * 
 */
public abstract class Config {

	private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
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
	
	public abstract boolean has(String path);
	
	protected void initializeConfig(File file, String fileName) {
		try {
			file.getParentFile().mkdirs();
			file.createNewFile();
		} catch (IOException e) {
			Common.getInstance().getLogger().severe("Error while trying to create the file " + file.getName() + "! Error is: " + e.getMessage());
		}
		URL url = this.getClass().getResource("/" + fileName);
		
		if (url != null) {
			try {
				InputStream defaultStream = url.openStream();
				FileOutputStream fos = new FileOutputStream(file);
				
				byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
		        int n = 0;
		        while (-1 != (n = defaultStream.read(buffer))) {
		            fos.write(buffer, 0, n);
		        }
		        fos.flush();
		        fos.close();
		        defaultStream.close();
			} catch (IOException e1) {
				Common.getInstance().getLogger().severe("Error while trying to copy the default file + " +file.getName() + ". Error is: " + e1.getMessage());
			}
		}
	}
}
