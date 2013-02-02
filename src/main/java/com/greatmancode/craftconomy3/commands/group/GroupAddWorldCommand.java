package com.greatmancode.craftconomy3.commands.group;

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.commands.interfaces.CraftconomyCommand;

public class GroupAddWorldCommand extends CraftconomyCommand{
	@Override
	public void execute(String sender, String[] args) {
		if (Common.getInstance().getWorldGroupManager().worldGroupExist(args[0])) {
			if (Common.getInstance().getServerCaller().worldExist(args[1])) {
				Common.getInstance().getWorldGroupManager().addWorldToGroup(args[0], args[1]);
				Common.getInstance().getServerCaller().sendMessage(sender, Common.getInstance().getLanguageManager().getString("group_world_added"));
			} else {
				Common.getInstance().getServerCaller().sendMessage(sender, Common.getInstance().getLanguageManager().getString("world_not_exist"));
			}
		} else {
			Common.getInstance().getServerCaller().sendMessage(sender, Common.getInstance().getLanguageManager().getString("group_not_exist"));
		}
	}

	@Override
	public String help() {
		return Common.getInstance().getLanguageManager().getString("group_addworld_cmd_help");
	}

	@Override
	public int maxArgs() {
		return 2;
	}

	@Override
	public int minArgs() {
		return 2;
	}

	@Override
	public boolean playerOnly() {
		return false;
	}

	@Override
	public String getPermissionNode() {
		return "craftconomy.group.addworld";
	}
}
