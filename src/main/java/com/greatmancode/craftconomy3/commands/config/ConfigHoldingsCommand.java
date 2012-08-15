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
package com.greatmancode.craftconomy3.commands.config;

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.commands.CraftconomyCommand;
import com.greatmancode.craftconomy3.utils.Tools;

public class ConfigHoldingsCommand implements CraftconomyCommand {

	@Override
	public void execute(String sender, String[] args) {
		if (Tools.isValidDouble(args[0])) {
			Common.getInstance().getConfigurationManager().setHoldings(Double.parseDouble(args[0]));
			Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}Default holdings modified!");
		} else {
			Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_RED}}Invalid amount!");
		}

	}

	@Override
	public boolean permission(String sender) {
		return Common.getInstance().getServerCaller().checkPermission(sender, "craftconomy.config.holdings");
	}

	@Override
	public String help() {
		return "/craftconomy holdings <Amount> - Set the default amount of money of a user account.";
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
