package com.greatmancode.craftconomy3.commands.setup;

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.SetupWizard;
import com.greatmancode.craftconomy3.commands.CraftconomyCommand;
import com.greatmancode.craftconomy3.currency.Currency;
import com.greatmancode.craftconomy3.currency.CurrencyManager;
import com.greatmancode.craftconomy3.database.tables.ConfigTable;
import com.greatmancode.craftconomy3.utils.Tools;

public class SetupBasicCommand implements CraftconomyCommand {

	public static String defaultAmount = null, bankprice = null, longmode = null;

	@Override
	public void execute(String sender, String[] args) {
		if (SetupWizard.getState() == 4) {
			if (args.length == 0) {
				Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}Basic setup step. Here, we will configure how Craftconomy behave globally.");
				Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}First, how much money people will have initially in their account (Of the Default currency set earlier)?. Type /ccsetup basic default <Amount>");
			} else if (args.length == 2) {
				if (args[0].equals("default")) {
					if (Tools.isValidDouble(args[1])) {
						defaultAmount = args[1];
						Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}Alright. Now, how much you want your users to pay for a bank account? Type /ccsetup basic bank <Price>");
					} else {
						Common.getInstance().getServerCaller().sendMessage(sender, args[1] + "{{DARK_RED}} Is not a valid number! Something valid is something like 30.0");
					}

				} else if (args[0].equals("bank")) {
					if (Tools.isValidDouble(args[1])) {
						bankprice = args[1];
						Currency currency = Common.getInstance().getCurrencyManager().getCurrency(CurrencyManager.defaultCurrencyID);
						Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}Alright. Now, I want to know if you want the long formatting or not.");
						Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}Long formatting is: {{WHITE}}30 " + currency.getPlural() + " 1 " + currency.getMinor() + ". {{DARK_GREEN}}No long formatting is: {{WHITE}}30.32 " + currency.getPlural());
						Common.getInstance().getServerCaller().sendMessage(sender, "Type /ccsetup basic format <True/False>");
					} else {
						Common.getInstance().getServerCaller().sendMessage(sender, args[1] + "{{DARK_RED}} Is not a valid number! Something valid is something like 30.0");
					}
				} else if (args[0].equals("format")) {
					if (args[1].equals("true") || args[1].equals("false")) {
						longmode = args[1];
						Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}Awesome! Now, type /ccsetup basic confirm to save everything!");
					} else {
						Common.getInstance().getServerCaller().sendMessage(sender, args[1] + "{{DARK_RED}} is a invalid value!");
					}
				} else {
					Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_RED}}Sub-Command not found.");
				}
			} else if (args[0].equals("confirm")) {
				if (defaultAmount != null && bankprice != null && longmode != null) {
					ConfigTable table = new ConfigTable();
					table.name = "holdings";
					table.value = defaultAmount;
					Common.getInstance().getDatabaseManager().getDatabase().save(table);
					table = new ConfigTable();
					table.name = "bankprice";
					table.value = bankprice;
					Common.getInstance().getDatabaseManager().getDatabase().save(table);
					table.name = "longmode";
					table.value = longmode;
					Common.getInstance().getDatabaseManager().getDatabase().save(table);
					SetupWizard.setState(5);
					Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}Only 1 step left! Type {{WHITE}}/ccsetup convert {{DARK_GREEN}}to continue!");
				}
			} else {
				Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_RED}}Sub-Command not found.");
			}
		} else {
			Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_RED}}Wrong setup status for this cmd. If you didin't start the setup yet, use /ccsetup");
		}

	}

	@Override
	public boolean permission(String sender) {
		return Common.getInstance().getServerCaller().checkPermission(sender, "craftconomy.setup");
	}

	@Override
	public String help() {
		return "/ccsetup basic - Basic configuration";
	}

	@Override
	public int maxArgs() {
		return 2;
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
