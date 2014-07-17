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
package com.greatmancode.craftconomy3.database.tables;

public class LogTable extends DatabaseTable {
    public static final String TABLE_NAME = "log";

    public final String CREATE_TABLE_MYSQL = "CREATE TABLE `" + getPrefix() + TABLE_NAME + "` (" +
            "  `id` int(11) NOT NULL AUTO_INCREMENT," +
            "  `username_id` int(11) DEFAULT NULL," +
            "  `type` varchar(30)," +
            "  `cause` varchar(50)," +
            "  `causeReason` varchar(50)," +
            "  `worldName` varchar(50)," +
            "  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP," +
            "  `amount` double DEFAULT NULL," +
            "  `currency_id` text," +
            "  PRIMARY KEY (`id`)," +
            "  ADD CONSTRAINT `fk_log_account` FOREIGN KEY (`username_id`) REFERENCES `" + getPrefix() + AccountTable.TABLE_NAME + "` (`id`) ON DELETE CASCADE;" +
            " ADD CONSTRAINT `fk_log_currency` FOREIGN KEY (`currency_id`) REFERENCES `" + getPrefix() + CurrencyTable.TABLE_NAME + "` (`name`) ON DELETE CASCADE;" +
            ") ENGINE=InnoDB;";

    public final String INSERT_ENTRY = "INSERT INTO " + getPrefix() + TABLE_NAME + "" +
            "(username_id, type, cause, causeReason, worldName, amount, currency_id, timestamp)" +
            "VALUES((SELECT id from " + getPrefix() + AccountTable.TABLE_NAME + " WHERE name=?),?,?,?,?,?,?,?)";

    public final String SELECT_ENTRY = "SELECT * FROM " + getPrefix() + TABLE_NAME + " " +
            "LEFT JOIN " + getPrefix() + AccountTable.TABLE_NAME + " " +
            "ON " + getPrefix() + TABLE_NAME + ".username_id = " + getPrefix() + AccountTable.TABLE_NAME + ".id " +
            "WHERE " + getPrefix() + AccountTable.TABLE_NAME + ".name=?";

    public final String SELECT_ENTRY_LIMIT = "SELECT * FROM " + getPrefix() + TABLE_NAME + " " +
            "LEFT JOIN " + getPrefix() + AccountTable.TABLE_NAME + " " +
            "ON " + getPrefix() + TABLE_NAME + ".username_id = " + getPrefix() + AccountTable.TABLE_NAME + ".id " +
            "WHERE " + getPrefix() + AccountTable.TABLE_NAME + ".name=? LIMIT ?,? ORDER BY " + TABLE_NAME + ".id";

    public final String CLEAN_ENTRY = "DELETE FROM " + getPrefix() + TABLE_NAME + " WHERE timestamp <= ?";

    public LogTable(String prefix) {
        super(prefix);
    }
}
