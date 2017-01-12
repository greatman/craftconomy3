/**
 * This file is part of Craftconomy4.
 *
 * Copyright (c) 2011-2016, Greatman <http://github.com/greatman/>
 * Copyright (c) 2017, Aztorius <http://github.com/Aztorius/>
 *
 * Craftconomy4 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Craftconomy4 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Craftconomy4.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aztorius.craftconomy4.commands.money;

import com.aztorius.craftconomy4.Common;
import com.aztorius.craftconomy4.account.Account;
import com.aztorius.craftconomy4.account.Balance;
import com.greatmancode.tools.commands.interfaces.CommandExecutor;

public class MainCommand extends CommandExecutor {
    @Override
    public void execute(String sender, String[] args) {
        Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, Common.getInstance().getLanguageManager().getString("money_all_title"));
        Account account = Common.getInstance().getAccountManager().getAccount(sender, false);
        for (Balance bl : account.getAllWorldBalance(Account.getWorldGroupOfPlayerCurrentlyIn(sender))) {
            Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, Common.getInstance().format(bl.getWorld(), bl.getCurrency(), bl.getBalance()));
        }
    }

    @Override
    public String help() {
        return Common.getInstance().getLanguageManager().getString("money_main_cmd_help");
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
        return "craftconomy.money.balance";
    }
}
