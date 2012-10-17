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
package com.greatmancode.craftconomy3.commands.payday;

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.commands.interfaces.CraftconomyCommand;
import com.greatmancode.craftconomy3.currency.Currency;
import com.greatmancode.craftconomy3.currency.CurrencyManager;
import com.greatmancode.craftconomy3.utils.Tools;

public class PayDayCreateCommand extends CraftconomyCommand {

	@Override
	public void execute(String sender, String[] args) {
		if (Common.getInstance().getPaydayManager().getPayDay(args[0]) == null) {
			if (Tools.isInteger(args[1])) {
				if (args[2].equalsIgnoreCase("wage") || args[2].equalsIgnoreCase("tax")) {
					if (Tools.isValidDouble(args[3])) {
						String accountName = "", worldName = "any";
						int currencyId = Common.getInstance().getCurrencyManager().getCurrency(CurrencyManager.defaultCurrencyID).getDatabaseID();
						if (args.length >= 5) {
							if (Common.getInstance().getAccountManager().exist(args[4])) {
								accountName = args[4];
							} else {
								Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_RED}}Account not found!");
								return;
							}
						}
						if (args.length >= 6) {
							Currency currency = Common.getInstance().getCurrencyManager().getCurrency(args[5]);
							if (currency != null) {
								currencyId = currency.getDatabaseID();
							} else {
								Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_RED}}Currency not found!");
								return;
							}
						}
						if (args.length == 7 && Common.getInstance().getConfigurationManager().isMultiWorld()) {
							if (Common.getInstance().getServerCaller().worldExist(args[6])) {
								worldName = args[6];
							} else {
								Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_RED}}World not found!");
								return;
							}
						}
						int status = 0;
						if (args[2].equalsIgnoreCase("tax")) {
							status = 1;
						}
						Common.getInstance().getPaydayManager().addPayDay(args[0], false, Integer.parseInt(args[1]), accountName, status, currencyId, Double.parseDouble(args[3]), worldName, true);
						Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}Payday added! Add the permission node {{WHITE}}craftconomy.payday." + args[0].toLowerCase() + " {{DARK_GREEN}}to the players you want to add this payday!");
					} else {
						Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_RED}}Invalid Amount!");
					}
				} else {
					Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_RED}}Invalid mode. only wage or tax is supported!");
				}
			} else {
				Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_RED}}Invalid interval!");
			}
		} else {
			Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_RED}}There's already a payday named like that!");
		}
	}

	@Override
	public String help() {
		return "/payday create <Name> <Interval> <wage/tax> <Amount> [Account] [Currency Name] [World Name] - Create a new payday";
	}

	@Override
	public int maxArgs() {
		return 7;
	}

	@Override
	public int minArgs() {
		return 4;
	}

	@Override
	public boolean playerOnly() {
		return false;
	}

	@Override
	public String getPermissionNode() {
		return "craftconomy.payday.command.create";
	}

}
