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

public class AccountTable {


    public static final String TABLE_NAME = "account";
    public static final String CREATE_TABLE_MYSQL = "CREATE TABLE `"+ Common.getInstance().getDatabaseManager().getTablePrefix()+TABLE_NAME+"` (" +
            "  `id` int(11) NOT NULL AUTO_INCREMENT," +
            "  `name` text," +
            "  `infiniteMoney` boolean DEFAULT FALSE," +
            "  `uuid` varchar(36) UNIQUE," +
            "  `ignoreACL` boolean DEFAULT FALSE," +
            "  `bank` boolean DEFAULT FALSE," +
            "  PRIMARY KEY (id)," +
            "  KEY `account_name_index` (`name`(50))," +
            "  KEY `account_uuid_index` (`uuid`(50))" +
            ") ENGINE=InnoDB;";

    public static final String SELECT_ENTRY_NAME = "SELECT * FROM "+Common.getInstance().getDatabaseManager().getTablePrefix()+TABLE_NAME+" WHERE name=? AND bank=?";

    public static final String SELECT_ENTRY_UUID = "SELECT * FROM "+Common.getInstance().getDatabaseManager().getTablePrefix()+TABLE_NAME+" WHERE uuid=?";

    public static final String INSERT_ENTRY = "INSERT INTO "+Common.getInstance().getDatabaseManager().getTablePrefix()+TABLE_NAME+"(name,uuid) VALUES(?,?)";

    public static final String INSERT_ENTRY_BANK = "INSERT INTO "+Common.getInstance().getDatabaseManager().getTablePrefix()+TABLE_NAME+"(name,bank) VALUES(?,true)";

    public static final String INSERT_ENTRY_ALL_INFO = "INSERT INTO "+Common.getInstance().getDatabaseManager().getTablePrefix()+TABLE_NAME+"(name,uuid,infiniteMoney,ignoreACL,bank) VALUES(?,?,?,?,?)";

    public static final String UPDATE_ENTRY = "UPDATE "+Common.getInstance().getDatabaseManager().getTablePrefix()+TABLE_NAME+" SET name=?, infiniteMoney=?, uuid=?, ignoreACL=? WHERE id=?";

    public static final String DELETE_ENTRY = "DELETE FROM "+Common.getInstance().getDatabaseManager().getTablePrefix()+TABLE_NAME+" WHERE name=? AND bank=?";
}
