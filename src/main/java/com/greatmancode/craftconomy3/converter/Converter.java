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
package com.greatmancode.craftconomy3.converter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.alta189.simplesave.sqlite.SQLiteDatabase;
import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.database.tables.AccountTable;

/**
 * Represents a Converter
 * @author greatman
 */
public abstract class Converter {
	public static final int ALERT_EACH_X_ACCOUNT = 10;
	private StringBuilder stringBuilder = new StringBuilder();
	/**
	 * Contains the type of Database (flatfile, sqlite, etc.) supported by the originating plugin
	 */
	private final List<String> dbTypes = new ArrayList<String>();
	/**
	 * Contains the selected Db Type.
	 */
	private String selectedDbType;
	/**
	 * Contains all the required fields about the selected database type
	 */
	private final List<String> dbInfo = new ArrayList<String>();
	/**
	 * Contains all the information about the required fields entered by the user.
	 */
	private final Map<String, String> dbConnectInfo = new HashMap<String, String>();

	/**
	 * Retrieve a list of all the database type.
	 * @return A list of database type.
	 */
	public List<String> getDbTypes() {
		return dbTypes;
	}

	protected List<String> getDbInfoList() {
		return dbInfo;
	}

	/**
	 * Sets the selected database type.
	 * @param dbType The database type selected
	 * @return True if the database type has been saved else false (A invalid type)
	 */
	public boolean setDbType(String dbType) {
		boolean result = false;
		if (dbTypes.contains(dbType)) {
			setSelectedDbType(dbType);
			result = true;
		}
		return result;
	}

	/**
	 * Retrieve the list of required fields about the selected database type
	 * @return A list of required fields to connect to the selected database type
	 */
	public abstract List<String> getDbInfo();

	/**
	 * Sets a field information for the selected database type
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

	public Map<String, String> getDbConnectInfo() {
		return dbConnectInfo;
	}

	/**
	 * Checks if we filled all the required fields
	 * @return True if all fields has been filled else false.
	 */
	public boolean allSet() {
		return dbInfo.size() == dbConnectInfo.size();
	}

	/**
	 * Connects to the database
	 * @return True if the connection is successful else false.
	 */
	public abstract boolean connect();

	/**
	 * Import all the data into Craftconomy
	 * @param sender The name of the sender so we can send status update.
	 * @return True if everything went well else false.
	 */
	public abstract boolean importData(String sender);

	public String getSelectedDbType() {
		return selectedDbType;
	}

	public void setSelectedDbType(String selectedDbType) {
		this.selectedDbType = selectedDbType;
	}

	//Should be used in INSERT TO cc3_account
	protected void addAccountToString(List<User> userList) {
		stringBuilder = new StringBuilder();
		stringBuilder.append("INSERT INTO cc3_account(name) VALUES ");
		Iterator<User> iterator = userList.iterator();
		boolean first = true, isSQLite = Common.getInstance().getDatabaseManager().getDatabase() instanceof SQLiteDatabase;
		while (iterator.hasNext()) {
			if (isSQLite && !first) {
				stringBuilder = new StringBuilder();
				stringBuilder.append("INSERT INTO cc3_account(name) VALUES ");
			}
			User user = iterator.next();
			stringBuilder.append("('" + user.user + "')");
			if (!isSQLite && iterator.hasNext()) {
				stringBuilder.append(",");
			} else {
				Common.getInstance().getDatabaseManager().getDatabase().directQuery(stringBuilder.toString());
				first = false;
			}
		}
		if (!isSQLite) {
			Common.getInstance().getDatabaseManager().getDatabase().directQuery(stringBuilder.toString());
		}
	}

	protected void addBalance(String sender, List<User> userList) {
		int i = 0;
		stringBuilder = new StringBuilder();
		String worldName = Common.getInstance().getServerCaller().getDefaultWorld();

		stringBuilder.append("INSERT INTO cc3_balance(username_id, currency_id, worldName,balance) VALUES ");
		Iterator<User> iterator = userList.iterator();
		boolean first = true, isSQLite = Common.getInstance().getDatabaseManager().getDatabase() instanceof SQLiteDatabase;
		while (iterator.hasNext()) {
			if (i % ALERT_EACH_X_ACCOUNT == 0) {
				Common.getInstance().getServerCaller().sendMessage(sender, i + " {{DARK_GREEN}}of {{WHITE}} " + userList.size() + " {{DARK_GREEN}}accounts ready to be imported.");
			}
			if (isSQLite && !first) {
				stringBuilder = new StringBuilder();
				stringBuilder.append("INSERT INTO cc3_balance(username_id, currency_id, worldName,balance) VALUES ");
			}
			User user = iterator.next();
			AccountTable account = Common.getInstance().getDatabaseManager().getDatabase().select(AccountTable.class).where().equal("name", user.user).execute().findOne();
			stringBuilder.append("(" + account.getId() + "," + Common.getInstance().getCurrencyManager().getDefaultCurrency().getDatabaseID() + ",'" + worldName + "'," + user.balance + ")");
			if (!isSQLite && iterator.hasNext()) {
				stringBuilder.append(",");
			} else {
				Common.getInstance().getDatabaseManager().getDatabase().directQuery(stringBuilder.toString());
				first = false;
			}
			i++;
		}
		if (!isSQLite) {
			Common.getInstance().getDatabaseManager().getDatabase().directQuery(stringBuilder.toString());
		}

		Common.getInstance().getServerCaller().sendMessage(sender, userList.size() + " {{DARK_GREEN}}accounts converted! Enjoy!");
	}

	protected class User {
		public String user;
		public double balance;

		public User(String user, double balance) {
			this.user = user;
			this.balance = balance;
		}
	}
}
