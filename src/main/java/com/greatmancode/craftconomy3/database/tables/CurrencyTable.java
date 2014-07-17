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

public class CurrencyTable extends DatabaseTable {

    public static final String TABLE_NAME = "currency";

    public final String CREATE_TABLE_MYSQL = "CREATE TABLE `" + getPrefix() + TABLE_NAME + "` (" +
            "  `name` varchar(50)," +
            "  `plural` varchar(50)," +
            "  `minor` varchar(50)," +
            "  `minorplural` text," +
            "  `sign` varchar(5)," +
            "  `status` BOOLEAN DEFAULT FALSE," +
            "  `bankCurrency` BOOLEAN DEFAULT FALSE," +
            "  PRIMARY KEY (`name`)" +
            ") ENGINE=InnoDB;";

    public final String SELECT_ALL_ENTRY = "SELECT * FROM " + getPrefix() + TABLE_NAME;
    public final String SELECT_ENTRY = "SELECT * FROM " + getPrefix() + TABLE_NAME + " WHERE name=?";

    public final String INSERT_ENTRY = "INSERT INTO " + getPrefix() + TABLE_NAME + "(name,plural,minor,minorplural,sign,status,bankCurrency) " +
            "VALUES (?,?,?,?,?,?,?)";

    public final String SET_AS_DEFAULT = "UPDATE " + getPrefix() + TABLE_NAME + " SET status=FALSE; UPDATE " + getPrefix() + TABLE_NAME + " SET status=TRUE WHERE name=?";

    public final String SET_AS_DEFAULT_BANK = "UPDATE " + getPrefix() + TABLE_NAME + " SET bankCurrency=FALSE; UPDATE " + getPrefix() + TABLE_NAME + " SET bankCurrency=TRUE WHERE name=?";

    public final String UPDATE_ENTRY = "UPDATE " + getPrefix() + TABLE_NAME + " SET name=? plural=? minor=? minorplural=? sign=? status=? bankCurrency=? WHERE name=?";

    public final String DELETE_ENTRY = "DELETE FROM " + getPrefix() + TABLE_NAME + " WHERE name=?";

    public CurrencyTable(String prefix) {
        super(prefix);
    }
}
