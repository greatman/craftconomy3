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
package com.greatmancode.craftconomy3.commands.bank;

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.commands.AbstractCommand;
import com.greatmancode.tools.commands.CommandSender;
import com.greatmancode.tools.commands.interfaces.CommandExecutor;

import java.util.Arrays;

public class BankListCommand extends AbstractCommand {
    public BankListCommand(String name) {
        super(name);
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        sendMessage(sender, Common.getInstance()
                .getLanguageManager().parse("bank_account_list", Arrays.toString(Common.getInstance()
                        .getStorageHandler().getStorageEngine().getBankAccountList(sender.getName()))));
    }

    @Override
    public String help() {
        return Common.getInstance().getLanguageManager().getString("bank_list_cmd_help");
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
        return true;
    }

    @Override
    public String getPermissionNode() {
        return "craftconomy.bank.list";
    }
}
