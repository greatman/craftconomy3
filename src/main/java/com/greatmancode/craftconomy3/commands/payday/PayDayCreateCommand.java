package com.greatmancode.craftconomy3.commands.payday;

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.commands.CraftconomyCommand;
import com.greatmancode.craftconomy3.utils.Tools;

public class PayDayCreateCommand implements CraftconomyCommand {

	@Override
	public void execute(String sender, String[] args) {
		if (Common.getInstance().getPaydayManager().getPayDay(args[0]) == null) {
			if (Tools.isInteger(args[1])) {
				
			} else {
				Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_RED}}Invalid interval!");
			}
		} else {
			Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_RED}}There's already a payday named like that!");
		}
	}

	@Override
	public boolean permission(String sender) {
		return Common.getInstance().getServerCaller().checkPermission(sender, "craftconomy.payday.create");
	}

	@Override
	public String help() {
		return "/payday create <Name> <Interval> <wage/tax> <value> [Account] [Currency Name] [World Name] - Create a new payday";
	}

	@Override
	public int maxArgs() {
		return 7;
	}

	@Override
	public int minArgs() {
		return 4;
	}

	@Override
	public boolean playerOnly() {
		return false;
	}

}
