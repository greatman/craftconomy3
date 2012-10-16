package com.greatmancode.craftconomy3.commands;

import java.util.HashMap;
import java.util.Map;

import com.greatmancode.craftconomy3.Common;

public class CommandHandler {

	private boolean setupEnabled = false;
	private Map<String, CraftconomyCommand> commandList = new HashMap<String, CraftconomyCommand>();
	
	public CommandHandler(String commandName, String help, boolean setupEnabled) {
		Common.getInstance().getServerCaller().addCommand(commandName, help, Common.getInstance().getCommandManager().getCommandManager());
	}
	public void registerCommand(String commandName, CraftconomyCommand command) {
		//Common.getInstance().getServerCaller().addCommand(commandName, help, Common.getInstance().getCommandManager().getCommandManager());
		commandList.put(commandName, command);
		Common.getInstance().getServerCaller().registerPermission(command.getPermissionNode());
	}
	
	public void execute(String username, String commandName, String[] args) {
		if (Common.getInstance().getConfigurationManager().getConfig().getBoolean("System.Setup") && !setupEnabled) {
			return;
		}
		if (commandList.containsKey(commandName)) {
			CraftconomyCommand command = commandList.get(commandName);
			if (command.permission(username)) {
				if (args.length >= command.minArgs() && args.length <= command.maxArgs()) {
					command.execute(username, args);
				}
			}
		}
	}
	
	
	public Map<String,CraftconomyCommand> getCommandList() {
		return commandList;
	}
}
