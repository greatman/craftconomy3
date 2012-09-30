/*
 * This file is part of Craftconomy3.
 *
 * Copyright (c) 2011-2012, Greatman <http://github.com/greatman/>
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

@Table("cc3_balance")
public class BalanceTable {

	public static final String USERNAME_ID_FIELD = "username_id";
	public static final String CURRENCY_ID_FIELD = "currency_id";
	public static final String WORLD_NAME_FIELD = "worldName";
	@Id
	private int id;

	@Field
	private int username_id;

	@Field
	private int currency_id;

	@Field
	private String worldName;

	@Field
	private double balance;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUsernameId() {
		return username_id;
	}

	public void setUsernameId(int usernameId) {
		this.username_id = usernameId;
	}

	public int getCurrencyId() {
		return currency_id;
	}

	public void setCurrencyId(int currencyId) {
		this.currency_id = currencyId;
	}

	public String getWorldName() {
		return worldName;
	}

	public void setWorldName(String worldName) {
		this.worldName = worldName;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}
}
