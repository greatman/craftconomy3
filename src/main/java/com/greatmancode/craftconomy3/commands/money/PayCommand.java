package com.greatmancode.craftconomy3.commands.money;

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.commands.CraftconomyCommand;
import com.greatmancode.craftconomy3.currency.Currency;
import com.greatmancode.craftconomy3.utils.Tools;

public class PayCommand implements CraftconomyCommand {

	@Override
	public void execute(String sender, String[] args) {
		if (Common.getInstance().getServerCaller().isOnline(args[0])) {
			if (Tools.isdouble(args[1])) {
				double amount = Double.parseDouble(args[1]);
				boolean hasEnough = false;
				Currency currency = Common.getInstance().getCurrencyManager().getCurrency(Common.getInstance().getConfigurationManager().getConfig().getString("System.Default.Currency.Name"));
				
				if (args.length > 2) {
					if (Common.getInstance().getCurrencyManager().getCurrency(args[2]) != null) {
						currency = Common.getInstance().getCurrencyManager().getCurrency(args[2]);
					} else {
						Common.getInstance().getServerCaller().sendMessage(sender, "&4That currency doesn't exist!");
						return;
					}
				}
				hasEnough = Common.getInstance().getAccountHandler().getAccount(sender).hasEnough(amount, Common.getInstance().getServerCaller().getPlayerWorld(sender), currency.getName());

				if (hasEnough) {
					Common.getInstance().getAccountHandler().getAccount(sender).withdraw(amount, Common.getInstance().getServerCaller().getPlayerWorld(sender), currency.getName());
					Common.getInstance().getAccountHandler().getAccount(args[0]).deposit(amount, Common.getInstance().getServerCaller().getPlayerWorld(sender), currency.getName());
					Common.getInstance().getServerCaller().sendMessage(sender, "&2Sent &f" + Common.getInstance().format(null, currency, amount) + "&2 to &f" + args[0]);
					Common.getInstance().getServerCaller().sendMessage(sender, "&2Received &f" + Common.getInstance().format(null, currency, amount) + "&2 from &f" + sender);
				} else {
					Common.getInstance().getServerCaller().sendMessage(sender, "&4 You don't have enough money!");
				}
			} else {
				Common.getInstance().getServerCaller().sendMessage(sender, "&4Excepted Double as Amount. Received something else!");
			}
		} else {
			Common.getInstance().getServerCaller().sendMessage(sender, "&4The player isin't online!");
		}
	}

	@Override
	public boolean permission(String sender) {
		return Common.getInstance().getServerCaller().checkPermission(sender, "craftconomy.money.pay");
	}

	@Override
	public String help() {
		return "/money pay <Player Name> <Amount> *<Currency> - Send money to someone";
	}

	@Override
	public int maxArgs() {
		return 3;
	}

	@Override
	public int minArgs() {
		return 2;
	}

	@Override
	public boolean playerOnly() {
		return true;
	}

}
