package com.greatmancode.craftconomy3.commands.currency;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.commands.CraftconomyCommand;

public class CurrencyHelpCommand implements CraftconomyCommand {

	@Override
	public void execute(String sender, String[] args) {
		Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}} ======== Currency Commands ========");
		HashMap<String, CraftconomyCommand> cmdList = Common.getInstance().getCommandManager().getCurrencyCmdList();
		Iterator<Entry<String, CraftconomyCommand>> iterator = cmdList.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, CraftconomyCommand> entry = iterator.next();
			if (entry.getValue().permission(sender)) {
				Common.getInstance().getServerCaller().sendMessage(sender, entry.getValue().help());
			}
		}
	}

	@Override
	public boolean permission(String sender) {
		return true;
	}

	@Override
	public String help() {
		return "/currency - shows currency command help";
	}

	@Override
	public int maxArgs() {
		return 0;
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
