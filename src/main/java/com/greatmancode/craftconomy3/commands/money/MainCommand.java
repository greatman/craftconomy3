package com.greatmancode.craftconomy3.commands.money;

import java.util.Iterator;

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.account.Account;
import com.greatmancode.craftconomy3.account.Balance;
import com.greatmancode.craftconomy3.commands.CraftconomyCommand;

public class MainCommand implements CraftconomyCommand {

	@Override
	public void execute(String sender, String[] args) {
		Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}Balance: ");
		Account account = Common.getInstance().getAccountHandler().getAccount(sender);
		Iterator<Balance> balanceList = account.getAllWorldBalance(account.getWorldPlayerCurrentlyIn()).iterator();
		while (balanceList.hasNext())
		{
			Balance bl = balanceList.next();
			Common.getInstance().getServerCaller().sendMessage(sender, Common.getInstance().format(bl.getWorld(), bl.getCurrency(), bl.getBalance()));
		}
	}

	@Override
	public boolean permission(String sender) {
		return Common.getInstance().getServerCaller().checkPermission(sender, "craftconomy.money.balance");
	}

	@Override
	public String help() {
		return "/money  - List your balance";
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
		return true;
	}

}
