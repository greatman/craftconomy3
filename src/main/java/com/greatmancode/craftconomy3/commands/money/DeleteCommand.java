package com.greatmancode.craftconomy3.commands.money;

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.commands.CraftconomyCommand;
import com.greatmancode.craftconomy3.database.tables.AccountTable;
import com.greatmancode.craftconomy3.database.tables.BalanceTable;

public class DeleteCommand implements CraftconomyCommand {

	@Override
	public void execute(String sender, String[] args) {
		if (Common.getInstance().getAccountHandler().exist(args[0])) {
			AccountTable account = Common.getInstance().getDatabaseManager().getDatabase().select(AccountTable.class).where().contains("name", args[0]).execute().findOne();
			Common.getInstance().getDatabaseManager().getDatabase().remove(Common.getInstance().getDatabaseManager().getDatabase().select(BalanceTable.class).where().contains("username_id", account.id).execute().find());
			Common.getInstance().getDatabaseManager().getDatabase().remove(account);
			Common.getInstance().getServerCaller().sendMessage(sender, "{{GREEN}}The account {{WHITE}}" + args[0] + " {{GREEN}}has been deleted!");
		} else {
			Common.getInstance().getServerCaller().sendMessage(sender, "{{RED}}This account doesn't exist!");
		}
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
