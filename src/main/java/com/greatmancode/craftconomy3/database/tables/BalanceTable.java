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

public class BalanceTable extends DatabaseTable{

//TODO Fix tables with currency NAME as primary key
    public static final String BALANCE_FIELD = "balance";
    public static final String WORLD_NAME_FIELD = "worldName";
    public static final String CURRENCY_FIELD = "currency_id";
    public static final String TABLE_NAME = "balance";

    public final String CREATE_TABLE_MYSQL = "CREATE TABLE "+ getPrefix()+TABLE_NAME+"` (" +
            "  `"+BALANCE_FIELD+"` double DEFAULT NULL," +
            "  `"+WORLD_NAME_FIELD+"` text," +
            "  `username_id` int(11)," +
            "  `"+CURRENCY_FIELD+"` varchar(50)," +
            "  PRIMARY KEY ("+WORLD_NAME_FIELD+", username_id, currency_id)," +
            "  CONSTRAINT `fk_balance_account`" +
            "    FOREIGN KEY (username_id)" +
            "    REFERENCES "+getPrefix()+AccountTable.TABLE_NAME+" (id)) ON UPDATE CASCADE ON DELETE CASCADE" +
            "  CONSTRAINT `fk_balance_currency`" +
            "    FOREIGN KEY ("+CURRENCY_FIELD+")" +
            "    REFERENCES "+getPrefix()+"currency (name)) ON UPDATE CASCADE ON DELETE CASCADE" +
            ") ENGINE=InnoDB;";

    public final String SELECT_ALL_ENTRY_ACCOUNT = "SELECT * FROM "+getPrefix()+TABLE_NAME+" " +
            "LEFT JOIN "+getPrefix()+AccountTable.TABLE_NAME+" " +
            "ON "+getPrefix()+TABLE_NAME+".username_id = "+getPrefix()+AccountTable.TABLE_NAME+".id " +
            "WHERE "+getPrefix()+AccountTable.TABLE_NAME+".name=?";

    public final String SELECT_WORLD_ENTRY_ACCOUNT = "SELECT * FROM "+getPrefix()+TABLE_NAME+" " +
            "LEFT JOIN "+getPrefix()+AccountTable.TABLE_NAME+" " +
            "ON "+getPrefix()+TABLE_NAME+".username_id = "+getPrefix()+AccountTable.TABLE_NAME+".id " +
            "WHERE "+getPrefix()+AccountTable.TABLE_NAME+".name=? AND "+WORLD_NAME_FIELD+"=?";

    public final String SELECT_WORLD_CURRENCY_ENTRY_ACCOUNT = "SELECT * FROM "+getPrefix()+TABLE_NAME+" " +
            "LEFT JOIN "+getPrefix()+AccountTable.TABLE_NAME+" " +
            "ON "+getPrefix()+TABLE_NAME+".username_id = "+getPrefix()+AccountTable.TABLE_NAME+".id " +
            "LEFT JOIN "+getPrefix()+AccountTable.TABLE_NAME+" " +
            "ON "+getPrefix()+TABLE_NAME+".currency_id = "+getPrefix()+CurrencyTable.TABLE_NAME+".id " +
            "WHERE "+getPrefix()+AccountTable.TABLE_NAME+".name=? AND "+WORLD_NAME_FIELD+"=? AND "+getPrefix()+CurrencyTable.TABLE_NAME+".name=?";

    public final String INSERT_ENTRY = "INSERT INTO "+getPrefix()+TABLE_NAME+"" +
            "("+BALANCE_FIELD+", "+WORLD_NAME_FIELD+", username_id, currency_id) " +
            "VALUES(?, ?, (SELECT id from "+getPrefix()+AccountTable.TABLE_NAME+" WHERE name=?), (SELECT id FROM "+getPrefix()+CurrencyTable.TABLE_NAME+" WHERE name=?))";

    public final String UPDATE_ENTRY = "UPDATE "+getPrefix()+TABLE_NAME+" SET balance=?" +
            "LEFT JOIN "+getPrefix()+AccountTable.TABLE_NAME+" " +
            "ON "+getPrefix()+TABLE_NAME+".username_id = "+getPrefix()+AccountTable.TABLE_NAME+".id " +
            "LEFT JOIN "+getPrefix()+CurrencyTable.TABLE_NAME+" " +
            "ON "+getPrefix()+TABLE_NAME+"."+CURRENCY_FIELD+" = "+getPrefix()+CurrencyTable.TABLE_NAME+".id "+
            "WHERE "+getPrefix()+AccountTable.TABLE_NAME+".name=?" +
            "AND "+getPrefix()+CurrencyTable.TABLE_NAME+".name=? AND "+WORLD_NAME_FIELD+"=?";

    public final String DEPOSIT_ENTRY = "UPDATE "+getPrefix()+TABLE_NAME+" SET balance=?" +
            "LEFT JOIN "+getPrefix()+AccountTable.TABLE_NAME+" " +
            "ON "+getPrefix()+TABLE_NAME+".username_id = "+getPrefix()+AccountTable.TABLE_NAME+".id " +
            "LEFT JOIN "+getPrefix()+CurrencyTable.TABLE_NAME+" " +
            "ON "+getPrefix()+TABLE_NAME+"."+CURRENCY_FIELD+" = "+getPrefix()+CurrencyTable.TABLE_NAME+".id "+
            "WHERE "+getPrefix()+AccountTable.TABLE_NAME+".name=?" +
            "AND "+getPrefix()+CurrencyTable.TABLE_NAME+".name=?";

    public final String LIST_TOP_ACCOUNT = "SELECT balance, "+getPrefix()+CurrencyTable.TABLE_NAME+".name AS currencyName, "+getPrefix()+AccountTable.TABLE_NAME+".username FROM "+getPrefix()+TABLE_NAME+" " +
            "LEFT JOIN "+getPrefix()+AccountTable.TABLE_NAME+" " +
            "ON "+getPrefix()+TABLE_NAME+".username_id = "+getPrefix()+AccountTable.TABLE_NAME+".id " +
            "LEFT JOIN "+getPrefix()+AccountTable.TABLE_NAME+" " +
            "ON "+getPrefix()+TABLE_NAME+".currency_id = "+getPrefix()+CurrencyTable.TABLE_NAME+".id " +
            "WHERE "+WORLD_NAME_FIELD+"=? AND "+CurrencyTable.TABLE_NAME+".name=? ORDER BY balance DESC LIMIT ?,?";
    public BalanceTable(String prefix) {
        super(prefix);
    }
}
