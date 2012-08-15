package com.greatmancode.craftconomy3.commands.currency;

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.commands.CraftconomyCommand;

public class CurrencyDeleteCommand implements CraftconomyCommand {

	@Override
	public void execute(String sender, String[] args) {
		if (Common.getInstance().getCurrencyManager().getCurrency(args[0]) != null) {
			Common.getInstance().getCurrencyManager().deleteCurrency(Common.getInstance().getCurrencyManager().getCurrency(args[0]).getDatabaseID());
			Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}Currency deleted!");
		} else {
			Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_RED}}Currency not found!");
		}
	}

	@Override
	public boolean permission(String sender) {
		return Common.getInstance().getServerCaller().checkPermission(sender, "craftconomy.currency.delete");
	}

	@Override
	public String help() {
		return "/currency delete <Name> - Delete a currency {{DARK_RED}}It also deletes all balance with this currency.";
	}

	@Override
	public int maxArgs() {
		return 1;
	}

	@Override
	public int minArgs() {
		return 0;
	}

	@Override
	public boolean playerOnly() {
		return false;
	}

}
