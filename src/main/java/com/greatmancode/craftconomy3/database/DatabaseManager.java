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
package com.greatmancode.craftconomy3.database;

import java.io.File;

import com.alta189.simplesave.Database;
import com.alta189.simplesave.DatabaseFactory;
import com.alta189.simplesave.exceptions.ConnectionException;
import com.alta189.simplesave.exceptions.TableRegistrationException;
import com.alta189.simplesave.h2.H2Configuration;
import com.alta189.simplesave.mysql.MySQLConfiguration;
import com.alta189.simplesave.sqlite.SQLiteConfiguration;
import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.SpoutCaller;
import com.greatmancode.craftconomy3.database.tables.AccessTable;
import com.greatmancode.craftconomy3.database.tables.AccountTable;
import com.greatmancode.craftconomy3.database.tables.BalanceTable;
import com.greatmancode.craftconomy3.database.tables.ConfigTable;
import com.greatmancode.craftconomy3.database.tables.CurrencyTable;
import com.greatmancode.craftconomy3.database.tables.ExchangeTable;
import com.greatmancode.craftconomy3.database.tables.LogTable;
import com.greatmancode.craftconomy3.database.tables.PayDayTable;
import com.greatmancode.craftconomy3.database.tables.WorldGroupTable;

/**
 * Handle the database link.
 * @author greatman
 */
public class DatabaseManager {
	private Database db = null;

	/**
	 * Initialize the database manager.
	 * @throws TableRegistrationException
	 * @throws ConnectionException
	 */
	public DatabaseManager() throws TableRegistrationException, ConnectionException {
		try {
			DatabaseType databaseType = DatabaseType.valueOf(Common.getInstance().getConfigurationManager().getConfig().getString("System.Database.Type").toUpperCase());
			switch (databaseType) {
				case SQLITE:
					loadSQLite();
					break;
				case MYSQL:
					loadMySQL();
					break;
				case H2:
					loadH2();
					break;
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			Common.getInstance().getServerCaller().disablePlugin();
			return;
		}

		db.registerTable(AccountTable.class);
		db.registerTable(AccessTable.class);
		db.registerTable(BalanceTable.class);
		db.registerTable(CurrencyTable.class);
		db.registerTable(ConfigTable.class);
		db.registerTable(PayDayTable.class);
		db.registerTable(ExchangeTable.class);
		db.registerTable(WorldGroupTable.class);
		db.registerTable(LogTable.class);
		db.connect();
	}

	/**
	 * Load the SQLite database.
	 */
	private void loadSQLite() {
		if (Common.getInstance().getServerCaller() instanceof SpoutCaller) {
			// TODO: Improve that
			Common.getInstance().getServerCaller().loadLibrary("lib" + File.separator + "sqlite.jar");
		}
		SQLiteConfiguration config = new SQLiteConfiguration(Common.getInstance().getServerCaller().getDataFolder() + File.separator + "database.db");
		db = DatabaseFactory.createNewDatabase(config);
		Common.getInstance().addMetricsGraph("Database Engine", "SQLite");
	}

	/**
	 * Load the MySQL database.
	 */
	private void loadMySQL() {
		if (Common.getInstance().getServerCaller() instanceof SpoutCaller) {
			Common.getInstance().getServerCaller().loadLibrary("lib" + File.separator + "mysql.jar");
		}
		MySQLConfiguration config = new MySQLConfiguration();
		config.setHost(Common.getInstance().getConfigurationManager().getConfig().getString("System.Database.Address"));
		config.setUser(Common.getInstance().getConfigurationManager().getConfig().getString("System.Database.Username"));
		config.setPassword(Common.getInstance().getConfigurationManager().getConfig().getString("System.Database.Password"));
		config.setDatabase(Common.getInstance().getConfigurationManager().getConfig().getString("System.Database.Db"));
		config.setPort(Common.getInstance().getConfigurationManager().getConfig().getInt("System.Database.Port"));
		db = DatabaseFactory.createNewDatabase(config);
		Common.getInstance().addMetricsGraph("Database Engine", "MySQL");
	}

	/**
	 * Load the H2 database.
	 */
	private void loadH2() {
		H2Configuration config = new H2Configuration();
		Common.getInstance().getServerCaller().loadLibrary("lib" + File.separator + "h2.jar");
		File file = new File(Common.getInstance().getServerCaller().getDataFolder(), "craftconomy");
		config.setDatabase(file.getAbsolutePath());
		db = DatabaseFactory.createNewDatabase(config);
		Common.getInstance().addMetricsGraph("Database Engine", "H2");
	}

	/**
	 * Retrieve the database
	 * @return The Database link.
	 */
	public Database getDatabase() {
		return db;
	}
}
