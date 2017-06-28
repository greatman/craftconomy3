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

public class WorldGroupTable extends DatabaseTable {

    public static final String TABLE_NAME = "worldgroup";

    public final String createTableMySQL = "CREATE TABLE IF NOT EXISTS `" + getPrefix() + TABLE_NAME + "` (" +
            "  `groupName` varchar(255)," +
            "  `worldList` varchar(255)," +
            "  PRIMARY KEY (`groupName`)" +
            ") ENGINE=InnoDB CHARSET=utf8;";

    public final String createTableH2 = "CREATE TABLE IF NOT EXISTS `" + getPrefix() + TABLE_NAME + "` (" +
            "  `groupName` varchar(255)," +
            "  `worldList` varchar(255)," +
            "  PRIMARY KEY (`groupName`)" +
            ");";

    public final String selectAllEntry = "SELECT * FROM "+getPrefix()+ TABLE_NAME;

    public final String selectEntry = "SELECT * FROM " + getPrefix() + TABLE_NAME + " WHERE groupName=?";

    public final String insertEntry = "INSERT INTO " + getPrefix() + TABLE_NAME + "(groupName) VALUES(?)";

    public final String insertEntryWithWorldlist = "INSERT INTO " + getPrefix() + TABLE_NAME + "(groupName, worldList) VALUES(?,?)";

    public final String updateEntry = "UPDATE " + getPrefix() + TABLE_NAME + " SET worldList=? WHERE groupName=?";

    public final String deleteEntry = "DELETE FROM "+getPrefix()+TABLE_NAME+" WHERE groupName=?";

    public WorldGroupTable(String prefix) {
        super(prefix);
    }
}
