package com.greatmancode.craftconomy3.commands;

import org.bukkit.ChatColor;
import org.spout.api.command.Command;
import org.spout.api.command.CommandContext;
import org.spout.api.command.CommandExecutor;
import org.spout.api.command.CommandSource;
import org.spout.api.exception.CommandException;
import org.spout.api.player.Player;

import com.greatmancode.craftconomy3.Common;

public class SpoutCommandManager implements CommandExecutor, CommandLoader {

	@Override
	public boolean processCommand(CommandSource source, Command command, CommandContext args) throws CommandException {
		if (command.getPreferredName().equals("money")) {
			if (args.length() == 0) {

				Common.getInstance().getCommandManager().getMoneyCmdList().get("").execute(source.getName(), args.getRawArgs());
				return true;
			}
			if (Common.getInstance().getCommandManager().getMoneyCmdList().containsKey(args.getString(0))) {

				if (Common.getInstance().getCommandManager().getMoneyCmdList().get(args.getString(0)).playerOnly()) {
					if (!(source instanceof Player)) {
						source.sendMessage(ChatColor.RED + "Only a player can use this command!");
						return true;
					}
				}

				if (!(source instanceof Player) || Common.getInstance().getCommandManager().getMoneyCmdList().get(args.getString(0)).permission(source.getName())) {
					String[] newargs = new String[args.length() - 1];
					for (int i = 1; i < args.length(); i++)
					{
						newargs[i - 1] = args.getString(i);
					}
					if (newargs.length >= Common.getInstance().getCommandManager().getMoneyCmdList().get(args.getString(0)).minArgs() && newargs.length <= Common.getInstance().getCommandManager().getMoneyCmdList().get(args.getString(0)).maxArgs())
					{
						source.sendMessage(Common.getInstance().getCommandManager().getMoneyCmdList().get(args.getString(0)).help());
						return true;
					}
					Common.getInstance().getCommandManager().getMoneyCmdList().get(args.getString(0)).execute(source.getName(), newargs);
					return true;
				} else {
					source.sendMessage(ChatColor.RED + "Not enough permissions!");
					return true;
				}
			}
		}
		return false;
	}

}
