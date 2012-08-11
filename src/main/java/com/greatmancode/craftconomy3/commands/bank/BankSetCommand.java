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
package com.greatmancode.craftconomy3.commands.bank;

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.account.Account;
import com.greatmancode.craftconomy3.commands.CraftconomyCommand;

public class BankSetCommand implements CraftconomyCommand{

	@Override
	public void execute(String sender, String[] args) {
		if (Common.getInstance().getAccountManager().exist(Account.BANK_PREFIX + args[0])) {
			Account account = Common.getInstance().getAccountManager().getAccount(Account.BANK_PREFIX + args[0]);
			if (account.getAccountACL().canAcl(sender) || account.getAccountACL().isOwner(sender) || Common.getInstance().getServerCaller().checkPermission(sender, "craftconomy.bank.set.others")) {
				
				if (args[1].equalsIgnoreCase("deposit")) {
					account.getAccountACL().setDeposit(args[2], Boolean.parseBoolean(args[3]));
				} else if (args[1].equalsIgnoreCase("withdraw")) {
					account.getAccountACL().setWithdraw(args[2], Boolean.parseBoolean(args[3]));
				} else if (args[1].equalsIgnoreCase("acl")) {
					account.getAccountACL().setAcl(args[2], Boolean.parseBoolean(args[3]));
				} else if (args[1].equalsIgnoreCase("show")) {
					account.getAccountACL().setShow(args[2], Boolean.parseBoolean(args[3]));
				} else {
					Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_RED}}Invalid flag!");
					return;
				}
				Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}The flag {{WHITE}}" + args[1] + "{{DARK_GREEN}} for the player {{WHITE}}" + args[2] + "{{DARK_GREEN}} has been set to {{WHITE}}" + args[3]);
			} else {
				Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_RED}}You can't modify the ACL of this account!");
			}
		} else {
			Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_RED}}This account doesn't exist!");
		}
		
	}

	@Override
	public boolean permission(String sender) {
		// TODO Auto-generated method stub
		return Common.getInstance().getServerCaller().checkPermission(sender, "craftconomy.bank.set");
	}

	@Override
	public String help() {
		// TODO Auto-generated method stub
		return "/bank set <Account Name> <deposit/withdraw/acl/show> <Player Name> <true/false> - Modify the permission of a player";
	}

	@Override
	public int maxArgs() {
		return 4;
	}

	@Override
	public int minArgs() {
		return 4;
	}

	@Override
	public boolean playerOnly() {
		return false;
	}

}
