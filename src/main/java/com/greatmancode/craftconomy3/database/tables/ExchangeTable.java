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

public class ExchangeTable extends DatabaseTable{

    public static final String TABLE_NAME = "exchange";

    public final String CREATE_TABLE_MYSQL = "CREATE TABLE `"+ getPrefix()+TABLE_NAME+"` (" +
            "  `from_currency_id` int(11) NOT NULL," +
            "  `to_currency_id` int(11) NOT NULL," +
            "  `amount` double DEFAULT 1.0," +
            "  PRIMARY KEY (`to_currency_id`, from_currency_id)" +
            ") ENGINE=InnoDB;";

    public final String SELECT_ENTRY = "SELECT * FROM "+getPrefix()+TABLE_NAME+" " +
            "LEFT JOIN "+getPrefix()+CurrencyTable.TABLE_NAME+" AS c1 ON " +
            getPrefix()+TABLE_NAME+".to_currency_id = c1.id " +
            "LEFT JOIN "+getPrefix()+CurrencyTable.TABLE_NAME+" AS c2 ON " +
            getPrefix()+TABLE_NAME+".from_currency_id = c2.id " +
            "WHERE c1.name=? AND c2.name=?";

    public final String INSERT_ENTRY = "INSERT INTO "+getPrefix()+TABLE_NAME+"(from_currency_id, to_currency_id, amount) " +
            "VALUES((SELECT id from "+getPrefix()+CurrencyTable.TABLE_NAME+" WHERE name=?), " +
            "(SELECT id from "+getPrefix()+CurrencyTable.TABLE_NAME+" WHERE name=?),?)";

    public final String UPDATE_ENTRY = "UPDATE "+getPrefix()+TABLE_NAME+" SET amount=? " +
            "LEFT JOIN "+getPrefix()+CurrencyTable.TABLE_NAME+" AS c1 ON " +
            getPrefix()+TABLE_NAME+".to_currency_id = c1.id " +
            "LEFT JOIN "+getPrefix()+CurrencyTable.TABLE_NAME+" AS c2 ON " +
            getPrefix()+TABLE_NAME+".from_currency_id = c2.id " +
            "WHERE c1.name=? AND c2.name=?";

    public ExchangeTable(String prefix) {
        super(prefix);
    }
}
