package com.greatmancode.craftconomy3.commands.config;

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.commands.CraftconomyCommand;
import com.greatmancode.craftconomy3.utils.Tools;

public class ConfigBankPriceCommand implements CraftconomyCommand {

	@Override
	public void execute(String sender, String[] args) {
		if (Tools.isValidDouble(args[0])) {
			Common.getInstance().getConfigurationManager().setBankPrice(Double.parseDouble(args[0]));
			Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}Bank price modified!");
		} else {
			Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_RED}}Invalid amount!");
		}

	}

	@Override
	public boolean permission(String sender) {
		return Common.getInstance().getServerCaller().checkPermission(sender, "craftconomy.config.bankprice");
	}

	@Override
	public String help() {
		return "/craftconomy bankprice <Amount> - Change the price to create a bank account.";
	}

	@Override
	public int maxArgs() {
		return 1;
	}

	@Override
	public int minArgs() {
		return 1;
	}

	@Override
	public boolean playerOnly() {
		return false;
	}

}
