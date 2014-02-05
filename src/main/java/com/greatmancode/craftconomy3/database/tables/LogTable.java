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

import com.alta189.simplesave.Field;
import com.alta189.simplesave.Id;
import com.alta189.simplesave.Table;
import com.greatmancode.craftconomy3.Cause;
import com.greatmancode.craftconomy3.LogInfo;
import lombok.Data;

import java.sql.Timestamp;

@Table("log")
@Data
@SuppressWarnings("PMD.UnusedPrivateField")
public class LogTable {
    @Id
    private int id;
    @Field
    private int username_id;
    @Field
    private double amount;
    @Field
    private LogInfo type;
    @Field
    private Cause cause;
    @Field
    private Timestamp timestamp;
    @Field
    private String causeReason;
    @Field
    private String currencyName;
    @Field
    private String worldName;
}
