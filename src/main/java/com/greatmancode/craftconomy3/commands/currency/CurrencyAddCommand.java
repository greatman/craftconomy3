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
package com.greatmancode.craftconomy3.commands.currency;

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.tools.commands.interfaces.CommandExecutor;

public class CurrencyAddCommand extends CommandExecutor {
    @Override
    public void execute(String sender, String[] args) {
        if (args[0] != null && args[1] != null && args[2] != null && args[3] != null && args[4] != null) {
            if (Common.getInstance().getCurrencyManager().getCurrency(args[0]) == null) {
                Common.getInstance().getCurrencyManager().addCurrency(args[0], args[1], args[2], args[3], args[4], true);
                Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, Common.getInstance().getLanguageManager().getString("currency_added"));
            } else {
                Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, Common.getInstance().getLanguageManager().getString("currency_already_exists"));
            }
        }
    }

    @Override
    public String help() {
        return Common.getInstance().getLanguageManager().getString("currency_add_cmd_help");
    }

    @Override
    public int maxArgs() {
        return 5;
    }

    @Override
    public int minArgs() {
        return 5;
    }

    @Override
    public boolean playerOnly() {
        return false;
    }

    @Override
    public String getPermissionNode() {
        return "craftconomy.currency.add";
    }
}
