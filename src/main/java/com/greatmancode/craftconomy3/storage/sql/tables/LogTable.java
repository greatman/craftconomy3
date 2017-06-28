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
package com.greatmancode.craftconomy3.storage.sql.tables;

public class LogTable extends DatabaseTable {
    public static final String TABLE_NAME = "log";

    public final String createTableMySQL = "CREATE TABLE IF NOT EXISTS `" + getPrefix() + TABLE_NAME + "` (" +
            "  `id` int(11) NOT NULL AUTO_INCREMENT," +
            "  `username_id` int(11) DEFAULT NULL," +
            "  `type` varchar(30)," +
            "  `cause` varchar(50)," +
            "  `causeReason` varchar(50)," +
            "  `worldName` varchar(50)," +
            "  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP," +
            "  `amount` double DEFAULT NULL," +
            "  `currency_id` varchar(50)," +
            "  PRIMARY KEY (`id`)," +
            "  CONSTRAINT `"+getPrefix()+"fk_log_account` FOREIGN KEY (`username_id`) REFERENCES `" + getPrefix() + AccountTable.TABLE_NAME + "` (`id`) ON DELETE CASCADE," +
            "  CONSTRAINT `"+getPrefix()+"fk_log_currency` FOREIGN KEY (`currency_id`) REFERENCES `" + getPrefix() + CurrencyTable.TABLE_NAME + "` (`name`) ON DELETE CASCADE" +
            ") ENGINE=InnoDB CHARSET=utf8;";

    public final String createTableH2 = "CREATE TABLE IF NOT EXISTS `" + getPrefix() + TABLE_NAME + "` (" +
            "  `id` int(11) NOT NULL AUTO_INCREMENT," +
            "  `username_id` int(11) DEFAULT NULL," +
            "  `type` varchar(30)," +
            "  `cause` varchar(50)," +
            "  `causeReason` varchar(50)," +
            "  `worldName` varchar(50)," +
            "  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP," +
            "  `amount` double DEFAULT NULL," +
            "  `currency_id` varchar(50)," +
            "  PRIMARY KEY (`id`)," +
            "  FOREIGN KEY (`username_id`) REFERENCES `" + getPrefix() + AccountTable.TABLE_NAME + "` (`id`) ON DELETE CASCADE," +
            "  FOREIGN KEY (`currency_id`) REFERENCES `" + getPrefix() + CurrencyTable.TABLE_NAME + "` (`name`) ON DELETE CASCADE" +
            ");";

    public final String insertEntry = "INSERT INTO " + getPrefix() + TABLE_NAME + "" +
            "(username_id, type, cause, causeReason, worldName, amount, currency_id, timestamp)" +
            "VALUES((SELECT id from " + getPrefix() + AccountTable.TABLE_NAME + " WHERE name=? AND bank=?),?,?,?,?,?,?,?)";

    public final String selectEntry = "SELECT * FROM " + getPrefix() + TABLE_NAME + " " +
            "LEFT JOIN " + getPrefix() + AccountTable.TABLE_NAME + " " +
            "ON " + getPrefix() + TABLE_NAME + ".username_id = " + getPrefix() + AccountTable.TABLE_NAME + ".id " +
            "WHERE " + getPrefix() + AccountTable.TABLE_NAME + ".name=?";

    public final String selectEntryLimit = "SELECT * FROM " + getPrefix() + TABLE_NAME + " " +
            "LEFT JOIN " + getPrefix() + AccountTable.TABLE_NAME + " " +
            "ON " + getPrefix() + TABLE_NAME + ".username_id = " + getPrefix() + AccountTable.TABLE_NAME + ".id " +
            "WHERE " + getPrefix() + AccountTable.TABLE_NAME + ".name=? ORDER BY " + getPrefix() + TABLE_NAME + ".id DESC LIMIT ?,?";

    public final String cleanEntry = "DELETE FROM " + getPrefix() + TABLE_NAME + " WHERE timestamp <= ?";

    public LogTable(String prefix) {
        super(prefix);
    }
}
