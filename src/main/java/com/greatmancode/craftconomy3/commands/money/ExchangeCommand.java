package com.greatmancode.craftconomy3.commands.money;

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.commands.interfaces.CraftconomyCommand;
import com.greatmancode.craftconomy3.currency.Currency;
import com.greatmancode.craftconomy3.utils.Tools;

public class ExchangeCommand extends CraftconomyCommand {

	@Override
	public void execute(String sender, String[] args) {
		/*if (Tools.isValidDouble(args[0]) && Tools.isValidDouble(args[2])) {
		 	Currency currency1 = Common.getInstance().getCurrencyManager().getCurrency(args[1]);
		 	Currency currency2 = Common.getInstance().getCurrencyManager().getCurrency(args[3]);
		 	if (currency1 != null && currency2 != null) {
		 		
		 	} else {
		 		Common.getInstance().getServerCaller().sendMessage(sender, Common.getInstance().getLanguageManager().getString("currency_not_exist"));
		 	}
		} else {
			Common.getInstance().getServerCaller().sendMessage(sender, Common.getInstance().getLanguageManager().getString("invalid_amount"));
		}*/

	}

	@Override
	public String help() {
		return Common.getInstance().getLanguageManager().getString("money_exchange_cmd_help");
	}

	@Override
	public int maxArgs() {
		return 4;
	}

	@Override
	public int minArgs() {
		return 4;
	}

	@Override
	public boolean playerOnly() {
		return true;
	}

	@Override
	public String getPermissionNode() {
		return "craftconomy.money.exchange";
	}

}
