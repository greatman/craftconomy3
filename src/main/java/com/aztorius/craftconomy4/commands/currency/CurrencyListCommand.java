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
package com.aztorius.craftconomy4.commands.currency;

import com.aztorius.craftconomy4.Common;
import com.greatmancode.tools.commands.interfaces.CommandExecutor;

import java.util.Arrays;

public class CurrencyListCommand extends CommandExecutor {

    @Override
    public void execute(String sender, String[] args) {
        Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, Common.getInstance().getLanguageManager().parse("currency_list_title"));
        Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, Arrays.toString(Common.getInstance().getCurrencyManager().getCurrencyNames().toArray()));
    }

    @Override
    public String help() {
        return Common.getInstance().getLanguageManager().getString("currency_list_cmd_help");
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
        return "craftconomy.currency.list";
    }
}
