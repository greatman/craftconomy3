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

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.greatmancode.craftconomy3.Caller;
import com.greatmancode.craftconomy3.Common;

/**
 * Handles the commands for the Bukkit server
 * 
 * @author greatman
 * 
 */
public class BukkitCommandManager implements CommandExecutor, CommandManager {

	public BukkitCommandManager() {
		Common.getInstance().getServerCaller().addCommand("money", "", this);
		Common.getInstance().getServerCaller().addCommand("bank", "", this);
		Common.getInstance().getServerCaller().addCommand("ccsetup", "", this);
		Common.getInstance().getServerCaller().addCommand("currency", "", this);
		Common.getInstance().getServerCaller().addCommand("craftconomy", "", this);
		Common.getInstance().getServerCaller().addCommand("payday", "", this);
	}

	@Override
	public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] args) {
		// TODO: Improve the setup alert
		CraftconomyCommand cmd = null;
		if (command.getName().equals("money")) {
			if (SETUP_ACTIVE) {
				commandSender.sendMessage(Common.getInstance().getServerCaller().addColor(SETUP_MODE));
				return true;
			}
			if (args.length == 0) {
				cmd = Common.getInstance().getCommandManager().getMoneyCmdList().get("");
			} else {
				cmd = Common.getInstance().getCommandManager().getMoneyCmdList().get(args[0]);
			}
		} else if (command.getName().equals("bank")) {
			if (SETUP_ACTIVE) {
				commandSender.sendMessage(Common.getInstance().getServerCaller().addColor(SETUP_MODE));
				return true;
			}
			if (args.length == 0) {
				cmd = Common.getInstance().getCommandManager().getBankCmdList().get("");
			} else {
				cmd = Common.getInstance().getCommandManager().getBankCmdList().get(args[0]);
			}
		} else if (command.getName().equals("ccsetup")) {
			if (SETUP_ACTIVE) {
				if (args.length == 0) {
					cmd = Common.getInstance().getCommandManager().getSetupCmdList().get("");
				} else {
					cmd = Common.getInstance().getCommandManager().getSetupCmdList().get(args[0]);
				}
			}

		} else if (command.getName().equals("currency")) {
			if (SETUP_ACTIVE) {
				commandSender.sendMessage(Common.getInstance().getServerCaller().addColor(SETUP_MODE));
				return true;
			}
			if (args.length == 0) {
				cmd = Common.getInstance().getCommandManager().getCurrencyCmdList().get("");
			} else {
				cmd = Common.getInstance().getCommandManager().getCurrencyCmdList().get(args[0]);
			}

		} else if (command.getName().equals("craftconomy")) {
			if (SETUP_ACTIVE) {
				commandSender.sendMessage(Common.getInstance().getServerCaller().addColor(SETUP_MODE));
				return true;
			}
			if (args.length == 0) {
				cmd = Common.getInstance().getCommandManager().getConfigCmdList().get("help");
			} else {
				cmd = Common.getInstance().getCommandManager().getConfigCmdList().get(args[0]);
			}

		} else if (command.getName().equals("payday")) {
			if (SETUP_ACTIVE) {
				commandSender.sendMessage(Common.getInstance().getServerCaller().addColor(SETUP_MODE));
				return true;
			}
			if (args.length == 0) {
				cmd = Common.getInstance().getCommandManager().getPaydayCmdList().get("help");
			} else {
				cmd = Common.getInstance().getCommandManager().getPaydayCmdList().get(args[0]);
			}

		} else {
			return false;
		}

		if (cmd != null) {
			if (cmd.playerOnly() && !(commandSender instanceof Player)) {
					commandSender.sendMessage(Common.getInstance().getServerCaller().addColor(Caller.CHAT_PREFIX + "{{DARK_RED}}Only a player can use this command!"));
					return true;
			}
			if (!(commandSender instanceof Player) || cmd.permission(commandSender.getName())) {
				String[] newargs;
				if (args.length == 0) {
					newargs = new String[0];
				} else {
					newargs = new String[args.length - 1];
					System.arraycopy(args, 1, newargs, 0, args.length - 1);
				}

				if (newargs.length >= cmd.minArgs() && newargs.length <= cmd.maxArgs()) {
					cmd.execute(commandSender.getName(), newargs);
					return true;

				} else {
					commandSender.sendMessage(Common.getInstance().getServerCaller().addColor(Caller.CHAT_PREFIX + cmd.help()));
					return true;
				}
			} else {
				commandSender.sendMessage(Common.getInstance().getServerCaller().addColor(Caller.CHAT_PREFIX + "{{DARK_RED}}You don't have enough permissions!"));
				return true;
			}
		} else {
			commandSender.sendMessage(Common.getInstance().getServerCaller().addColor(Caller.CHAT_PREFIX + "{{DARK_RED}}Sub-Command not found! Use {{WHITE}}/" + command.getName() + " help {{DARK_RED}} for help."));
			return true;
		}
	}

}
