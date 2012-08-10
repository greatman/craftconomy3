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

import org.spout.api.chat.ChatArguments;
import org.spout.api.command.Command;
import org.spout.api.command.CommandContext;
import org.spout.api.command.CommandExecutor;
import org.spout.api.command.CommandSource;
import org.spout.api.exception.CommandException;
import org.spout.api.player.Player;

import com.greatmancode.craftconomy3.Caller;
import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.CC3SpoutLoader;

/**
 * Handle the commands for the Spout server.
 * @author greatman
 *
 */
public class SpoutCommandManager implements CommandExecutor, CommandManager {

	public SpoutCommandManager() {
		CC3SpoutLoader.getInstance().getEngine().getRootCommand().addSubCommand(CC3SpoutLoader.getInstance(), "money").setHelp("Money Related Commands").setExecutor(this);
	}

	@Override
	public boolean processCommand(CommandSource source, Command command, CommandContext args) throws CommandException {
		CraftconomyCommand cmd = null;
		if (command.getPreferredName().equals("money")) {
			if (args.length() == 0) {
				cmd = Common.getInstance().getCommandManager().getMoneyCmdList().get("");
			} else {
				Common.getInstance().getCommandManager().getMoneyCmdList().get(args.getString(0));
			}
		} else if (command.getPreferredName().equals("bank")) {
			if (args.length() == 0) {
				cmd = Common.getInstance().getCommandManager().getBankCmdList().get("");
			} else {
				Common.getInstance().getCommandManager().getBankCmdList().get(args.getString(0));
			}
		}
		if (cmd != null) {
			if (cmd.playerOnly()) {
				if (!(source instanceof Player)) {
					source.sendMessage(ChatArguments.fromString(Caller.CHAT_PREFIX + "{{DARK_RED}}Only a player can use this command!"));
					return true;
				}
			}

			if (!(source instanceof Player) || cmd.permission(source.getName())) {
				String[] newargs;
				if (args.length() == 0) {
					newargs = new String[0];
				} else {
					newargs = new String[args.length() - 1];
					for (int i = 1; i <= newargs.length; i++) {
						newargs[i - 1] = args.getString(i);
					}
				}
				if (newargs.length >= cmd.minArgs() && newargs.length <= cmd.maxArgs()) {
					cmd.execute(source.getName(), newargs);
					return true;
				}
				source.sendMessage(ChatArguments.fromString(Caller.CHAT_PREFIX + cmd.help()));
				return true;
			} else {
				source.sendMessage(ChatArguments.fromString(Caller.CHAT_PREFIX + "{{DARK_RED}}Not enough permissions!"));
				return true;
			}
		}
		return false;
	}

}
