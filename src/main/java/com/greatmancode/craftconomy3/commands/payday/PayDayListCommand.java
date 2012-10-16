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

import java.util.Iterator;
import java.util.Map.Entry;

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.commands.CraftconomyCommand;
import com.greatmancode.craftconomy3.payday.PayDay;

public class PayDayListCommand extends CraftconomyCommand {

	@Override
	public void execute(String sender, String[] args) {
		Iterator<Entry<Integer, PayDay>> paydayList = Common.getInstance().getPaydayManager().getPayDayList().entrySet().iterator();
		Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}} ========= {{WHITE}}Payday list {{DARK_GREEN}}=========");
		while(paydayList.hasNext()) {
			Entry<Integer,PayDay> entry = paydayList.next();
			Common.getInstance().getServerCaller().sendMessage(sender, entry.getValue().getName());
		}
	}

	@Override
	public boolean permission(String sender) {
		return Common.getInstance().getServerCaller().checkPermission(sender, "craftconomy.payday.command.list");
	}

	@Override
	public String help() {
		return "/payday list - List all payday";
	}

	@Override
	public int maxArgs() {
		return 0;
	}

	@Override
	public int minArgs() {
		return 0;
	}

	@Override
	public boolean playerOnly() {
		return false;
	}

	@Override
	public String getPermissionNode() {
		return "craftconomy.payday.command.list";
	}

}
