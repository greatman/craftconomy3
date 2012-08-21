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
import com.greatmancode.craftconomy3.commands.CraftconomyCommand;
import com.greatmancode.craftconomy3.utils.Tools;

public class PayDayModifyCommand implements CraftconomyCommand {

	@Override
	public void execute(String sender, String[] args) {
		if (Common.getInstance().getPaydayManager().getPayDay(args[0]) != null) {
			if (args[1].equalsIgnoreCase("name")) {
				if (Common.getInstance().getPaydayManager().getPayDay(args[2]) == null) {
					Common.getInstance().getPaydayManager().getPayDay(args[0]).setName(args[2]);
					Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}Name changed!");
				} else {
					Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_RED}}There's already a payday with this name!");
				}
			} else if (args[1].equalsIgnoreCase("status")) {
				if (args[2].equalsIgnoreCase("wage") || args[2].equalsIgnoreCase("tax")) {
					if (args[2].equalsIgnoreCase("wage")) {
						Common.getInstance().getPaydayManager().getPayDay(args[0]).setStatus(0);
					} else {
						Common.getInstance().getPaydayManager().getPayDay(args[0]).setStatus(1);
					}
					Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}Status changed!");
				} else {
					Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_RED}}Invalid status! Valid values are: wage/tax");
				}
			} else if (args[1].equalsIgnoreCase("disabled")) {
				if (args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("false")) {
					Common.getInstance().getPaydayManager().getPayDay(args[0]).setDisabled(Boolean.parseBoolean(args[2]));
					Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}Disabled changed!");
				} else {
					Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_RED}}Invalid disabled mode! Valid values are: true/false");
				}
			} else if (args[1].equalsIgnoreCase("interval")) {
				if (Tools.isInteger(args[2])) {
					Common.getInstance().getPaydayManager().getPayDay(args[0]).setInterval(Integer.parseInt(args[2]));
					Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}Interval changed!");
				} else {
					Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_RED}}Invalid interval! I need a number of seconds!");
				}
			} else if (args[1].equalsIgnoreCase("amount")) {
				if (Tools.isValidDouble(args[2])) {
					Common.getInstance().getPaydayManager().getPayDay(args[0]).setValue(Double.parseDouble(args[2]));
					Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}Amount changed!");
				} else {
					Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_RED}}Invalid amount!");
				}
				
			} else if (args[1].equalsIgnoreCase("account")) {
				if (Common.getInstance().getAccountManager().exist(args[2])) {
					Common.getInstance().getPaydayManager().getPayDay(args[0]).setAccount(args[2]);
					Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}Account changed!");
				} else {
					Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_RED}}This account doesn't exist!");
				}
			} else if (args[1].equalsIgnoreCase("currency")) {
				if (Common.getInstance().getCurrencyManager().getCurrency(args[2]) != null) {
					Common.getInstance().getPaydayManager().getPayDay(args[0]).setCurrencyId(Common.getInstance().getCurrencyManager().getCurrency(args[2]).getDatabaseID());
					Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}Currency changed!");
				} else {
					Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_RED}}Currency doesn't exist!");
				}
			} else if (args[1].equalsIgnoreCase("world")) {
				if (Common.getInstance().getServerCaller().worldExist(args[2])) {
					Common.getInstance().getPaydayManager().getPayDay(args[0]).setWorldName(args[2]);
					Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}World changed!");
				} else {
					Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_RED}}World doesn't exist!");
				}
			} else {
				Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_RED}}Invalid Edit mode.");
			}
		} else {
			Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_RED}}This payday doesn't exist!");
		}
	}

	@Override
	public boolean permission(String sender) {
		return Common.getInstance().getServerCaller().checkPermission(sender, "craftconomy.payday.command.modify");
	}

	@Override
	public String help() {
		return "/payday modify <Name> <Name/status/disabled/interval/amount/account/currency/World> <Value> - Modify a payday setting.";
	}

	@Override
	public int maxArgs() {
		return 3;
	}

	@Override
	public int minArgs() {
		return 3;
	}

	@Override
	public boolean playerOnly() {
		return false;
	}

}
