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

public class ExchangeTable {

    public static final String TABLE_NAME = "exchange";

    public static final String CREATE_TABLE_MYSQL = "CREATE TABLE `"+ Common.getInstance().getDatabaseManager().getTablePrefix()+TABLE_NAME+"` (" +
            "  `amount` double DEFAULT 1.0," +
            "  `to_currency_id` int(11)," +
            "  `from_currency_id` int(11)," +
            "  PRIMARY KEY (`to_currency_id`, from_currency_id)" +
            ") ENGINE=InnoDB;";

    public static final String SELECT_ENTRY = "SELECT * FROM "+Common.getInstance().getDatabaseManager().getTablePrefix()+TABLE_NAME+" " +
            "LEFT JOIN "+Common.getInstance().getDatabaseManager().getTablePrefix()+CurrencyTable.TABLE_NAME+" AS c1 ON " +
            Common.getInstance().getDatabaseManager().getTablePrefix()+TABLE_NAME+".to_currency_id = c1.id " +
            "LEFT JOIN "+Common.getInstance().getDatabaseManager().getTablePrefix()+CurrencyTable.TABLE_NAME+" AS c2 ON " +
            Common.getInstance().getDatabaseManager().getTablePrefix()+TABLE_NAME+".from_currency_id = c2.id " +
            "WHERE c1.name=? AND c2.name=?";

    public static final String INSERT_ENTRY = "INSERT INTO "+Common.getInstance().getDatabaseManager().getTablePrefix()+TABLE_NAME+"(to_currency_id,from_currency_id, amount) " +
            "VALUES((SELECT id from "+Common.getInstance().getDatabaseManager().getTablePrefix()+CurrencyTable.TABLE_NAME+" WHERE name=?), " +
            "(SELECT id from "+Common.getInstance().getDatabaseManager().getTablePrefix()+CurrencyTable.TABLE_NAME+" WHERE name=?),?)";

    public static final String UPDATE_ENTRY = "UPDATE "+Common.getInstance().getDatabaseManager().getTablePrefix()+TABLE_NAME+" SET amount=? " +
            "LEFT JOIN "+Common.getInstance().getDatabaseManager().getTablePrefix()+CurrencyTable.TABLE_NAME+" AS c1 ON " +
            Common.getInstance().getDatabaseManager().getTablePrefix()+TABLE_NAME+".to_currency_id = c1.id " +
            "LEFT JOIN "+Common.getInstance().getDatabaseManager().getTablePrefix()+CurrencyTable.TABLE_NAME+" AS c2 ON " +
            Common.getInstance().getDatabaseManager().getTablePrefix()+TABLE_NAME+".from_currency_id = c2.id " +
            "WHERE c1.name=? AND c2.name=?";
}
