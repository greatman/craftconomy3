package com.greatmancode.craftconomy3.commands.currency;

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.commands.CraftconomyCommand;

public class CurrencyEditCommand implements CraftconomyCommand {

	@Override
	public void execute(String sender, String[] args) {
		if (Common.getInstance().getCurrencyManager().getCurrency(args[1]) != null) {
			if (!args[2].equals("")) {
				if (args[0].equals("name")) {
					Common.getInstance().getCurrencyManager().getCurrency(args[1]).setName(args[2]);
				} else if (args[0].equals("nameplural")) {
					Common.getInstance().getCurrencyManager().getCurrency(args[1]).setPlural(args[2]);
				} else if (args[0].equals("minor")) {
					Common.getInstance().getCurrencyManager().getCurrency(args[1]).setMinor(args[2]);
				} else if (args[0].equals("minorplural")) {
					Common.getInstance().getCurrencyManager().getCurrency(args[1]).setMinorPlural(args[2]);
				} else {
					Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_RED}}Invalid type!");
					return;
				}
				Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}Currency modified!");
				
			} else {
				Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_RED}}Can't change a currency value to empty (Aka \"\")");
			}
		} else {
			Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_RED}}Currency not found!");
		}

	}

	@Override
	public boolean permission(String sender) {
		return Common.getInstance().getServerCaller().checkPermission(sender, "craftconomy.currency.edit");
	}

	@Override
	public String help() {
		return "/currency edit <name/nameplural/minor/minorplural> <Currency Name> <new Value> - Modify a currency.";
	}

	@Override
	public int maxArgs() {
		return 3;
	}

	@Override
	public int minArgs() {
		return 3;
	}

	@Override
	public boolean playerOnly() {
		return false;
	}

}
