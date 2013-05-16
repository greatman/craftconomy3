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
package com.greatmancode.craftconomy3.commands.managers;

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.commands.interfaces.CommandManager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Handles the commands for the Bukkit server
 * @author greatman
 */
public class BukkitCommandManager implements CommandExecutor, CommandManager {
	@Override
	public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] args) {
		if (Common.getInstance().getCommandManager().commandExist(command.getName())) {

			String[] newargs;
			if (args.length == 0) {
				newargs = new String[0];
				args = new String[1];
				args[0] = "";
			} else {
				newargs = new String[args.length - 1];
				System.arraycopy(args, 1, newargs, 0, args.length - 1);
			}
			Common.getInstance().getCommandManager().getCommandHandler(command.getName()).execute(commandSender.getName(), args[0], newargs);
			return true;
		}
		return false;
	}
}
