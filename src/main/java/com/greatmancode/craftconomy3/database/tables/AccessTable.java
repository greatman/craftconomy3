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

@Table("cc3_acl")
public class AccessTable {

	@Id
	private int id;
	@Field
	private int account_id;

	@Field
	private String playerName;

	@Field
	private boolean deposit;

	@Field
	private boolean withdraw;

	@Field
	private boolean acl;

	@Field
	private boolean balance;

	@Field
	private boolean owner;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getAccount_id() {
		return account_id;
	}

	public void setAccount_id(int account_id) {
		this.account_id = account_id;
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public boolean canDeposit() {
		return deposit;
	}

	public void setDeposit(boolean deposit) {
		this.deposit = deposit;
	}

	public boolean canWithdraw() {
		return withdraw;
	}

	public void setWithdraw(boolean withdraw) {
		this.withdraw = withdraw;
	}

	public boolean canAcl() {
		return acl;
	}

	public void setAcl(boolean acl) {
		this.acl = acl;
	}

	public boolean canBalance() {
		return balance;
	}

	public void setBalance(boolean balance) {
		this.balance = balance;
	}

	public boolean isOwner() {
		return owner;
	}

	public void setOwner(boolean owner) {
		this.owner = owner;
	}

}
