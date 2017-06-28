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

public class BalanceTable extends DatabaseTable {

    //TODO Fix tables with currency NAME as primary key
    public static final String BALANCE_FIELD = "balance";
    public static final String WORLD_NAME_FIELD = "worldName";
    public static final String CURRENCY_FIELD = "currency_id";
    public static final String TABLE_NAME = "balance";

    public final String createTableMySQL = "CREATE TABLE IF NOT EXISTS " + getPrefix() + TABLE_NAME + " (" +
            "  `" + BALANCE_FIELD + "` double DEFAULT NULL," +
            "  `" + WORLD_NAME_FIELD + "` varchar(255)," +
            "  `username_id` int(11)," +
            "  `" + CURRENCY_FIELD + "` varchar(50)," +
            "  PRIMARY KEY (" + WORLD_NAME_FIELD + ", username_id, currency_id)," +
            "  CONSTRAINT `"+getPrefix()+"fk_balance_account`" +
            "    FOREIGN KEY (username_id)" +
            "    REFERENCES " + getPrefix() + AccountTable.TABLE_NAME + "(id) ON UPDATE CASCADE ON DELETE CASCADE," +
            "  CONSTRAINT `"+getPrefix()+"fk_balance_currency`" +
            "    FOREIGN KEY (" + CURRENCY_FIELD + ")" +
            "    REFERENCES " + getPrefix() + CurrencyTable.TABLE_NAME +"(name) ON UPDATE CASCADE ON DELETE CASCADE" +
            ") ENGINE=InnoDB CHARSET=utf8;";

    public final String createTableH2 = "CREATE TABLE IF NOT EXISTS " + getPrefix() + TABLE_NAME + " (" +
            "  `" + BALANCE_FIELD + "` double DEFAULT NULL," +
            "  `" + WORLD_NAME_FIELD + "` varchar(255)," +
            "  `username_id` int(11)," +
            "  `" + CURRENCY_FIELD + "` varchar(50)," +
            "  PRIMARY KEY (" + WORLD_NAME_FIELD + ", username_id, currency_id)," +
            "    FOREIGN KEY (username_id)" +
            "    REFERENCES " + getPrefix() + AccountTable.TABLE_NAME + "(id) ON UPDATE CASCADE ON DELETE CASCADE," +
            "    FOREIGN KEY (" + CURRENCY_FIELD + ")" +
            "    REFERENCES " + getPrefix() + CurrencyTable.TABLE_NAME + "(name) ON UPDATE CASCADE ON DELETE CASCADE" +
            ")";

    public final String selectAllEntryAccount = "SELECT * FROM " + getPrefix() + TABLE_NAME + " " +
            "LEFT JOIN " + getPrefix() + AccountTable.TABLE_NAME + " " +
            "ON " + getPrefix() + TABLE_NAME + ".username_id = " + getPrefix() + AccountTable.TABLE_NAME + ".id " +
            "WHERE " + getPrefix() + AccountTable.TABLE_NAME + ".name=?";

    public final String selectWorldEntryAccount = "SELECT * FROM " + getPrefix() + TABLE_NAME + " " +
            "LEFT JOIN " + getPrefix() + AccountTable.TABLE_NAME + " " +
            "ON " + getPrefix() + TABLE_NAME + ".username_id = " + getPrefix() + AccountTable.TABLE_NAME + ".id " +
            "WHERE " + getPrefix() + AccountTable.TABLE_NAME + ".name=? AND " + WORLD_NAME_FIELD + "=?";

    public final String selectWorldCurrencyEntryAccount = "SELECT balance, worldName, currency_id, username_id FROM " + getPrefix() + TABLE_NAME + " " +
            "LEFT JOIN " + getPrefix() + AccountTable.TABLE_NAME + " " +
            "ON " + getPrefix() + TABLE_NAME + ".username_id = " + getPrefix() + AccountTable.TABLE_NAME + ".id " +
            "LEFT JOIN " + getPrefix() + CurrencyTable.TABLE_NAME + " " +
            "ON " + getPrefix() + TABLE_NAME + ".currency_id = " + getPrefix() + CurrencyTable.TABLE_NAME + ".name " +
            "WHERE " + getPrefix() + AccountTable.TABLE_NAME + ".name=? AND " + WORLD_NAME_FIELD + "=? AND " + getPrefix() + CurrencyTable.TABLE_NAME + ".name=?";

    public final String insertEntry = "INSERT INTO " + getPrefix() + TABLE_NAME + "" +
            "(" + BALANCE_FIELD + ", " + WORLD_NAME_FIELD + ", username_id, currency_id) " +
            "VALUES(?, ?, (SELECT id from " + getPrefix() + AccountTable.TABLE_NAME + " WHERE "+getPrefix()+AccountTable.TABLE_NAME+".name=? AND bank=?),?)";

    public final String updateEntry = "UPDATE "+getPrefix()+TABLE_NAME+" SET balance=? " +
            "WHERE username_id=? " +
            "AND "+CURRENCY_FIELD+"=? AND "+WORLD_NAME_FIELD+"=?";

    public final String listTopAccount = "SELECT balance, " + getPrefix() + CurrencyTable.TABLE_NAME + ".name AS currencyName, " + getPrefix() + AccountTable.TABLE_NAME + ".name FROM " + getPrefix() + TABLE_NAME + " " +
            "LEFT JOIN " + getPrefix() + AccountTable.TABLE_NAME + " " +
            "ON " + getPrefix() + TABLE_NAME + ".username_id = " + getPrefix() + AccountTable.TABLE_NAME + ".id " +
            "LEFT JOIN " + getPrefix() + CurrencyTable.TABLE_NAME + " " +
            "ON " + getPrefix() + TABLE_NAME + ".currency_id = " + getPrefix() + CurrencyTable.TABLE_NAME + ".name " +
            "WHERE " + WORLD_NAME_FIELD + "=? AND " + getPrefix() + CurrencyTable.TABLE_NAME + ".name=? ORDER BY balance DESC LIMIT ?,?";

    public BalanceTable(String prefix) {
        super(prefix);
    }
}
