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

public class AccessTable extends DatabaseTable {

    public static final String TABLE_NAME = "acl";

    public final String createTableMySQL = "CREATE TABLE IF NOT EXISTS `" + getPrefix() + TABLE_NAME + "` (" +
            "  `account_id` int(11) DEFAULT NULL," +
            "  `playerName` varchar(16)," +
            "  `owner` BOOLEAN DEFAULT NULL," +
            "  `balance` BOOLEAN DEFAULT FALSE," +
            "  `deposit` BOOLEAN DEFAULT FALSE," +
            "  `acl` BOOLEAN DEFAULT FALSE," +
            "  `withdraw` BOOLEAN DEFAULT FALSE," +
            "  PRIMARY KEY (`account_id`, `playerName`)," +
            "  CONSTRAINT `" + getPrefix() + "fk_acl_account` FOREIGN KEY (`account_id`) REFERENCES `" + getPrefix() + AccountTable.TABLE_NAME + "` (`id`) ON UPDATE CASCADE ON DELETE CASCADE" +
            ") ENGINE=InnoDB CHARSET=utf8;";

    public final String createTableH2 = "CREATE TABLE IF NOT EXISTS `" + getPrefix() + TABLE_NAME + "` (" +
            "  `account_id` int(11)," +
            "  `playerName` varchar(16)," +
            "  `owner` BOOLEAN DEFAULT NULL," +
            "  `balance` BOOLEAN DEFAULT FALSE," +
            "  `deposit` BOOLEAN DEFAULT FALSE," +
            "  `acl` BOOLEAN DEFAULT FALSE," +
            "  `withdraw` BOOLEAN DEFAULT FALSE," +
            " PRIMARY KEY(account_id, playerName)," +
            " FOREIGN KEY (`account_id`) REFERENCES `" + getPrefix() + AccountTable.TABLE_NAME + "` (`id`) ON UPDATE CASCADE ON DELETE CASCADE" +
            ");";

    public final String selectEntry = "SELECT * FROM " + getPrefix() + TABLE_NAME + " " +
            "LEFT JOIN " + getPrefix() + AccountTable.TABLE_NAME + " ON " +
            getPrefix() + TABLE_NAME + ".account_id = " + getPrefix() + AccountTable.TABLE_NAME + ".id WHERE " + getPrefix() + AccountTable.TABLE_NAME + ".name=? AND " + getPrefix() + AccountTable.TABLE_NAME + ".bank=?";

    public final String selectEntryUnique = "SELECT * FROM " + getPrefix() + TABLE_NAME + " " +
            "LEFT JOIN " + getPrefix() + AccountTable.TABLE_NAME + " ON " +
            getPrefix() + TABLE_NAME + ".account_id = " + getPrefix() + AccountTable.TABLE_NAME + ".id WHERE " + getPrefix() + AccountTable.TABLE_NAME + ".name=? AND " + getPrefix() + AccountTable.TABLE_NAME + ".bank=? AND playerName=?";

    public final String insertEntry = "INSERT INTO " + getPrefix() + TABLE_NAME + "" +
            "(account_id, playerName, owner, balance, deposit, acl, withdraw) VALUES((SELECT id from " + getPrefix() + AccountTable.TABLE_NAME + " WHERE name=? AND bank=?),?,?,?,?,?,?)";

    public final String updateEntry = "UPDATE " + getPrefix() + TABLE_NAME + " SET owner=? , balance=?, deposit=?, acl=?, withdraw=? " +
            "WHERE account_id=(SELECT id FROM " + getPrefix() + AccountTable.TABLE_NAME + " WHERE name=? AND bank=?) AND playerName=?";

    public final String getAccountList = "SELECT " + getPrefix() + AccountTable.TABLE_NAME+".name FROM " + getPrefix() + TABLE_NAME + " " +
            "LEFT JOIN " + getPrefix() + AccountTable.TABLE_NAME + " ON " +
            getPrefix() + TABLE_NAME + ".account_id = " + getPrefix() + AccountTable.TABLE_NAME + ".id " +
            "WHERE playerName=?";

    public AccessTable(String prefix) {
        super(prefix);
    }
}
