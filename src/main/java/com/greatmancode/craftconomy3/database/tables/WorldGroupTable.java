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

public class WorldGroupTable extends DatabaseTable {

    public static final String TABLE_NAME = "worldgroup";

    public final String CREATE_TABLE_MYSQL = "CREATE TABLE `" + getPrefix() + TABLE_NAME + "` (" +
            "  `groupName` varchar(255)," +
            "  `worldList` varchar(255)," +
            "  PRIMARY KEY (`groupName`)" +
            ") ENGINE=InnoDB;";

    public final String CREATE_TABLE_H2 = "CREATE TABLE `" + getPrefix() + TABLE_NAME + "` (" +
            "  `groupName` varchar(255)," +
            "  `worldList` varchar(255)," +
            "  PRIMARY KEY (`groupName`)" +
            ");";

    public final String SELECT_ALL_ENTRY = "SELECT * FROM "+getPrefix()+ TABLE_NAME;

    public final String SELECT_ENTRY = "SELECT * FROM " + getPrefix() + TABLE_NAME + " WHERE groupName=?";

    public final String INSERT_ENTRY = "INSERT INTO " + getPrefix() + TABLE_NAME + "(groupName) VALUES(?)";

    public final String INSERT_ENTRY_WITH_WORLDLIST = "INSERT INTO " + getPrefix() + TABLE_NAME + "(groupName, worldList) VALUES(?,?)";

    public final String UPDATE_ENTRY = "UPDATE " + getPrefix() + TABLE_NAME + " SET worldList=? WHERE groupName=?";

    public final String DELETE_ENTRY = "DELETE FROM "+getPrefix()+TABLE_NAME+" WHERE groupName=?";

    public WorldGroupTable(String prefix) {
        super(prefix);
    }
}
