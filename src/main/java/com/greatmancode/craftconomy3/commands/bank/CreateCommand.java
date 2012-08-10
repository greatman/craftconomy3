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

public class CreateCommand implements CraftconomyCommand {

	@Override
	public void execute(String sender, String[] args) {
		if (!Common.getInstance().getAccountHandler().exist(Account.BANK_PREFIX + args[0])) {
			if (Common.getInstance().getAccountHandler().getAccount(sender).hasEnough(Common.getInstance().getConfigurationManager().getConfig().getDouble("System.Bank.Price"), Common.getInstance().getServerCaller().getPlayerWorld(sender), Common.getInstance().getCurrencyManager().getCurrency(Common.getInstance().getConfigurationManager().getConfig().getString("System.Bank.Currency")).getName())) {
				Account account = Common.getInstance().getAccountHandler().getAccount(Account.BANK_PREFIX + args[0]);
				account.getAccountACL().set(sender,true,true,true,true);
				Common.getInstance().getServerCaller().sendMessage(sender, "The account has been created!");
			} else {
				Common.getInstance().getServerCaller().sendMessage(sender, "{{RED}}You don't have enough money to create a bank account! You need {{WHITE}}" + Common.getInstance().format(null, Common.getInstance().getCurrencyManager().getCurrency(Common.getInstance().getConfigurationManager().getConfig().getString("System.Bank.Currency")), Common.getInstance().getConfigurationManager().getConfig().getDouble("System.Bank.Price")));
			}
			
		} else {
			Common.getInstance().getServerCaller().sendMessage(sender, "{{RED}}This account already exists!");
		}
	}

	@Override
	public boolean permission(String sender) {
		return Common.getInstance().getServerCaller().checkPermission(sender, "craftconomy.bank.create");
	}

	@Override
	public String help() {
		return null;
	}

	@Override
	public int maxArgs() {
		return 1;
	}

	@Override
	public int minArgs() {
		return 1;
	}

	@Override
	public boolean playerOnly() {
		return true;
	}

}
