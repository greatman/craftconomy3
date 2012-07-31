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
package com.greatmancode.craftconomy3.commands;

import java.util.HashMap;

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.commands.money.*;

public class CommandManager {

	private HashMap<String, CraftconomyCommand> moneyCmdList = new HashMap<String, CraftconomyCommand>();
	
	private CommandLoader cmdLoader;
	public CommandManager() {
		moneyCmdList.put("", new MainCommand());
		moneyCmdList.put("all", new AllCommand());
		moneyCmdList.put("pay", new PayCommand());
		moneyCmdList.put("give", new GiveCommand());
		moneyCmdList.put("take", new TakeCommand());
		moneyCmdList.put("set", new SetCommand());
		moneyCmdList.put("delete", new DeleteCommand());
		if (Common.isBukkit()) {
		}
		else
		{
			cmdLoader = new SpoutCommandManager();
		}
	}
	
	public HashMap<String, CraftconomyCommand> getMoneyCmdList() {
		return moneyCmdList;
	}
}
