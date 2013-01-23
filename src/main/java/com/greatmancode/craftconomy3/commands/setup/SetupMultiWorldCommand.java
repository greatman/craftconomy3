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

public class SetupMultiWorldCommand extends CraftconomyCommand {
	private static ConfigTable oldValue = null;

	@Override
	public void execute(String sender, String[] args) {
		if (SetupWizard.getState() == SetupWizard.MULTIWORLD_SETUP) {
			if (args.length == 0) {
				Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}Do you wish to have a Multiworld economy system? (Different wallet on each world). Type {{WHITE}}/ccsetup multiworld true {{DARK_GREEN}} for yes and {{WHITE}}/ccsetup multiworld false {{DARK_GREEN}} for no.");
			} else {
				if (args[0].equals("true") || args[0].equals("false")) {
					if (oldValue != null) {
						Common.getInstance().getDatabaseManager().getDatabase().remove(oldValue);
					}

					oldValue = new ConfigTable();
					oldValue.setName("multiworld");
					oldValue.setValue(args[0]);
					Common.getInstance().getDatabaseManager().getDatabase().save(oldValue);
					Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}You set Multiworld to " + args[0] + ". If you accept this setting, type {{WHITE}}/ccsetup multiworld confirm {{DARK_GREEN}}Else, just type {{WHITE}}/ccsetup multiworld <true/false>");
				} else if (args[0].equals("confirm")) {
					SetupWizard.setState(SetupWizard.CURRENCY_SETUP);
					Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}Step done! Type {{WHITE}}/ccsetup currency {{DARK_GREEN}}to continue!");
				} else {
					Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_RED}}Invalid value. Accepted value are {{WHITE}}true {{DARK_RED}}or {{WHITE}}false");
				}
			}
		} else {
			Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_RED}}Wrong setup status for this cmd. If you didin't start the setup yet, use /ccsetup");
		}
	}

	@Override
	public String help() {
		return "/ccsetup - Start the setup";
	}

	@Override
	public int maxArgs() {
		return 1;
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
