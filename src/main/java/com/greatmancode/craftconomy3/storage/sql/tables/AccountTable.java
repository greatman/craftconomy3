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
package com.greatmancode.craftconomy3.storage.sql.tables;

public class AccountTable extends DatabaseTable {


    public static final String TABLE_NAME = "account";
    public final String CREATE_TABLE_MYSQL = "CREATE TABLE IF NOT EXISTS `" + getPrefix() + TABLE_NAME + "` (" +
            "  `id` int(11) NOT NULL AUTO_INCREMENT," +
            "  `name` varchar(50)," +
            "  `infiniteMoney` boolean DEFAULT FALSE," +
            "  `uuid` varchar(36) UNIQUE," +
            "  `ignoreACL` boolean DEFAULT FALSE," +
            "  `bank` boolean DEFAULT FALSE," +
            "  PRIMARY KEY (id)," +
            "  KEY `account_name_index` (`name`(50))," +
            "  KEY `account_uuid_index` (`uuid`(36))" +
            ") ENGINE=InnoDB;";

    public final String CREATE_TABLE_H2 = "CREATE TABLE IF NOT EXISTS " + getPrefix() + TABLE_NAME + " (" +
            "id int PRIMARY KEY AUTO_INCREMENT," +
            "name varchar(50)," +
            "infiniteMoney boolean DEFAULT FALSE," +
            "uuid varchar(36) NULL," +
            "ignoreACL boolean DEFAULT FALSE," +
            "bank boolean DEFAULT FALSE);" +
            "CREATE INDEX IF NOT EXISTS account_name ON " + getPrefix() + TABLE_NAME + "(name);" +
            "CREATE INDEX IF NOT EXISTS account_uuid ON " + getPrefix() + TABLE_NAME + "(uuid);";

    public final String SELECT_ENTRY_NAME = "SELECT * FROM " + getPrefix() + TABLE_NAME + " WHERE name=? AND bank=?";

    public final String SELECT_ENTRY_UUID = "SELECT * FROM " + getPrefix() + TABLE_NAME + " WHERE uuid=?";

    public final String INSERT_ENTRY = "INSERT INTO " + getPrefix() + TABLE_NAME + "(name,uuid) VALUES(?,?)";

    public final String INSERT_ENTRY_BANK = "INSERT INTO " + getPrefix() + TABLE_NAME + "(name,bank) VALUES(?,true)";

    public final String INSERT_ENTRY_ALL_INFO = "INSERT INTO " + getPrefix() + TABLE_NAME + "(name,uuid,infiniteMoney,ignoreACL,bank) VALUES(?,?,?,?,?)";

    public final String UPDATE_INFINITEMONEY_ENTRY = "UPDATE " + getPrefix() + TABLE_NAME + " SET infiniteMoney=? WHERE name=? AND bank=?";

    public final String UPDATE_IGNOREACL_ENTRY = "UPDATE " + getPrefix() + TABLE_NAME + " SET infiniteMoney=? WHERE name=? AND bank=?";

    public final String DELETE_ENTRY = "DELETE FROM " + getPrefix() + TABLE_NAME + " WHERE name=? AND bank=?";

    public final String UPDATE_NAME_BY_UUID = "UPDATE "+getPrefix()+TABLE_NAME + " SET name=? WHERE uuid=?";

    public final String UPDATE_UUID_BY_NAME = "UPDATE "+getPrefix()+TABLE_NAME+" SET uuid=? WHERE name=?";



    public AccountTable(String prefix) {
        super(prefix);
    }
}
