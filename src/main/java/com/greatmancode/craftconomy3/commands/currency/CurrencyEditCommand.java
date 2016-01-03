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

public class CurrencyEditCommand extends CommandExecutor {
    @Override
    public void execute(String sender, String[] args) {
        if (Common.getInstance().getCurrencyManager().getCurrency(args[1]) != null) {
            if (!"".equals(args[2])) {
                if ("name".equalsIgnoreCase(args[0])) {
                    Common.getInstance().getCurrencyManager().getCurrency(args[1]).setName(args[2]);
                } else if ("nameplural".equalsIgnoreCase(args[0])) {
                    Common.getInstance().getCurrencyManager().getCurrency(args[1]).setPlural(args[2]);
                } else if ("minor".equalsIgnoreCase(args[0])) {
                    Common.getInstance().getCurrencyManager().getCurrency(args[1]).setMinor(args[2]);
                } else if ("minorplural".equalsIgnoreCase(args[0])) {
                    Common.getInstance().getCurrencyManager().getCurrency(args[1]).setMinorPlural(args[2]);
                } else if ("sign".equalsIgnoreCase(args[0])) {
                    Common.getInstance().getCurrencyManager().getCurrency(args[1]).setSign(args[2]);
                } else {
                    Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, Common.getInstance().getLanguageManager().getString("invalid_type"));
                    return;
                }
                Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, Common.getInstance().getLanguageManager().getString("currency_modified"));
            } else {
                Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, Common.getInstance().getLanguageManager().getString("currency_empty_value"));
            }
        } else {
            Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, Common.getInstance().getLanguageManager().getString("currency_not_exist"));
        }
    }

    @Override
    public String help() {
        return Common.getInstance().getLanguageManager().getString("currency_edit_cmd_help");
    }

    @Override
    public int maxArgs() {
        return 3;
    }

    @Override
    public int minArgs() {
        return 3;
    }

    @Override
    public boolean playerOnly() {
        return false;
    }

    @Override
    public String getPermissionNode() {
        return "craftconomy.currency.edit";
    }
}
