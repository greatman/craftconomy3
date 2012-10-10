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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import com.alta189.simplesave.Database;
import com.alta189.simplesave.DatabaseFactory;
import com.alta189.simplesave.exceptions.ConnectionException;
import com.alta189.simplesave.exceptions.TableRegistrationException;
import com.alta189.simplesave.mysql.MySQLConfiguration;
import com.alta189.simplesave.sqlite.SQLiteConfiguration;
import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.currency.CurrencyManager;
import com.greatmancode.craftconomy3.database.tables.iconomy.IConomyTable;

/**
 * Converter for iConomy 6.
 * 
 * @author greatman
 * 
 */
public class Iconomy6 extends Converter {

	private BufferedReader flatFileReader = null;
	private Database db = null;

	public Iconomy6() {
		dbTypes.add("flatfile");
		dbTypes.add("minidb");
		dbTypes.add("sqlite");
		dbTypes.add("mysql");
	}

	@Override
	public List<String> getDbInfo() {

		if (selectedDbType.equals("flatfile") || selectedDbType.equals("minidb") || selectedDbType.equals("sqlite")) {
			dbInfo.add("filename");
		} else if (selectedDbType.equals("mysql")) {
			dbInfo.add("address");
			dbInfo.add("port");
			dbInfo.add("username");
			dbInfo.add("password");
			dbInfo.add("database");
		}
		return dbInfo;
	}

	@Override
	public boolean connect() {
		boolean result = false;
		if (selectedDbType.equals("flatfile") || selectedDbType.equals("minidb")) {
			result = loadFile();
		} else if (selectedDbType.equals("mysql")) {
			loadMySQL();
		} else if (selectedDbType.equals("sqlite")) {
			loadSQLite();
		}

		if (db != null) {

			try {
				db.registerTable(IConomyTable.class);
				db.setCheckTableOnRegistration(false);
				db.connect();
				result = true;
			} catch (TableRegistrationException e) {
				Common.getInstance().getLogger().severe("Unable to register iConomy tables. Reason: " + e.getMessage());
			} catch (ConnectionException e) {
				Common.getInstance().getLogger().severe("Unable to connect to iConomy database. Reason: " + e.getMessage());
			}

		}
		return result;
	}

	private boolean loadFile() {
		boolean result = false;
		File dbFile = new File(Common.getInstance().getServerCaller().getDataFolder(), dbConnectInfo.get("filename"));
		if (dbFile.exists()) {
			try {
				flatFileReader = new BufferedReader(new FileReader(dbFile));
				result = true;
			} catch (FileNotFoundException e) {
				Common.getInstance().getLogger().severe("iConomy database file not found!");
			}
		}
		return result;
	}

	private void loadMySQL() {
		try {
			MySQLConfiguration config = new MySQLConfiguration();
			config.setHost(dbConnectInfo.get("address"));
			config.setUser(dbConnectInfo.get("username"));
			config.setPassword(dbConnectInfo.get("password"));
			config.setDatabase(dbConnectInfo.get("database"));
			config.setPort(Integer.parseInt(dbConnectInfo.get("port")));
			db = DatabaseFactory.createNewDatabase(config);
		} catch (NumberFormatException e) {
			Common.getInstance().getLogger().severe("Illegal Port!");
		}
	}

	private void loadSQLite() {
		SQLiteConfiguration config = new SQLiteConfiguration(Common.getInstance().getServerCaller().getDataFolder() + File.separator + dbConnectInfo.get("filename"));
		db = DatabaseFactory.createNewDatabase(config);
	}

	@Override
	public boolean importData(String sender) {
		boolean result = false;
		if (flatFileReader != null) {
			result = importFlatFile(sender);
		} else if (db != null) {
			result = importDatabase(sender);
		}
		return result;
	}
	
	private boolean importFlatFile(String sender) {
		boolean result = false;
		String str;
		try {
			int i = 0;
			while ((str = flatFileReader.readLine()) != null) {
				if (i % ALERT_EACH_X_ACCOUNT == 0) {
					Common.getInstance().getServerCaller().sendMessage(sender, i + " {{DARK_GREEN}}accounts imported.");
				}
				String[] info = str.split(" ");
				if (info.length >= 2) {
					String[] balance = info[1].split(":");
					try {
						Common.getInstance().getAccountManager().getAccount(info[0]).set(Double.parseDouble(balance[1]), Common.getInstance().getServerCaller().getDefaultWorld(), Common.getInstance().getCurrencyManager().getCurrency(CurrencyManager.defaultCurrencyID).getName());
					} catch (NumberFormatException e) {
						Common.getInstance().sendConsoleMessage(Level.SEVERE, "User " + info[0] + " have a invalid balance" + balance[1]);
					}

				}
				i++;
			}
			flatFileReader.close();
			result = true;
		} catch (IOException e) {
			Common.getInstance().getLogger().severe("A error occured while reading the iConomy database file! Message: " + e.getMessage());
		}
		return result;
	}
	
	private boolean importDatabase(String sender) {
		List<IConomyTable> icoList = db.select(IConomyTable.class).execute().find();
		if (icoList != null && icoList.size() > 0) {
			Iterator<IConomyTable> icoListIterator = icoList.iterator();
			int i = 0;
			while (icoListIterator.hasNext()) {
				if (i % ALERT_EACH_X_ACCOUNT == 0) {
					Common.getInstance().getServerCaller().sendMessage(sender, i + " of  " + icoList.size() + "{{DARK_GREEN}}accounts imported.");

				}
				IConomyTable entry = icoListIterator.next();
				Common.getInstance().getAccountManager().getAccount(entry.getUsername()).set(entry.getBalance(), Common.getInstance().getServerCaller().getDefaultWorld(), Common.getInstance().getCurrencyManager().getCurrency(CurrencyManager.defaultCurrencyID).getName());
				i++;
			}
			Common.getInstance().getServerCaller().sendMessage(sender, i + " of  " + icoList.size() + "{{DARK_GREEN}}accounts imported.");
		}
		try {
			db.close();
		} catch (ConnectionException e) {
			Common.getInstance().getLogger().severe("Unable to disconnect from the iConomy database! Message: " + e.getMessage());
		}
		return true;
	}

}
