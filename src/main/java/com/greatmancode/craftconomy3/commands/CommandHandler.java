package com.greatmancode.craftconomy3.commands;

import java.util.HashMap;
import java.util.Map;

import com.greatmancode.craftconomy3.Common;

public class CommandHandler {

	private boolean setupEnabled = false;
	private Map<String, CraftconomyCommand> commandList = new HashMap<String, CraftconomyCommand>();
	
	public CommandHandler(String commandName, String help, boolean setupEnabled) {
		Common.getInstance().getServerCaller().addCommand(commandName, help, Common.getInstance().getCommandManager().getCommandManager());
		this.setupEnabled = setupEnabled;
	}
	
	public void registerCommand(String commandName, CraftconomyCommand command) {
		//Common.getInstance().getServerCaller().addCommand(commandName, help, Common.getInstance().getCommandManager().getCommandManager());
		commandList.put(commandName, command);
		Common.getInstance().getServerCaller().registerPermission(command.getPermissionNode());
	}
	
	public void execute(String sender, String commandName, String[] args) {
		if (Common.getInstance().getConfigurationManager().getConfig().getBoolean("System.Setup") && !setupEnabled) {
			Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_RED}}This command is disabled while Craftconomy is under setup mode! Type /ccsetup to configure the plugin.");
			return;
		}
		if (commandList.containsKey(commandName)) {
			CraftconomyCommand command = commandList.get(commandName);
			if (command.permission(sender)) {
				if (args.length >= command.minArgs() && args.length <= command.maxArgs()) {
					command.execute(sender, args);
				} else {
					Common.getInstance().getServerCaller().sendMessage(sender, "Usage: " + command.help());
				}
			} else {
				Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_RED}}You don't have permissions!");
			}
		} else {
			Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_RED}}This subcommand doesn't exist!");
		}
	}
	
	
	public Map<String,CraftconomyCommand> getCommandList() {
		return commandList;
	}
}
