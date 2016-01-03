/**
 * This file is part of Craftconomy3.
 *
 * Copyright (c) 2011-2016, Greatman <http://github.com/greatman/>
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
package com.greatmancode.craftconomy3.storage.sql;

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.storage.sql.tables.*;
import com.greatmancode.tools.utils.Tools;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class H2Engine extends SQLStorageEngine {

    public H2Engine() {
        HikariConfig config = new HikariConfig();
        config.setMaximumPoolSize(Common.getInstance().getMainConfig().getInt("System.Database.Poolsize"));
        config.setDataSourceClassName("org.h2.jdbcx.JdbcDataSource");
        config.addDataSourceProperty("user", "sa");
        config.addDataSourceProperty("url", "jdbc:h2:file:"+new File(Common.getInstance().getServerCaller().getDataFolder().getPath(), "database").getAbsolutePath()+";MV_STORE=FALSE");
        config.setConnectionTimeout(5000);
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
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = db.getConnection();
            statement = connection.prepareStatement(accountTable.createTableH2);
            statement.executeUpdate();
            statement.close();

            statement = connection.prepareStatement(currencyTable.createTableH2);
            statement.executeUpdate();
            statement.close();

            statement = connection.prepareStatement(configTable.createTableH2);
            statement.executeUpdate();
            statement.close();

            statement = connection.prepareStatement(worldGroupTable.createTableH2);
            statement.executeUpdate();
            statement.close();

            statement = connection.prepareStatement(balanceTable.createTableH2);
            statement.executeUpdate();
            statement.close();

            statement = connection.prepareStatement(accessTable.createTableH2);
            statement.executeUpdate();
            statement.close();

            statement = connection.prepareStatement(exchangeTable.createTableH2);
            statement.executeUpdate();
            statement.close();

            statement = connection.prepareStatement(logTable.createTableH2);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Tools.closeJDBCStatement(statement);
            Tools.closeJDBCConnection(connection);
        }
    }
}
