/*
 * This file is part of Craftconomy3.
 *
 * Copyright (c) 2011-2013, Greatman <http://github.com/greatman/>
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

import com.greatmancode.craftconomy3.Cause;
import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.account.Account;
import com.greatmancode.tools.commands.interfaces.CommandExecutor;

public class BankCreateCommand extends CommandExecutor {
	@Override
	public void execute(String sender, String[] args) {
		if (!Common.getInstance().getAccountManager().exist(Account.BANK_PREFIX + args[0])) {
			if (Common.getInstance().getAccountManager().getAccount(sender).hasEnough(Common.getInstance().getBankPrice(), Common.getInstance().getServerCaller().getPlayerCaller().getPlayerWorld(sender), Common.getInstance().getCurrencyManager().getDefaultCurrency().getName())) {
				Common.getInstance().getAccountManager().getAccount(sender).withdraw(Common.getInstance().getBankPrice(), Common.getInstance().getServerCaller().getPlayerCaller().getPlayerWorld(sender), Common.getInstance().getCurrencyManager().getDefaultCurrency().getName(), Cause.BANK_CREATION, null);
				Account account = Common.getInstance().getAccountManager().getAccount(Account.BANK_PREFIX + args[0]);
				account.getAccountACL().set(sender, true, true, true, true, true);
				Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, Common.getInstance().getLanguageManager().getString("bank_account_created"));
			} else {
				Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, Common.getInstance().getLanguageManager().parse("bank_account_not_enough_money_create", Common.getInstance().format(null, Common.getInstance().getCurrencyManager().getCurrency(Common.getInstance().getBankCurrencyId()), Common.getInstance().getBankPrice())));
			}
		} else {
			Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, Common.getInstance().getLanguageManager().getString("account_already_exists"));
		}
	}

	@Override
	public String help() {
		return Common.getInstance().getLanguageManager().getString("bank_create_cmd_help");
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

	@Override
	public String getPermissionNode() {
		return "craftconomy.bank.create";
	}
}
