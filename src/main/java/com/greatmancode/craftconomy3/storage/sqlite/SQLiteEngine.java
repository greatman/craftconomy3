/*
 * This file is part of Craftconomy3.
 *
 * Copyright (c) 2011-2014, Greatman <http://github.com/greatman/>
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
package com.greatmancode.craftconomy3.storage.sqlite;

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.account.Account;
import com.greatmancode.craftconomy3.database.tables.*;
import com.greatmancode.craftconomy3.storage.mysql.MySQLEngine;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.File;

/**
 * Created by greatman on 2014-07-13.
 */
public class SQLiteEngine extends MySQLEngine {

    public SQLiteEngine() {
        HikariConfig config = new HikariConfig();
        config.setMaximumPoolSize(10);
        config.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        config.addDataSourceProperty("databaseName", new File(Common.getInstance().getServerCaller().getDataFolder(), "database.db").getAbsolutePath());
        db = new HikariDataSource(config);
        this.tablePrefix = Common.getInstance().getMainConfig().getString("System.Database.Prefix");
        accessTable = new AccessTable(tablePrefix);
        accountTable = new AccountTable(tablePrefix);
        balanceTable = new BalanceTable(tablePrefix);
        configTable = new ConfigTable(tablePrefix);
        currencyTable = new CurrencyTable(tablePrefix);
        exchangeTable = new ExchangeTable(tablePrefix);
        logTable = new LogTable(tablePrefix);
        worldGroupTable = new WorldGroupTable(tablePrefix);
    }
}
