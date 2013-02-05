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

import org.spout.api.command.Command;
import org.spout.api.command.CommandContext;
import org.spout.api.command.CommandExecutor;
import org.spout.api.command.CommandSource;
import org.spout.api.exception.CommandException;

/**
 * Handle the commands for the Spout server.
 * @author greatman
 */
public class SpoutCommandManager implements CommandExecutor, CommandManager {
	@Override
	public void processCommand(CommandSource source, Command command, CommandContext args) throws CommandException {
		if (Common.getInstance().getCommandManager().commandExist(command.getPreferredName())) {
			String[] newargs;
			if (args.length() == 0) {
				newargs = new String[0];
			} else {
				newargs = new String[args.length() - 1];
				for (int i = 1; i <= newargs.length; i++) {
					newargs[i - 1] = args.getString(i);
				}
			}
			if (args.length() == 0) {
				Common.getInstance().getCommandManager().getCommandHandler(command.getPreferredName()).execute(source.getName(), "", newargs);
			} else {
				Common.getInstance().getCommandManager().getCommandHandler(command.getPreferredName()).execute(source.getName(), args.getString(0), newargs);
			}
		}
	}
}
