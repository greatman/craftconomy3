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
import com.greatmancode.craftconomy3.payday.PayDay;

public class PayDayInfoCommand extends CraftconomyCommand {

	@Override
	public void execute(String sender, String[] args) {
		PayDay payday = Common.getInstance().getPaydayManager().getPayDay(args[0]);
		if (payday != null) {
			Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}} ======== {{WHITE}}" + payday.getName() + " information {{DARK_GREEN}}========");
			Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}Amount: {{WHITE}}" + Common.getInstance().format(payday.getWorldName(), Common.getInstance().getCurrencyManager().getCurrency(payday.getCurrencyId()), payday.getValue()));
			if (payday.getStatus() == 0) {
				Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}Type: {{WHITE}}Wage");
			} else {
				Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}Type: {{WHITE}}Tax");
				
			}
			Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}Account: {{WHITE}}" + payday.getAccount());
		} else {
			Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_RED}}Payday not found!");
		}

	}

	@Override
	public String help() {
		return "/payday info <Payday Name> - Show information about a payday.";
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

	@Override
	public String getPermissionNode() {
		return "craftconomy.payday.command.info";
	}

}
