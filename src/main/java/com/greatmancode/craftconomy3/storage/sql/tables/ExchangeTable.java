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

public class ExchangeTable extends DatabaseTable {

    public static final String TABLE_NAME = "exchange";

    public final String createTableMySQL = "CREATE TABLE IF NOT EXISTS `" + getPrefix() + TABLE_NAME + "` (" +
            "  `from_currency` VARCHAR(50) NOT NULL," +
            "  `to_currency` VARCHAR(50) NOT NULL," +
            "  `amount` double DEFAULT 1.0," +
            "  PRIMARY KEY (`from_currency`, to_currency)," +
            "  CONSTRAINT `"+getPrefix()+"fk_exchange_currencyfrom`" +
            "    FOREIGN KEY (from_currency)" +
            "    REFERENCES " + getPrefix() + CurrencyTable.TABLE_NAME + " (name) ON UPDATE CASCADE ON DELETE CASCADE," +
            "  CONSTRAINT `"+getPrefix()+"fk_exchange_currencyto`" +
            "    FOREIGN KEY (to_currency)" +
            "    REFERENCES " + getPrefix() + CurrencyTable.TABLE_NAME + " (name) ON UPDATE CASCADE ON DELETE CASCADE" +
            ") ENGINE=InnoDB CHARSET=utf8;";

    public final String createTableH2 = "CREATE TABLE IF NOT EXISTS `" + getPrefix() + TABLE_NAME + "` (" +
            "  `from_currency` VARCHAR(50) NOT NULL," +
            "  `to_currency` VARCHAR(50) NOT NULL," +
            "  `amount` double DEFAULT 1.0," +
            "  PRIMARY KEY (`from_currency`, to_currency)," +
            "    FOREIGN KEY (from_currency)" +
            "    REFERENCES " + getPrefix() + CurrencyTable.TABLE_NAME + "(name) ON UPDATE CASCADE ON DELETE CASCADE," +
            "    FOREIGN KEY (to_currency)" +
            "    REFERENCES " + getPrefix() + CurrencyTable.TABLE_NAME + "(name) ON UPDATE CASCADE ON DELETE CASCADE" +
            ");";

    public final String selectEntry = "SELECT * FROM " + getPrefix() + TABLE_NAME + " " +
            "WHERE from_currency=? AND to_currency=?";

    public final String selectAll = "SELECT * FROM "+getPrefix() + TABLE_NAME;

    public final String insertEntry = "INSERT INTO " + getPrefix() + TABLE_NAME + "(from_currency, to_currency, amount) " +
            "VALUES(?,?,?)";

    public final String updateEntry = "UPDATE " + getPrefix() + TABLE_NAME + " SET amount=? " +
            "WHERE from_currency.name=? AND to_currency.name=?";

    public ExchangeTable(String prefix) {
        super(prefix);
    }
}
