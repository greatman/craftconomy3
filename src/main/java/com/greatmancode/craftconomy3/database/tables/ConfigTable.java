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

import com.greatmancode.craftconomy3.Common;

public class ConfigTable extends DatabaseTable{

    public static final String TABLE_NAME = "config";
    public static final String NAME_FIELD = "name";
    public static final String VALUE_FIELD = "value";
    public final String CREATE_TABLE_MYSQL = "CREATE TABLE `"+ getPrefix()+TABLE_NAME+"` (" +
            "  `"+NAME_FIELD+"` varchar(30) NOT NULL," +
            "  `"+VALUE_FIELD+"` varchar(255) NOT NULL," +
            "  PRIMARY KEY (`name`)" +
            ") ENGINE=InnoDB;";

    public final String SELECT_ENTRY = "SELECT * FROM "+getPrefix()+TABLE_NAME+" WHERE name=?";

    public final String INSERT_ENTRY = "INSERT INTO "+getPrefix()+TABLE_NAME+"(name,value) VALUES(?,?)";

    public final String UPDATE_ENTRY = "UPDATE "+getPrefix()+TABLE_NAME+" SET value=? WHERE name=?";

    public ConfigTable(String prefix) {
        super(prefix);
    }
}
