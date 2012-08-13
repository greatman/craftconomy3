/*
 * This file is part of Craftconomy3.
 *
 * Copyright (c) 2011-2012, Greatman <http://github.com/greatman/>
 *
 * Craftconomy3 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Craftconomy3 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Craftconomy3.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.greatmancode.craftconomy3.commands.setup;

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.SetupWizard;
import com.greatmancode.craftconomy3.commands.CraftconomyCommand;
import com.greatmancode.craftconomy3.database.tables.CurrencyTable;

public class SetupCurrencyCommand implements CraftconomyCommand {

	public static boolean usecc = false;
	public static String name = null, nameplural = null, minor = null, minorplural = null;
	@Override
	public void execute(String sender, String[] args) {
		if (SetupWizard.getState() == 3) {
			if (args.length == 0) {
				Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}Alright! Half-way done! Before we continue, I must ask you a question. Do you want to convert from Craftconomy V2? Type {{WHITE}}/ccsetup currency yes {{DARK_GREEN}}or {{WHITE}}/ccsetup currency no");
			} else if (args.length == 1) {
				if (args[0].equals("yes")) {
					usecc = true;
					SetupWizard.setState(4);
					Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}Okay! Type /ccsetup basic to configure the basic settings of Craftconomy!");

				} else if (args[0].equals("no")) {
					Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}Alright! Welcome to Craftconomy! We use a Multi-Currency system. I need you to write the settings for the default currency.");
					Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}First, let's configure the main currency name (Ex. Dollar). Type {{WHITE}}/ccsetup currency name <Name>");
					
				} else if (args[0].equals("confirm")) {
					if (name != null && nameplural != null && minor != null && minorplural != null) {
						CurrencyTable currencyEntry = new CurrencyTable();
						currencyEntry.name = name;
						currencyEntry.plural = nameplural;
						currencyEntry.minor = minor;
						currencyEntry.minorplural = minorplural;
						currencyEntry.status = true;
						Common.getInstance().getDatabaseManager().getDatabase().save(currencyEntry);
						SetupWizard.setState(4);
						Common.getInstance().initializeCurrency();
						Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}Way to go! Only 2 steps left! (Basics settings & Conversion from another system if needed). Type {{WHITE}}/ccsetup basic {{DARK_GREEN}}to continue");
					} else if (name == null) {
						Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_RED}}Your currency name is empty! Type {{WHITE}}/ccsetup currency name <Name>");
					} else if (nameplural == null) {
						Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_RED}}Your currency name in plural is empty! Type {{WHITE}}/ccsetup currency plural <Name>");
					} else if (minor == null) {
						Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_RED}}Your currency minor name is empty! Type {{WHITE}}/ccsetup currency minor <Name>");
					} else if (minorplural == null) {
						Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_RED}}Your currency minor name in plural is empty! Type {{WHITE}}/ccsetup currency minorplural <Name>");
					}
				} else {
					Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_RED}}Sub-Command not found.");
				}
			} else {
				if (args[0].equals("name")) {
					name = args[1];
					Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}Currency Name set to {{WHITE}}" + args[1] + "{{DARK_GREEN}}. Now, let's set the Main currency name in plural (Ex. Dollars). Type {{WHITE}}/ccsetup currency plural <Name>");
				} else if (args[0].equals("plural")) {
					nameplural = args[1];
					Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}Currency Plural set to {{WHITE}}" + args[1] + "{{DARK_GREEN}}. Now, let's set the minor currency name (Ex. Coin). Type {{WHITE}}/ccsetup currency minor <Name>");
				} else if (args[0].equals("minor")) {
					minor = args[1];
					Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}Currency minor set to {{WHITE}}" + args[1] + "{{DARK_GREEN}}. Now, let's set the minor currency name in plural (Ex. Coins). Type {{WHITE}}/ccsetup currency minorplural <Name>");
				} else if (args[0].equals("minorplural")) {
					minorplural = args[1];
					Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}Currency minor Plural set to {{WHITE}}" + args[1] + "{{DARK_GREEN}}. Seems like your done for this part! Type {{WHITE}}/ccsetup currency confirm {{DARK_GREEN}}to finish the setup.");
				} else {
					Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_RED}}Sub-Command not found.");
				}
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
		return "/ccsetup - Start the setup";
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
