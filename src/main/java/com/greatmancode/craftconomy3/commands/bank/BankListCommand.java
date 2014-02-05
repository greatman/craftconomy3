/*
 * This file is part of Craftconomy3.
 *
 * Copyright (c) 2011-2014, Greatman <http://github.com/greatman/>
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
import com.greatmancode.craftconomy3.database.tables.AccessTable;
import com.greatmancode.craftconomy3.database.tables.AccountTable;
import com.greatmancode.tools.commands.interfaces.CommandExecutor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BankListCommand extends CommandExecutor {
    @Override
    public void execute(String sender, String[] args) {
        List<AccessTable> accessTableList = Common.getInstance().getDatabaseManager().getDatabase().select(AccessTable.class).where().equal("playerName", sender).execute().find();
        List<AccountTable> accountTableList = new ArrayList<AccountTable>();
        for (AccessTable accessEntry : accessTableList) {
            accountTableList.add(Common.getInstance().getDatabaseManager().getDatabase().select(AccountTable.class).where().equal("id", accessEntry.getAccountId()).execute().findOne());
        }

        Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, Common.getInstance().getLanguageManager().parse("bank_account_list", Arrays.toString(accountTableList.toArray())));
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
