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
import java.util.ArrayList;
import java.util.HashMap;
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
import com.greatmancode.craftconomy3.database.tables.iconomy.iConomyTable;

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
			File dbFile = new File(Common.getInstance().getServerCaller().getDataFolder(),dbConnectInfo.get("filename"));
			if (dbFile.exists()) {
				try {
					flatFileReader = new BufferedReader(new FileReader(dbFile));
					result = true;
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
			
		} else if (selectedDbType.equals("mysql")) {
			
			
			try {
				MySQLConfiguration config = new MySQLConfiguration();
				config.setHost(dbConnectInfo.get("address"));
				config.setUser(dbConnectInfo.get("username"));
				config.setPassword(dbConnectInfo.get("password"));
				config.setDatabase(dbConnectInfo.get("database"));
				config.setPort(Integer.parseInt(dbConnectInfo.get("port")));
				db = DatabaseFactory.createNewDatabase(config);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		} else if (selectedDbType.equals("sqlite")) {
			SQLiteConfiguration config = new SQLiteConfiguration(Common.getInstance().getServerCaller().getDataFolder() + File.separator + dbConnectInfo.get("filename"));
			db = DatabaseFactory.createNewDatabase(config);
		}
		
		if (db != null) {
			
			try {
				db.registerTable(iConomyTable.class);
				db.connect();
				result = true;
			} catch (TableRegistrationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ConnectionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return result;
	}
	@Override
	public boolean importData(String sender) {
		boolean result = false;
		if (flatFileReader != null) {
			result = true;
			String str;
			try {
				int i = 0;
				while ((str = flatFileReader.readLine()) != null) {
					if (i % 10 == 0) {
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
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} else if (db != null) {
			result = true;
			List<iConomyTable> icoList = db.select(iConomyTable.class).execute().find();
			if (icoList != null && icoList.size() > 0) {
				Iterator<iConomyTable> icoListIterator = icoList.iterator();
				int i = 0;
				while (icoListIterator.hasNext()) {
					if (i % 10 == 0) {
						Common.getInstance().getServerCaller().sendMessage(sender, i + " of  " + icoList.size() + "{{DARK_GREEN}}accounts imported.");
						
					}
					iConomyTable entry = icoListIterator.next();
					Common.getInstance().getAccountManager().getAccount(entry.username).set(entry.balance, Common.getInstance().getServerCaller().getDefaultWorld(), Common.getInstance().getCurrencyManager().getCurrency(CurrencyManager.defaultCurrencyID).getName());
					i++;
				}
			}
			try {
				db.close();
			} catch (ConnectionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}

}
