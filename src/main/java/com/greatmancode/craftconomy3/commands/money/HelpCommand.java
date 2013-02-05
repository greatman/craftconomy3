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
package com.greatmancode.craftconomy3.commands.money;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.commands.interfaces.CraftconomyCommand;

public class HelpCommand extends CraftconomyCommand {
	@Override
	public void execute(String sender, String[] args) {
		Common.getInstance().getServerCaller().sendMessage(sender, Common.getInstance().getLanguageManager().getString("money_help_title"));
		Map<String, CraftconomyCommand> cmdList = Common.getInstance().getCommandManager().getCommandHandler("money").getCommandList();
		Iterator<Entry<String, CraftconomyCommand>> iterator = cmdList.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, CraftconomyCommand> entry = iterator.next();
			if (entry.getValue().permission(sender)) {
				Common.getInstance().getServerCaller().sendMessage(sender, entry.getValue().help());
			}
		}
	}

	@Override
	public String help() {
		return Common.getInstance().getLanguageManager().getString("money_help_cmd_help");
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
		return null;
	}
}
