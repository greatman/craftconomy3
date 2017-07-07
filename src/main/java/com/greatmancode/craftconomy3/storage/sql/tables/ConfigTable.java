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

public class ConfigTable extends DatabaseTable {

    public static final String TABLE_NAME = "config";
    public static final String NAME_FIELD = "name";
    public static final String VALUE_FIELD = "value";

    public final String createTableMySQL = "CREATE TABLE IF NOT EXISTS `" + getPrefix() + TABLE_NAME + "` (" +
            "  `" + NAME_FIELD + "` varchar(30) NOT NULL," +
            "  `" + VALUE_FIELD + "` varchar(255) NOT NULL," +
            "  PRIMARY KEY (`"+NAME_FIELD+"`)" +
            ") ENGINE=InnoDB CHARSET=utf8;";

    public final String createTableH2 = "CREATE TABLE IF NOT EXISTS `" + getPrefix() + TABLE_NAME + "` (" +
            "  `" + NAME_FIELD + "` varchar(30) NOT NULL," +
            "  `" + VALUE_FIELD + "` varchar(255) NOT NULL," +
            "  PRIMARY KEY (`"+NAME_FIELD+"`)" +
            ");";

    public final String selectEntry = "SELECT * FROM " + getPrefix() + TABLE_NAME + " WHERE "+NAME_FIELD+"=?";

    public final String insertEntry = "INSERT INTO " + getPrefix() + TABLE_NAME + "("+NAME_FIELD+","+VALUE_FIELD+") VALUES(?,?)";

    public final String updateEntry = "UPDATE " + getPrefix() + TABLE_NAME + " SET "+VALUE_FIELD+"=? WHERE "+NAME_FIELD+"=?";

    public ConfigTable(String prefix) {
        super(prefix);
    }
}
