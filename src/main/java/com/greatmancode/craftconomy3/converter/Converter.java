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
package com.greatmancode.craftconomy3.converter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a Converter
 * 
 * @author greatman
 * 
 */
public abstract class Converter {

	public static final int ALERT_EACH_X_ACCOUNT = 10;
	/**
	 * Contains the type of Database (flatfile, sqlite, etc.) supported by the originating plugin
	 */
	protected List<String> dbTypes = new ArrayList<String>();

	/**
	 * Contains the selected Db Type.
	 */
	protected String selectedDbType;

	/**
	 * Contains all the required fields about the selected database type
	 */
	protected List<String> dbInfo = new ArrayList<String>();

	/**
	 * Contains all the information about the required fields entered by the user.
	 */
	protected Map<String, String> dbConnectInfo = new HashMap<String, String>();

	/**
	 * Retrieve a list of all the database type.
	 * 
	 * @return A list of database type.
	 */
	public List<String> getDbTypes() {
		return dbTypes;
	}

	/**
	 * Sets the selected database type.
	 * 
	 * @param dbType The database type selected
	 * @return True if the database type has been saved else false (A invalid type)
	 */
	public boolean setDbType(String dbType) {
		boolean result = false;
		if (dbTypes.contains(dbType)) {
			selectedDbType = dbType;
			result = true;
		}
		return result;
	}

	/**
	 * Retrieve the list of required fields about the selected database type
	 * 
	 * @return A list of required fields to connect to the selected database type
	 */
	public abstract List<String> getDbInfo();

	/**
	 * Sets a field information for the selected database type
	 * 
	 * @param field The field name.
	 * @param value The value of the field.
	 * @return True if the field has been saved else false (A invalid field)
	 */
	public boolean setDbInfo(String field, String value) {
		boolean result = false;
		if (dbInfo.contains(field)) {
			dbConnectInfo.put(field, value);
			result = true;
		}
		return result;
	}

	/**
	 * Checks if we filled all the required fields
	 * 
	 * @return True if all fields has been filled else false.
	 */
	public boolean allSet() {
		return dbInfo.size() == dbConnectInfo.size();
	}

	/**
	 * Connects to the database
	 * 
	 * @return True if the connection is successful else false.
	 */
	public abstract boolean connect();

	/**
	 * Import all the data into Craftconomy
	 * 
	 * @param sender The name of the sender so we can send status update.
	 * @return True if everything went well else false.
	 */
	public abstract boolean importData(String sender);
}
