package com.greatmancode.craftconomy3.commands.currency;

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.commands.CraftconomyCommand;

public class CurrencyDefaultCommand implements CraftconomyCommand {

	@Override
	public void execute(String sender, String[] args) {
		if (Common.getInstance().getCurrencyManager().getCurrency(args[0]) != null) {
			Common.getInstance().getCurrencyManager().setDefault(Common.getInstance().getCurrencyManager().getCurrency(args[0]).getDatabaseID());
			Common.getInstance().getServerCaller().sendMessage(sender, args[0] + " {{DARK_GREEN}}has been set as the default currency!");
		} else {
			Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_RED}}Currency not found!");
		}

	}

	@Override
	public boolean permission(String sender) {
		return Common.getInstance().getServerCaller().checkPermission(sender, "craftconomy.payday.default");
	}

	@Override
	public String help() {
		return "/currency default <Name> - Set a currency as the default one.";
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
