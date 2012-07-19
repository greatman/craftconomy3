package com.greatmancode.craftconomy3.commands.money;

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.commands.CraftconomyCommand;
import com.greatmancode.craftconomy3.currency.Currency;
import com.greatmancode.craftconomy3.currency.CurrencyManager;
import com.greatmancode.craftconomy3.utils.Tools;

public class GiveCommand implements CraftconomyCommand {

	@Override
	public void execute(String sender, String[] args) {
		if (Common.getInstance().getServerCaller().isOnline(args[0])) {
			if (Tools.isValidDouble(args[1])) {
				double amount = Double.parseDouble(args[1]);
				Currency currency = Common.getInstance().getCurrencyManager().getCurrency(CurrencyManager.DefaultCurrencyID);
				if (args.length > 2) {
					if (Common.getInstance().getCurrencyManager().getCurrency(args[2]) != null) {
						currency = Common.getInstance().getCurrencyManager().getCurrency(args[2]);
					} else {
						Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_RED}}That currency doesn't exist!");
						return;
					}
				}
				String worldName = "any";
				if (args.length > 3) {
					if (Common.getInstance().getConfigurationManager().getConfig().getBoolean("System.Default.Currency.MultiWorld")) {
						if (!Common.getInstance().getServerCaller().worldExist(args[3])) {
							Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_RED}}This world doesn't exist!");
							return;
						}
						worldName = args[3];
					}
				} else {
					if (!Common.getInstance().getServerCaller().isOnline(sender)) {
						worldName = Common.getInstance().getServerCaller().getDefaultWorld();
					} else {
						worldName = Common.getInstance().getAccountHandler().getAccount(sender).getWorldPlayerCurrentlyIn();
					}
				}

				Common.getInstance().getAccountHandler().getAccount(args[0]).deposit(amount, worldName, currency.getName());
				Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}Gave {{WHITE}}" + Common.getInstance().format(worldName, currency, amount) + "{{DARK_GREEN}} to {{WHITE}}" + args[0]);
				Common.getInstance().getServerCaller().sendMessage(args[0], "{{DARK_GREEN}}Received {{WHITE}}" + Common.getInstance().format(worldName, currency, amount) + "{{DARK_GREEN}} from {{WHITE}}" + sender);
			} else {
				Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_RED}}Excepted a positive number as Amount. Received something else!");
			}
		} else {
			Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_RED}}The player isin't online!");
		}
	}

	@Override
	public boolean permission(String sender) {
		return Common.getInstance().getServerCaller().checkPermission(sender, "craftconomy.money.give");
	}

	@Override
	public String help() {
		return "/money give <Player Name> <Amount> *<Currency> *<World> - Give money to someone";
	}

	@Override
	public int maxArgs() {
		return 4;
	}

	@Override
	public int minArgs() {
		return 2;
	}

	@Override
	public boolean playerOnly() {
		return false;
	}

}
