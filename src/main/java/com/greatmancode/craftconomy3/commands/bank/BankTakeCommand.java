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

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.account.Account;
import com.greatmancode.craftconomy3.commands.interfaces.CraftconomyCommand;
import com.greatmancode.craftconomy3.currency.Currency;
import com.greatmancode.craftconomy3.currency.CurrencyManager;
import com.greatmancode.craftconomy3.groups.WorldGroupsManager;
import com.greatmancode.craftconomy3.utils.Tools;

public class BankTakeCommand extends CraftconomyCommand {
	@Override
	public void execute(String sender, String[] args) {
		if (Common.getInstance().getAccountManager().exist(Account.BANK_PREFIX + args[0])) {
			Account bankAccount = Common.getInstance().getAccountManager().getAccount(Account.BANK_PREFIX + args[0]);
			if (Tools.isValidDouble(args[1])) {
				double amount = Double.parseDouble(args[1]);
				Currency currency = Common.getInstance().getCurrencyManager().getCurrency(CurrencyManager.defaultCurrencyID);
				if (args.length > 2) {
					if (Common.getInstance().getCurrencyManager().getCurrency(args[2]) != null) {
						currency = Common.getInstance().getCurrencyManager().getCurrency(args[2]);
					} else {
						Common.getInstance().getServerCaller().sendMessage(sender, Common.getInstance().getLanguageManager().getString("currency_not_exist"));
						return;
					}
				}
				String worldName = WorldGroupsManager.DEFAULT_GROUP_NAME;
				if (args.length > 3) {
					if (!Common.getInstance().getWorldGroupManager().worldGroupExist(args[3])) {
						Common.getInstance().getServerCaller().sendMessage(sender, Common.getInstance().getLanguageManager().getString("world_not_exist"));
						return;
					}
					worldName = args[3];
				} else if (Common.getInstance().getServerCaller().isOnline(sender)) {
					worldName = Common.getInstance().getAccountManager().getAccount(sender).getWorldPlayerCurrentlyIn();
				}

				if (bankAccount.hasEnough(amount, worldName, currency.getName())) {
					bankAccount.withdraw(amount, worldName, currency.getName());
					Common.getInstance().getServerCaller().sendMessage(sender, String.format(Common.getInstance().getLanguageManager().getString("bank_take_success"), Common.getInstance().format(worldName, currency, amount), args[0]));
				} else {
					Common.getInstance().getServerCaller().sendMessage(sender, Common.getInstance().getLanguageManager().getString("bank_not_enough_money"));
				}
			} else {
				Common.getInstance().getServerCaller().sendMessage(sender, Common.getInstance().getLanguageManager().getString("invalid_amount"));
			}
		} else {
			Common.getInstance().getServerCaller().sendMessage(sender, Common.getInstance().getLanguageManager().getString("account_not_exist"));
		}
	}

	@Override
	public String help() {
		return Common.getInstance().getLanguageManager().getString("bank_take_cmd_help");
	}

	@Override
	public int maxArgs() {
		return 3;
	}

	@Override
	public int minArgs() {
		return 2;
	}

	@Override
	public boolean playerOnly() {
		return false;
	}

	@Override
	public String getPermissionNode() {
		return "craftconomy.bank.take";
	}
}
