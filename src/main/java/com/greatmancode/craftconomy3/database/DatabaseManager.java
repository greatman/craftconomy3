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
package com.greatmancode.craftconomy3.database;

import java.io.File;

import com.alta189.simplesave.Database;
import com.alta189.simplesave.DatabaseFactory;
import com.alta189.simplesave.mysql.MySQLConfiguration;
import com.alta189.simplesave.sqlite.SQLiteConfiguration;

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.database.tables.AccessTable;
import com.greatmancode.craftconomy3.database.tables.AccountTable;
import com.greatmancode.craftconomy3.database.tables.BalanceTable;
import com.greatmancode.craftconomy3.database.tables.CurrencyTable;

/**
 * Handle the database link.
 * @author greatman
 *
 */
public class DatabaseManager {

	private Database db = null;

	public DatabaseManager() throws Exception {
		String databasetype = Common.getInstance().getConfigurationManager().getConfig().getString("System.Database.Type");
		if (databasetype.equals("sqlite")) {
			SQLiteConfiguration config = new SQLiteConfiguration(Common.getInstance().getServerCaller().getDataFolder() + File.separator + "database.db");
			db = DatabaseFactory.createNewDatabase(config);
		} else if (databasetype.equals("mysql")) {
			MySQLConfiguration config = new MySQLConfiguration();
			config.setHost(Common.getInstance().getConfigurationManager().getConfig().getString("System.Database.Address"));
			config.setUser(Common.getInstance().getConfigurationManager().getConfig().getString("System.Database.Username"));
			config.setPassword(Common.getInstance().getConfigurationManager().getConfig().getString("System.Database.Password"));
			config.setDatabase(Common.getInstance().getConfigurationManager().getConfig().getString("System.Database.Db"));
			config.setPort(Common.getInstance().getConfigurationManager().getConfig().getInt("System.Database.Port"));
			db = DatabaseFactory.createNewDatabase(config);
		}

		db.registerTable(AccountTable.class);
		db.registerTable(AccessTable.class);
		db.registerTable(BalanceTable.class);
		db.registerTable(CurrencyTable.class);

		db.connect();
	}

	/**
	 * Retrieve the database
	 * @return The Database link.
	 */
	public Database getDatabase() {
		return db;
	}
}
