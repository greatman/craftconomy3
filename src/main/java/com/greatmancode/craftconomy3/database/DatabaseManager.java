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

public class DatabaseManager {

	private Database db = null;
	public DatabaseManager() throws Exception{
		String databasetype = Common.getInstance().getConfigurationManager().getConfig().getString("System.Database.Type");
		if (databasetype.equals("sqlite"))
		{
			SQLiteConfiguration config = new SQLiteConfiguration(Common.getInstance().getConfigurationManager().getDataFolder() + File.separator + "database.db");
			db = DatabaseFactory.createNewDatabase(config);
		}
		else if (databasetype.equals("mysql"))
		{
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
	
	public Database getDatabase() {
		return db;
	}
}
