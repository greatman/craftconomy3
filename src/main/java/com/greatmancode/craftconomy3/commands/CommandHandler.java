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
import java.util.Map;

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.commands.interfaces.CraftconomyCommand;

public class CommandHandler {
	private boolean setupEnabled = false;
	private Map<String, CraftconomyCommand> commandList = new HashMap<String, CraftconomyCommand>();

	public CommandHandler(String commandName, String help, boolean setupEnabled) {
		Common.getInstance().getServerCaller().addCommand(commandName, help, Common.getInstance().getCommandManager().getCommandManager());
		this.setupEnabled = setupEnabled;
	}

	/**
	 * Register a sub-command.
	 * @param commandName The sub-command name
	 * @param command The sub-command handler.
	 */
	public void registerCommand(String commandName, CraftconomyCommand command) {
		commandList.put(commandName, command);
		Common.getInstance().getServerCaller().registerPermission(command.getPermissionNode());
	}

	/**
	 * Execute a certain command
	 * @param sender The sender name ("console" for the console)
	 * @param commandName The sub-command name.
	 * @param args The arguments.
	 */
	public void execute(String sender, String commandName, String[] args) {
		if (Common.getInstance().getConfigurationManager().getConfig().getBoolean("System.Setup") && !setupEnabled) {
			Common.getInstance().getServerCaller().sendMessage(sender, Common.getInstance().getLanguageManager().getString("command_disabled_setup_mode"));
			return;
		}
		if (commandList.containsKey(commandName)) {
			CraftconomyCommand command = commandList.get(commandName);
			if (command.playerOnly() && sender.equalsIgnoreCase("console")) {
				Common.getInstance().getServerCaller().sendMessage(sender, Common.getInstance().getLanguageManager().getString("user_only_command"));
				return;
			}
			if (command.permission(sender) || Common.getInstance().getServerCaller().checkPermission(sender, "craftconomy.*")) {
				if (args.length >= command.minArgs() && args.length <= command.maxArgs()) {
					command.execute(sender, args);
				} else {
					Common.getInstance().getServerCaller().sendMessage(sender, String.format(Common.getInstance().getLanguageManager().getString("command_usage"), command.help()));
				}
			} else {
				Common.getInstance().getServerCaller().sendMessage(sender, Common.getInstance().getLanguageManager().getString("no_permission"));
			}
		} else {
			Common.getInstance().getServerCaller().sendMessage(sender, Common.getInstance().getLanguageManager().getString("subcommand_not_exist"));
		}
	}

	/**
	 * Retrieve the list of the sub-commands.
	 * @return The list of sub-commands.
	 */
	public Map<String, CraftconomyCommand> getCommandList() {
		return commandList;
	}
}
