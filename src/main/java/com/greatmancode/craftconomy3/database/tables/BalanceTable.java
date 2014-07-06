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

public class BalanceTable {


    public static final String BALANCE_FIELD = "balance";
    public static final String WORLD_NAME_FIELD = "worldName";
    public static final String TABLE_NAME = "balance";

    public static final String CREATE_TABLE_MYSQL = "CREATE TABLE "+ Common.getInstance().getDatabaseManager().getTablePrefix()+TABLE_NAME+"` (" +
            "  `"+BALANCE_FIELD+"` double DEFAULT NULL," +
            "  `"+WORLD_NAME_FIELD+"` text," +
            "  `username_id` int(11) DEFAULT NULL," +
            "  `currency_id` int(11) DEFAULT NULL," +
            "  PRIMARY KEY ("+WORLD_NAME_FIELD+", username_id, currency_id)," +
            "  CONSTRAINT `fk_balance_account`" +
            "    FOREIGN KEY (username_id)" +
            "    REFERENCES "+Common.getInstance().getDatabaseManager().getTablePrefix()+AccountTable.TABLE_NAME+" (id))" +
            "  CONSTRAINT `fk_balance_currency`" +
            "    FOREIGN KEY (currency_id)" +
            "    REFERENCES "+Common.getInstance().getDatabaseManager().getTablePrefix()+"currency (id))" +
            ") ENGINE=InnoDB;";

    public static final String SELECT_ENTRY = "SELECT * FROM "+Common.getInstance().getDatabaseManager().getTablePrefix()+TABLE_NAME+" " +
            "LEFT JOIN "+Common.getInstance().getDatabaseManager().getTablePrefix()+AccountTable.TABLE_NAME+" " +
            "ON "+Common.getInstance().getDatabaseManager().getTablePrefix()+TABLE_NAME+".username_id = "+Common.getInstance().getDatabaseManager().getTablePrefix()+AccountTable.TABLE_NAME+".id " +
            "WHERE "+Common.getInstance().getDatabaseManager().getTablePrefix()+AccountTable.TABLE_NAME+".name=?";

    public static final String INSERT_ENTRY = "INSERT INTO "+Common.getInstance().getDatabaseManager().getTablePrefix()+TABLE_NAME+"("+BALANCE_FIELD+", "+WORLD_NAME_FIELD+", username_id, currency_id) " +
            "VALUES(?, ?, (SELECT id from "+Common.getInstance().getDatabaseManager().getTablePrefix()+AccountTable.TABLE_NAME+" WHERE name=?), (SELECT id FROM "+Common.getInstance().getDatabaseManager().getTablePrefix()+CurrencyTable.TABLE_NAME+" WHERE name=?))";

    public static final String UPDATE_ENTRY = "UPDATE "+Common.getInstance().getDatabaseManager().getTablePrefix()+TABLE_NAME+" SET balance=?" +
            "LEFT JOIN "+Common.getInstance().getDatabaseManager().getTablePrefix()+AccountTable.TABLE_NAME+" " +
            "ON "+Common.getInstance().getDatabaseManager().getTablePrefix()+TABLE_NAME+".username_id = "+Common.getInstance().getDatabaseManager().getTablePrefix()+AccountTable.TABLE_NAME+".id " +
            "LEFT JOIN "+Common.getInstance().getDatabaseManager().getTablePrefix()+CurrencyTable.TABLE_NAME+" " +
            "ON "+Common.getInstance().getDatabaseManager().getTablePrefix()+TABLE_NAME+".currency_id = "+Common.getInstance().getDatabaseManager().getTablePrefix()+CurrencyTable.TABLE_NAME+".id "+
            "WHERE "+Common.getInstance().getDatabaseManager().getTablePrefix()+AccountTable.TABLE_NAME+".name=?" +
            "AND "+Common.getInstance().getDatabaseManager().getTablePrefix()+CurrencyTable.TABLE_NAME+".name=?";
}
