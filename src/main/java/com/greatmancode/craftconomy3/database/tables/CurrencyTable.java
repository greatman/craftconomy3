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


public class CurrencyTable {

    public static final String TABLE_NAME="currency";

    public static final String CREATE_TABLE_MYSQL = "CREATE TABLE `"+ Common.getInstance().getDatabaseManager().getTablePrefix()+TABLE_NAME+"` (" +
            "  `id` int(11) NOT NULL AUTO_INCREMENT," +
            "  `name` varchar(50)," +
            "  `plural` varchar(50)," +
            "  `minor` varchar(50)," +
            "  `minorplural` text," +
            "  `hardCap` double DEFAULT NULL," +
            "  `sign` varchar(5)," +
            "  `status` BOOLEAN DEFAULT FALSE," +
            "  PRIMARY KEY (`id`)" +
            ") ENGINE=InnoDB;";

    public static final String SELECT_ENTRY = "SELECT * FROM "+ Common.getInstance().getDatabaseManager().getTablePrefix()+TABLE_NAME+" WHERE name=?";

    public static final String INSERT_ENTRY = "INSERT INTO "+ Common.getInstance().getDatabaseManager().getTablePrefix()+TABLE_NAME+"(name,plural,minor,minorplural,sign) VALUES (?,?,?,?,?)";

    public static final String SET_AS_DEFAULT = "UPDATE "+Common.getInstance().getDatabaseManager().getTablePrefix()+TABLE_NAME+" SET status=FALSE; UPDATE "+Common.getInstance().getDatabaseManager().getTablePrefix()+TABLE_NAME+" SET status=TRUE WHERE name=?";

    public static final String UPDATE_ENTRY = "UPDATE "+Common.getInstance().getDatabaseManager().getTablePrefix()+TABLE_NAME+" SET name=? plural=? minor=? minorplural=? sign=? WHERE name=?";

    public static final String DELETE_ENTRY = "DELETE FROM "+Common.getInstance().getDatabaseManager().getTablePrefix()+TABLE_NAME+" WHERE name=?";
}
