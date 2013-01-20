package com.greatmancode.craftconomy3.commands.managers;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.commands.interfaces.CommandManager;

public class ForgeCommandManager implements ICommand, CommandManager {

	private String commandName;
	public ForgeCommandManager(String commandName) {
		this.commandName = commandName;
	}

	@Override
	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List addTabCompletionOptions(ICommandSender arg0, String[] arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List getCommandAliases() {
		return new ArrayList();
	}

	@Override
	public String getCommandName() {
		return commandName;
	}

	@Override
	public String getCommandUsage(ICommandSender arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isUsernameIndex(int arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void processCommand(ICommandSender commandSender, String[] args) {
		if (Common.getInstance().getCommandManager().commandExist(commandName)) {
			String[] newargs;
			if (args.length == 0) {
				newargs = new String[0];
				args = new String[1];
				args[0] = "";
			} else {
				newargs = new String[args.length - 1];
				System.arraycopy(args, 1, newargs, 0, args.length - 1);
			}
			Common.getInstance().getCommandManager().getCommandHandler(commandName).execute(commandSender.getCommandSenderName(), args[0], newargs);
		}
	}

}
