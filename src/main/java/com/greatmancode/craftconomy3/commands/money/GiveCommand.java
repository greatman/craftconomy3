package com.greatmancode.craftconomy3.commands.money;

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.commands.CraftconomyCommand;

public class GiveCommand implements CraftconomyCommand {

	@Override
	public void execute(String sender, String[] args) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean permission(String sender) {
		// TODO Auto-generated method stub
		return Common.getInstance().getServerCaller().checkPermission(sender, "craftconomy.money.give");
	}

	@Override
	public String help() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int maxArgs() {
		// TODO Auto-generated method stub
		return 4;
	}

	@Override
	public int minArgs() {
		// TODO Auto-generated method stub
		return 2;
	}

	@Override
	public boolean playerOnly() {
		// TODO Auto-generated method stub
		return false;
	}

}
