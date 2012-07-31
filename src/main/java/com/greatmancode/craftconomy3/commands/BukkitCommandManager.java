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

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.greatmancode.craftconomy3.Common;

public class BukkitCommandManager implements CommandExecutor, CommandLoader {

	@Override
	public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] args) {

		// TODO: Better way to handle no args.
		if (command.getName().equals("money")) {
			if (args.length == 0) {
				if (Common.getInstance().getCommandManager().getMoneyCmdList().get("").playerOnly()) {
					if (!(commandSender instanceof Player)) {
						commandSender.sendMessage(ChatColor.RED + "Only a player can use this command!");
						return true;
					}
				}
				if (!(commandSender instanceof Player) || Common.getInstance().getCommandManager().getMoneyCmdList().get("").permission(commandSender.getName())) {
					Common.getInstance().getCommandManager().getMoneyCmdList().get("").execute(commandSender.getName(), args);
					return true;
				} else {
					commandSender.sendMessage(ChatColor.RED + "You don't have enough permissions!");
					return true;
				}

			}

			if (Common.getInstance().getCommandManager().getMoneyCmdList().containsKey(args[0])) {
				if (Common.getInstance().getCommandManager().getMoneyCmdList().get(args[0]).playerOnly()) {
					if (!(commandSender instanceof Player)) {
						commandSender.sendMessage(ChatColor.RED + "Only a player can use this command!");
						return true;
					}
				}
				if (!(commandSender instanceof Player) || Common.getInstance().getCommandManager().getMoneyCmdList().get(args[0]).permission(commandSender.getName())) {
					String[] newargs = new String[args.length - 1];
					for (int i = 1; i <= newargs.length; i++) {
						newargs[i - 1] = args[i];
					}
					if (newargs.length >= Common.getInstance().getCommandManager().getMoneyCmdList().get(args[0]).minArgs() && newargs.length <= Common.getInstance().getCommandManager().getMoneyCmdList().get(args[0]).maxArgs()) {
						Common.getInstance().getCommandManager().getMoneyCmdList().get(args[0]).execute(commandSender.getName(), newargs);
						return true;

					} else {
						commandSender.sendMessage(Common.getInstance().getCommandManager().getMoneyCmdList().get(args[0]).help());
						return true;
					}
				} else {
					commandSender.sendMessage(ChatColor.RED + "You don't have enough permissions!");
					return true;
				}
			}
		}

		return false;
	}

}
