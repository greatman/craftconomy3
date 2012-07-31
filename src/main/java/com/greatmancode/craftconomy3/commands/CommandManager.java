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
