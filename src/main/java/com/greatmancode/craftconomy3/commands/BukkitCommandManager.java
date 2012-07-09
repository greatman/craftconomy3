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
		if (command.getName().equals("money")) {
			if (args.length == 0) {
				Common.getInstance().getCommandManager().getMoneyCmdList().get("").execute(commandSender.getName(), args);
				return true;
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
					for (int i = 1; i < newargs.length; i++) {
						newargs[i - 1] = args[i];
					}
					Common.getInstance().getCommandManager().getMoneyCmdList().get(args[0]).execute(commandSender.getName(), newargs);
					return true;
				} else {
					commandSender.sendMessage(ChatColor.RED + "You don't have enough permissions!");
					return true;
				}
			}
		}

		return false;
	}

}
