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

abstract class Converter {

	protected List<String> dbTypes = new ArrayList<String>();
	protected String selectedDbType;
	protected List<String> dbInfo = new ArrayList<String>();
	protected HashMap<String,String> dbConnectInfo = new HashMap<String,String>();
	
	public List<String> getDbTypes() {
		return dbTypes;
	}
	public boolean setDbType(String dbType) {
		boolean result = false;
		if (dbTypes.contains(dbType)) {
			selectedDbType = dbType;
			result = true;
		}
		return result;
	}
	public abstract List<String> getDbInfo();
	public boolean setDbInfo(String field, String value) {
		boolean result = false;
		if (dbInfo.contains(field)) {
			dbConnectInfo.put(field, value);
			result = true;
		}
		return result;
	}
	public abstract boolean connect();
	public abstract boolean importData(String sender);
	public boolean allSet() {
		return dbInfo.size() == dbConnectInfo.size();
	}
}
