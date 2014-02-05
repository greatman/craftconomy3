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
import lombok.Data;

@Table("payday")
@Data
@SuppressWarnings("PMD.UnusedPrivateField")
public class PayDayTable {
    @Id
    private int id;
    @Field
    private String name;
    @Field
    private boolean disabled;
    /**
     * In seconds
     */
    @Field
    private int time;
    @Field
    private String account;
    /**
     * 0 = wage
     * 1 = tax
     */
    @Field
    private int status;
    @Field
    private int currency_id;
    @Field
    private double value;
    @Field
    private String worldName;
}
