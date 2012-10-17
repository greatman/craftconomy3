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
import com.greatmancode.craftconomy3.commands.interfaces.CraftconomyCommand;
import com.greatmancode.craftconomy3.database.tables.ConfigTable;
import com.greatmancode.craftconomy3.utils.Tools;

public class SetupBasicCommand extends CraftconomyCommand {

	private static String defaultAmount = null, bankprice = null, longmode = null;

	@Override
	public void execute(String sender, String[] args) {
		if (SetupWizard.getState() == SetupWizard.BASIC_SETUP) {
			if (args.length == 0) {
				Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}Basic setup step. Here, we will configure how Craftconomy behave globally.");
				Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}First, how much money people will have initially in their account (Of the Default currency set earlier)?. Type {{WHITE}}/ccsetup basic default <Amount>");
			} else if (args.length == 2) {
				if (args[0].equals("default")) {
					if (Tools.isValidDouble(args[1])) {
						defaultAmount = args[1];
						Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}Alright. Now, how much you want your users to pay for a bank account? Type {{WHITE}}/ccsetup basic bank <Price>");
					} else {
						Common.getInstance().getServerCaller().sendMessage(sender, args[1] + "{{DARK_RED}} Is not a valid number! Something valid is something like 30.0");
					}

				} else if (args[0].equals("bank")) {
					if (Tools.isValidDouble(args[1])) {
						bankprice = args[1];
						Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}Alright. Now, I want to know if you want the long formatting or not.");
						Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}Long formatting is: {{WHITE}}30 Dollars 1 Coin. {{DARK_GREEN}}No long formatting is: {{WHITE}}30.32 Dollars");
						Common.getInstance().getServerCaller().sendMessage(sender, "Type /ccsetup basic format <True/False>");
					} else {
						Common.getInstance().getServerCaller().sendMessage(sender, args[1] + "{{DARK_RED}} Is not a valid number! Something valid is something like 30.0");
					}
				} else if (args[0].equals("format")) {
					if (Tools.isBoolean(args[1])) {
						longmode = args[1];
						Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}Awesome! Now, type {{WHITE}}/ccsetup basic confirm {{DARK_GREEN}}to save everything!");
					} else {
						Common.getInstance().getServerCaller().sendMessage(sender, args[1] + "{{DARK_RED}} is a invalid value!");
					}
				} else {
					Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_RED}}Sub-Command not found.");
				}
			} else if (args[0].equals("confirm")) {
				if (defaultAmount != null && bankprice != null && longmode != null) {
					ConfigTable table = new ConfigTable();
					table.setName("holdings");
					table.setValue(defaultAmount);
					Common.getInstance().getDatabaseManager().getDatabase().save(table);
					table = new ConfigTable();
					table.setName("bankprice");
					table.setValue(bankprice);
					Common.getInstance().getDatabaseManager().getDatabase().save(table);
					table = new ConfigTable();
					table.setName("longmode");
					table.setValue(longmode);
					Common.getInstance().getDatabaseManager().getDatabase().save(table);
					SetupWizard.setState(SetupWizard.CONVERT_SETUP);
					Common.getInstance().startUp();
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

	@Override
	public String getPermissionNode() {
		return "craftconomy.setup";
	}

}
