/**
 * This file is part of Craftconomy3.
 *
 * Copyright (c) 2011-2016, Greatman <http://github.com/greatman/>
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
import com.greatmancode.craftconomy3.NewSetupWizard;
import com.greatmancode.tools.commands.interfaces.CommandExecutor;
import com.greatmancode.tools.utils.Tools;

import java.util.HashMap;
import java.util.Map;

public class NewSetupDatabaseCommand extends CommandExecutor {
    private enum INTERNALSTEP {
        START,
        SQLITE,
        MYSQL,
        H2;
    }

    private static final Map<String, String> VALUES = new HashMap<>();
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
            if ("mysql".equalsIgnoreCase(args[0])) {
                step = INTERNALSTEP.MYSQL;
                Common.getInstance().getMainConfig().setValue(CONFIG_NODE, "mysql");
                Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, "{{DARK_GREEN}}You selected {{WHITE}}MySQL{{DARK_GREEN}}. Please type {{WHITE}}/ccsetup database address <Your host>");
            } else if ("h2".equalsIgnoreCase(args[0])) {
                step = INTERNALSTEP.H2;
                h2(sender);
            } else {
                Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, "{{DARK_RED}}Invalid value!");
                Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, "{{DARK_GREEN}}Please type {{WHITE}}/ccsetup database <mysql/h2>");
            }
        }
    }

    private void h2(String sender) {
        Common.getInstance().getMainConfig().setValue(CONFIG_NODE, "h2");
        Common.getInstance().initialiseDatabase();
        done(sender);
    }

    private void mysql(String sender, String[] args) {
        if (args.length == 2) {
            if ("address".equalsIgnoreCase(args[0])) {
                VALUES.put("address", args[1]);
                Common.getInstance().getMainConfig().setValue("System.Database.Address", args[1]);
                Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, "{{DARK_GREEN}}Alright! Please type {{WHITE}}/ccsetup database port <Your port> {{DARK_GREEN}}to set your MySQL port (Usually 3306)");
            } else if ("port".equalsIgnoreCase(args[0])) {
                if (Tools.isInteger(args[1])) {
                    int port = Integer.parseInt(args[1]);
                    VALUES.put("port", args[1]);
                    Common.getInstance().getMainConfig().setValue("System.Database.Port", port);
                    Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, "{{DARK_GREEN}}Saved! Please type {{WHITE}}/ccsetup database username <Username> {{DARK_GREEN}}to set your MySQL username");
                } else {
                    Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, "{{DARK_RED}}Invalid port!");
                }
            } else if ("username".equalsIgnoreCase(args[0])) {
                VALUES.put("username", args[1]);
                Common.getInstance().getMainConfig().setValue("System.Database.Username", args[1]);
                Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, "{{DARK_GREEN}}Saved! Please type {{WHITE}}/ccsetup database password <Password> {{DARK_GREEN}}to set your MySQL password (enter \"\" for none)");
            } else if ("password".equalsIgnoreCase(args[0])) {
                if (args[1].equals("''") || args[1].equals("\"\"")) {
                    VALUES.put("password", "");
                    Common.getInstance().getMainConfig().setValue("System.Database.Password", "");
                } else {
                    VALUES.put("password", args[1]);
                    Common.getInstance().getMainConfig().setValue("System.Database.Password", args[1]);
                }
                Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, "{{DARK_GREEN}}Saved! Please type {{WHITE}}/ccsetup database db <Database Name> {{DARK_GREEN}}to set your MySQL database.");
            } else if ("db".equalsIgnoreCase(args[0])) {
                VALUES.put("db", args[1]);
                Common.getInstance().getMainConfig().setValue("System.Database.Db", args[1]);
                Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, "{{DARK_GREEN}}Saved! Please type {{WHITE}}/ccsetup database prefix <Prefix> {{DARK_GREEN}}to set your table prefix (If not sure, put cc3_).");
            } else if ("prefix".equalsIgnoreCase(args[0])) {
                VALUES.put("prefix", args[1]);
                Common.getInstance().getMainConfig().setValue("System.Database.Prefix", args[1]);
                Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, "{{DARK_GREEN}}Done! Please wait while the database is initializing.");
            }
        }

        if (VALUES.size() == 6) {
            Common.getInstance().initialiseDatabase();
            done(sender);
            //TODO: A catch
        }
    }

    private void done(String sender) {
        Common.getInstance().initializeCurrency();
        Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, "{{DARK_GREEN}}Alright! Welcome to Craftconomy! We use a Multi-Currency system. I need you to write the settings for the default currency.");
        Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, "{{DARK_GREEN}}First, let's configure the {{WHITE}}main currency name {{DARK_GREEN}}(Ex: {{WHITE}}Dollar{{DARK_GREEN}}). Type {{WHITE}}/ccsetup currency name <Name>");
        NewSetupWizard.setState(NewSetupWizard.CURRENCY_STEP);
    }
}
