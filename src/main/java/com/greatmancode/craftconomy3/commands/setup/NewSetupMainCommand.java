/**
 * This file is part of Craftconomy3.
 *
 * Copyright (c) 2011-2016, Greatman <http://github.com/greatman/>
 * Copyright (c) 2017, Aztorius <http://github.com/Aztorius/>
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
import com.greatmancode.craftconomy3.commands.AbstractCommand;
import com.greatmancode.tools.commands.CommandSender;
import com.greatmancode.tools.commands.interfaces.CommandExecutor;

public class NewSetupMainCommand extends AbstractCommand {
    public NewSetupMainCommand(String name) {
        super(name);
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (NewSetupWizard.getState().equals(NewSetupWizard.BASIC_STEP)) {
            start(sender);
        }
    }

    @Override
    public String help() {
        return "/ccsetup - Start the setup wizard.";
    }

    @Override
    public int maxArgs() {
        return 0;
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

    private void start(CommandSender sender) {
        sendMessage(sender, "{{DARK_GREEN}}Welcome to the {{WHITE}}Craftconomy 3 {{DARK_GREEN}} setup wizard!");
        sendMessage(sender, "{{DARK_GREEN}}I will help you configure {{WHITE}}Craftconomy {{DARK_GREEN}}like you want!");
        sendMessage(sender, "{{DARK_GREEN}}First, I need to know what kind of database you want to use. If you want a {{WHITE}}flatfile {{DARK_GREEN}}database, I recommend {{WHITE}}H2.");
        sendMessage(sender, "{{DARK_GREEN}}Please type {{WHITE}}/ccsetup database <mysql/h2>");
        NewSetupWizard.setState(NewSetupWizard.DATABASE_STEP);
    }
}
