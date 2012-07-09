package com.greatmancode.craftconomy3.commands.money;

import java.util.Iterator;

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.account.Account;
import com.greatmancode.craftconomy3.account.Balance;
import com.greatmancode.craftconomy3.commands.CraftconomyCommand;

public class AllCommand implements CraftconomyCommand {

	@Override
	public void execute(String sender, String[] args) {
		// TODO Auto-generated method stub
		Account account = Common.getInstance().getAccountHandler().getAccount(sender);
		Iterator<Balance> balanceList = account.getAllBalance().iterator();
		while(balanceList.hasNext())
		{
			
		}
	}

	@Override
	public boolean permission(String sender) {
		// TODO Auto-generated method stub
		return Common.getInstance().getServerCaller().checkPermission(sender, "craftconomy.money.balance");
	}

	@Override
	public String help() {
		// TODO Auto-generated method stub
		return null;
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
