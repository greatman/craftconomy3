package com.greatmancode.craftconomy3.commands;

import java.util.HashMap;

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.commands.money.AllCommand;
import com.greatmancode.craftconomy3.commands.money.MainCommand;

public class CommandManager {

	private HashMap<String, CraftconomyCommand> moneyCmdList = new HashMap<String, CraftconomyCommand>();
	
	private CommandLoader cmdLoader;
	public CommandManager() {
		moneyCmdList.put("", new MainCommand());
		moneyCmdList.put("all", new AllCommand());
		if (Common.isBukkit()) {
			cmdLoader = new BukkitCommandManager();
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
