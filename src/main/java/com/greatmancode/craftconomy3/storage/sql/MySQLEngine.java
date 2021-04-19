/**
 * This file is part of Craftconomy3.
 * <p>
 * Copyright (c) 2011-2016, Greatman <http://github.com/greatman/>
 * Copyright (c) 2017, Aztorius <http://github.com/Aztorius/>
 * <p>
 * Craftconomy3 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * Craftconomy3 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public License
 * along with Craftconomy3.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.greatmancode.craftconomy3.storage.sql;

import au.com.addstar.dripreporter.DripReporterApi;
import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.storage.sql.tables.*;
import com.greatmancode.tools.utils.DripReporterLoader;
import com.greatmancode.tools.utils.Tools;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class MySQLEngine extends SQLStorageEngine {

    public MySQLEngine() {
        HikariConfig config = new HikariConfig();
        config.setMaximumPoolSize(Common.getInstance().getMainConfig().getInt("System.Database.Poolsize", 20));
        config.setDataSourceClassName("com.mysql.cj.jdbc.MysqlDataSource");
        config.addDataSourceProperty("serverName", Common.getInstance().getMainConfig().getString("System.Database.Address", "localhost"));
        config.addDataSourceProperty("port", Common.getInstance().getMainConfig().getString("System.Database.Port", "3306"));
        config.addDataSourceProperty("databaseName", Common.getInstance().getMainConfig().getString("System.Database.Db", "Craftconomy"));
        config.addDataSourceProperty("user", Common.getInstance().getMainConfig().getString("System.Database.Username", "root"));
        config.addDataSourceProperty("password", Common.getInstance().getMainConfig().getString("System.Database.Password", ""));
        config.addDataSourceProperty("autoDeserialize", true);
        if (DripReporterLoader.isEnabled()) {
            if (DripReporterLoader.getApi() != null) {
                try {
                    DripReporterApi api = DripReporterLoader.getApi();
                    config.setHealthCheckRegistry(api.getHealthRegistry());
                    config.setMetricRegistry(api.getRegistry());
                } catch (Throwable err){
                    Common.getInstance().getLogger().info("Unable to hook dripReporter.");

                    Common.getInstance().getLogger().info("CraftCononmy-DripHook: " + err.getMessage());
                }
            }
        }
        String useSSL = Common.getInstance().getMainConfig().getString("System.Database.useSSL", "false");
        if (useSSL == null) useSSL = "false";
        config.addDataSourceProperty("useSSL", Boolean.valueOf(useSSL));
        config.setConnectionTimeout(5000);
        db = new HikariDataSource(config);
        this.tablePrefix = Common.getInstance().getMainConfig().getString("System.Database.Prefix", "cc3_");
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
            statement = connection.prepareStatement(accountTable.createTableMySQL);
            statement.executeUpdate();
            statement.close();

            statement = connection.prepareStatement(currencyTable.createTableMySQL);
            statement.executeUpdate();
            statement.close();

            statement = connection.prepareStatement(configTable.createTableMySQL);
            statement.executeUpdate();
            statement.close();

            statement = connection.prepareStatement(worldGroupTable.createTableMySQL);
            statement.executeUpdate();
            statement.close();

            statement = connection.prepareStatement(balanceTable.createTableMySQL);
            statement.executeUpdate();
            statement.close();

            statement = connection.prepareStatement(logTable.createTableMySQL);
            statement.executeUpdate();
            statement.close();

            statement = connection.prepareStatement(accessTable.createTableMySQL);
            statement.executeUpdate();
            statement.close();

            statement = connection.prepareStatement(exchangeTable.createTableMySQL);
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
