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
import com.greatmancode.craftconomy3.currency.Currency;
import com.greatmancode.craftconomy3.currency.CurrencyManager;
import com.greatmancode.craftconomy3.utils.Tools;

public class BankGiveCommand implements CraftconomyCommand {

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
						Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_RED}}That currency doesn't exist!");
						return;
					}
				}
				String worldName = "any";
				if (args.length > 3) {
					if (Common.getInstance().getConfigurationManager().isMultiWorld()) {
						if (!Common.getInstance().getServerCaller().worldExist(args[3])) {
							Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_RED}}This world doesn't exist!");
							return;
						}
						worldName = args[3];
					}
				} else {
					if (!Common.getInstance().getServerCaller().isOnline(sender)) {
						worldName = Common.getInstance().getServerCaller().getDefaultWorld();
					} else {
						worldName = Common.getInstance().getAccountManager().getAccount(sender).getWorldPlayerCurrentlyIn();
					}
				}
				bankAccount.deposit(amount, worldName, currency.getName());
				Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}Deposited {{WHITE}}" + Common.getInstance().format(worldName, currency, amount) + "{{DARK_GREEN}} from the {{WHITE}}" + args[0] + "{{DARK_GREEN}} bank Account.");

			} else {
				Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_RED}}Invalid amount!");
			}
		} else {
			Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_RED}}This account doesn't exist!");
		}
	}

	@Override
	public boolean permission(String sender) {
		return Common.getInstance().getServerCaller().checkPermission(sender, "craftconomy.bank.give");
	}

	@Override
	public String help() {
		return "/bank give <Account Name> <Amount> [Currency] [World] - Give money in a account.";
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

}
