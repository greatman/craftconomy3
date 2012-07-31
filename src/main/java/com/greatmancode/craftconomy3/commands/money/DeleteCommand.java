package com.greatmancode.craftconomy3.commands.money;

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.commands.CraftconomyCommand;

public class DeleteCommand implements CraftconomyCommand {

	@Override
	public void execute(String sender, String[] args) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean permission(String sender) {
		// TODO Auto-generated method stub
		return Common.getInstance().getServerCaller().checkPermission(sender, "craftconomy.account.delete");
	}

	@Override
	public String help() {
		// TODO Auto-generated method stub
		return "/money delete <Name> - Delete a account";
	}

	@Override
	public int maxArgs() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public int minArgs() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public boolean playerOnly() {
		// TODO Auto-generated method stub
		return false;
	}

}
