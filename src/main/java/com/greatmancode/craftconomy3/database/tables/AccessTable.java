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

public class AccessTable {
    public static final String TABLE_NAME = "acl";
    public static final String CREATE_TABLE_MYSQL = "CREATE TABLE `"+ Common.getInstance().getDatabaseManager().getTablePrefix()+TABLE_NAME+"` (" +
            "  `account_id` int(11) DEFAULT NULL," +
            "  `playerName` varchar(16)," +
            "  `owner` BOOLEAN DEFAULT NULL," +
            "  `balance` BOOLEAN DEFAULT FALSE," +
            "  `deposit` BOOLEAN DEFAULT FALSE," +
            "  `acl` BOOLEAN DEFAULT FALSE," +
            "  `withdraw` BOOLEAN DEFAULT FALSE," +
            "  PRIMARY KEY (`account_id`)," +
            "  ADD CONSTRAINT `fk_acl_account` FOREIGN KEY (`account_id`) REFERENCES `"+Common.getInstance().getDatabaseManager().getTablePrefix()+AccountTable.TABLE_NAME+"` (`id`);" +
            ") ENGINE=InnoDB;";

    public static final String SELECT_ENTRY = "SELECT * FROM "+Common.getInstance().getDatabaseManager().getTablePrefix()+TABLE_NAME+" "+
            "LEFT JOIN "+Common.getInstance().getDatabaseManager().getTablePrefix()+AccountTable.TABLE_NAME+" ON " +
            Common.getInstance().getDatabaseManager().getTablePrefix()+TABLE_NAME+".account_id = "+Common.getInstance().getDatabaseManager().getTablePrefix()+AccountTable.TABLE_NAME+".id WHERE "+Common.getInstance().getDatabaseManager().getTablePrefix()+AccountTable.TABLE_NAME+".name=?";

    public static final String INSERT_ENTRY = "INSERT INTO "+Common.getInstance().getDatabaseManager().getTablePrefix()+TABLE_NAME+"" +
            "(account_id, playerName, owner, balance, deposit, acl, withdraw) VALUES((SELECT id from "+Common.getInstance().getDatabaseManager().getTablePrefix()+AccountTable.TABLE_NAME+" WHERE name=?),?,?,?,?,?,?)";

    public static final String UPDATE_ENTRY = "UPDATE "+Common.getInstance().getDatabaseManager().getTablePrefix()+TABLE_NAME+" SET owner=? , balance=?, deposit=?, acl=?, withdraw=? " +
            "LEFT JOIN "+Common.getInstance().getDatabaseManager().getTablePrefix()+AccountTable.TABLE_NAME+" ON " +
            Common.getInstance().getDatabaseManager().getTablePrefix()+TABLE_NAME+".account_id = "+Common.getInstance().getDatabaseManager().getTablePrefix()+AccountTable.TABLE_NAME+".id " +
            "WHERE "+Common.getInstance().getDatabaseManager().getTablePrefix()+AccountTable.TABLE_NAME+".name=? AND playerName=?";
}
