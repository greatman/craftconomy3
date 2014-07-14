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

public class AccountTable extends DatabaseTable{


    public static final String TABLE_NAME = "account";
    public final String CREATE_TABLE_MYSQL = "CREATE TABLE `"+ getPrefix()+TABLE_NAME+"` (" +
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

    public final String SELECT_ENTRY_NAME = "SELECT * FROM "+getPrefix()+TABLE_NAME+" WHERE name=? AND bank=?";

    public final String SELECT_ENTRY_UUID = "SELECT * FROM "+getPrefix()+TABLE_NAME+" WHERE uuid=?";

    public final String INSERT_ENTRY = "INSERT INTO "+getPrefix()+TABLE_NAME+"(name,uuid) VALUES(?,?)";

    public final String INSERT_ENTRY_BANK = "INSERT INTO "+getPrefix()+TABLE_NAME+"(name,bank) VALUES(?,true)";

    public final String INSERT_ENTRY_ALL_INFO = "INSERT INTO "+getPrefix()+TABLE_NAME+"(name,uuid,infiniteMoney,ignoreACL,bank) VALUES(?,?,?,?,?)";

    public final String UPDATE_INFINITEMONEY_ENTRY = "UPDATE "+getPrefix()+TABLE_NAME+" SET infiniteMoney=?,WHERE name=? AND bank=?";

    public final String UPDATE_IGNOREACL_ENTRY = "UPDATE "+getPrefix()+TABLE_NAME+" SET infiniteMoney=?,WHERE name=? AND bank=?";

    public final String DELETE_ENTRY = "DELETE FROM "+getPrefix()+TABLE_NAME+" WHERE name=? AND bank=?";

    public AccountTable(String prefix) {
        super(prefix);
    }
}
