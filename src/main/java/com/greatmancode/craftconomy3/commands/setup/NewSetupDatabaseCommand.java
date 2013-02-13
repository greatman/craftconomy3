/*
 * This file is part of Craftconomy3.
 *
 * Copyright (c) 2011-2013, Greatman <http://github.com/greatman/>
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

import java.util.HashMap;
import java.util.Map;

import com.alta189.simplesave.exceptions.ConnectionException;
import com.alta189.simplesave.exceptions.TableRegistrationException;
import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.NewSetupWizard;
import com.greatmancode.craftconomy3.commands.interfaces.CraftconomyCommand;
import com.greatmancode.craftconomy3.utils.Tools;

public class NewSetupDatabaseCommand extends CraftconomyCommand {
	private enum INTERNALSTEP {
		START,
		SQLITE,
		MYSQL,
		H2;
	}

	private static final Map<String, String> VALUES = new HashMap<String, String>();
	private static final String ERROR_MESSAGE = "{{DARK_RED}}A error occured. The error is: {{WHITE}}%s";
	private static final String CONFIG_NODE = "System.Database.Type";
	private INTERNALSTEP step = INTERNALSTEP.START;

	@Override
	public void execute(String sender, String[] args) {
		if (NewSetupWizard.getState().equals(NewSetupWizard.DATABASE_STEP)) {
			if (step.equals(INTERNALSTEP.START)) {
				start(sender, args);
			} else if (step.equals(INTERNALSTEP.MYSQL)) {
				mysql(sender, args);
			}
		}
	}

	@Override
	public String help() {
		return "/ccsetup database - Database step for setup wizard.";
	}

	@Override
	public int maxArgs() {
		return 3;
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

	private void start(String sender, String[] args) {
		if (args.length == 1) {
			if (args[0].equalsIgnoreCase("sqlite")) {
				step = INTERNALSTEP.SQLITE;
				sqliteOrH2(sender, true);
			} else if (args[0].equalsIgnoreCase("mysql")) {
				step = INTERNALSTEP.MYSQL;
				Common.getInstance().getConfigurationManager().getConfig().setValue(CONFIG_NODE, "mysql");
				Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}You selected {{WHITE}}MySQL{{DARK_GREEN}}. Please type {{WHITE}}/ccsetup database address <Your host>");
			} else if (args[0].equalsIgnoreCase("h2")) {
				step = INTERNALSTEP.H2;
				sqliteOrH2(sender, false);
			} else {
				Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_RED}}Invalid value!");
				Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}Please type {{WHITE}}/ccsetup database <sqlite/mysql/h2>");
			}
		}
	}

	private void sqliteOrH2(String sender, boolean sqlite) {
		if (sqlite) {
			Common.getInstance().getConfigurationManager().getConfig().setValue(CONFIG_NODE, "sqlite");
		} else {
			Common.getInstance().getConfigurationManager().getConfig().setValue(CONFIG_NODE, "h2");
		}
		try {
			Common.getInstance().initialiseDatabase();
			done(sender);
		} catch (TableRegistrationException e) {
			Common.getInstance().getServerCaller().sendMessage(sender, String.format(ERROR_MESSAGE, e.getMessage()));
		} catch (ConnectionException e) {
			Common.getInstance().getServerCaller().sendMessage(sender, String.format(ERROR_MESSAGE, e.getMessage()));
		}
	}

	private void mysql(String sender, String[] args) {
		if (args.length == 2) {
			if (args[0].equalsIgnoreCase("address")) {
				VALUES.put("address", args[1]);
				Common.getInstance().getConfigurationManager().getConfig().setValue("System.Database.Address", args[1]);
				Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}Alright! Please type {{WHITE}}/ccsetup database port <Your port> {{DARK_GREEN}}to set your MySQL port (Usually 3306)");
			} else if (args[0].equalsIgnoreCase("port")) {
				if (Tools.isInteger(args[1])) {
					int port = Integer.parseInt(args[1]);
					VALUES.put("port", args[1]);
					Common.getInstance().getConfigurationManager().getConfig().setValue("System.Database.Port", port);
					Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}Saved! Please type {{WHITE}}/ccsetup database username <Username> {{DARK_GREEN}}to set your MySQL username");
				} else {
					Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_RED}}Invalid port!");
				}
			} else if (args[0].equalsIgnoreCase("username")) {
				VALUES.put("username", args[1]);
				Common.getInstance().getConfigurationManager().getConfig().setValue("System.Database.Username", args[1]);
				Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}Saved! Please type {{WHITE}}/ccsetup database password <Password> {{DARK_GREEN}}to set your MySQL password (enter \"\" for none)");
			} else if (args[0].equalsIgnoreCase("password")) {
				VALUES.put("password", args[1]);
				Common.getInstance().getConfigurationManager().getConfig().setValue("System.Database.Password", args[1]);
				Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}Last step for database! Please type {{WHITE}}/ccsetup database db <Database Name> {{DARK_GREEN}}to set your MySQL database.");
			} else if (args[0].equalsIgnoreCase("db")) {
				VALUES.put("db", args[1]);
				Common.getInstance().getConfigurationManager().getConfig().setValue("System.Database.Db", args[1]);
				Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}Done! Please wait while the database is initializing.");
			}
		}

		if (VALUES.size() == 5) {
			try {
				Common.getInstance().initialiseDatabase();
				done(sender);
			} catch (TableRegistrationException e) {
				Common.getInstance().getServerCaller().sendMessage(sender, String.format(ERROR_MESSAGE, e.getMessage()));
				Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}Be sure that you entered valid information! Commands are: {{WHITE}}/ccsetup database <address/port/username/password/db> <Value>");
			} catch (ConnectionException e) {
				Common.getInstance().getServerCaller().sendMessage(sender, String.format(ERROR_MESSAGE, e.getMessage()));
				Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}Be sure that you entered valid information! Commands are: {{WHITE}}/ccsetup database <address/port/username/password/db> <Value>");
			}
		}
	}

	private void done(String sender) {
		Common.getInstance().initializeCurrency();
		Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}Awesome! Now, I want to know if you want to convert from Craftconomy V2?");
		Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}Please type {{WHITE}}/ccsetup currency cc2 yes {{DARK_GREEN}}or {{WHITE}}/ccsetup currency cc2 no");
		NewSetupWizard.setState(NewSetupWizard.CURRENCY_STEP);
	}
}
