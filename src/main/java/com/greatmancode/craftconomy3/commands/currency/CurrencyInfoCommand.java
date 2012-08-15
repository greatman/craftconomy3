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
package com.greatmancode.craftconomy3.commands.currency;

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.commands.CraftconomyCommand;
import com.greatmancode.craftconomy3.currency.Currency;

public class CurrencyInfoCommand implements CraftconomyCommand {

	@Override
	public void execute(String sender, String[] args) {
		Currency currency = Common.getInstance().getCurrencyManager().getCurrency(args[0]);
		if (currency != null) {
			Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}======== {{WHITE}}" + currency.getName() + " {{DARK_GREEN}}========");
			Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}Name: {{WHITE}}" + currency.getName());
			Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}Name Plural: {{WHITE}}" + currency.getPlural());
			Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}Minor: {{WHITE}}" + currency.getMinor());
			Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}Minor plural: {{WHITE}}" + currency.getMinorPlural());
		} else {
			Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_RED}}Currency not found!");
		}

	}

	@Override
	public boolean permission(String sender) {
		return Common.getInstance().getServerCaller().checkPermission(sender, "craftconomy.currency.info");
	}

	@Override
	public String help() {
		return "/currency info <Name> - Display the information about a currency.";
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
		return false;
	}

}
