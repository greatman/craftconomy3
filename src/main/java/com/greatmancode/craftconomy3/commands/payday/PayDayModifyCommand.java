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
package com.greatmancode.craftconomy3.commands.payday;

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.tools.commands.interfaces.CommandExecutor;
import com.greatmancode.tools.utils.Tools;

public class PayDayModifyCommand extends CommandExecutor {
    @Override
    public void execute(String sender, String[] args) {
        if (Common.getInstance().getPaydayManager().getPayDay(args[0]) != null) {
            if (args[1].equalsIgnoreCase("name")) {
                if (Common.getInstance().getPaydayManager().getPayDay(args[2]) == null) {
                    Common.getInstance().getPaydayManager().getPayDay(args[0]).setName(args[2]);
                    Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, Common.getInstance().getLanguageManager().getString("name_changed"));
                } else {
                    Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, Common.getInstance().getLanguageManager().getString("payday_with_name_already_exist"));
                }
            } else if (args[1].equalsIgnoreCase("status")) {
                if (args[2].equalsIgnoreCase("wage") || args[2].equalsIgnoreCase("tax")) {
                    if (args[2].equalsIgnoreCase("wage")) {
                        Common.getInstance().getPaydayManager().getPayDay(args[0]).setStatus(0);
                    } else {
                        Common.getInstance().getPaydayManager().getPayDay(args[0]).setStatus(1);
                    }
                    Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, Common.getInstance().getLanguageManager().getString("status_changed"));
                } else {
                    Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, Common.getInstance().getLanguageManager().getString("invalid_status"));
                }
            } else if (args[1].equalsIgnoreCase("disabled")) {
                if (args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("false")) {
                    Common.getInstance().getPaydayManager().getPayDay(args[0]).setDisabled(Boolean.parseBoolean(args[2]));
                    Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, Common.getInstance().getLanguageManager().getString("disabled_changed"));
                } else {
                    Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, Common.getInstance().getLanguageManager().getString("invalid_disabled"));
                }
            } else if (args[1].equalsIgnoreCase("interval")) {
                if (Tools.isInteger(args[2])) {
                    Common.getInstance().getPaydayManager().getPayDay(args[0]).setInterval(Integer.parseInt(args[2]));
                    Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, Common.getInstance().getLanguageManager().getString("interval_changed"));
                } else {
                    Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, Common.getInstance().getLanguageManager().getString("invalid_interval"));
                }
            } else if (args[1].equalsIgnoreCase("amount")) {
                if (Tools.isValidDouble(args[2])) {
                    Common.getInstance().getPaydayManager().getPayDay(args[0]).setValue(Double.parseDouble(args[2]));
                    Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, Common.getInstance().getLanguageManager().getString("amount_changed"));
                } else {
                    Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, Common.getInstance().getLanguageManager().getString("invalid_amount"));
                }
            } else if (args[1].equalsIgnoreCase("account")) {
                if (Common.getInstance().getAccountManager().exist(args[2])) {
                    Common.getInstance().getPaydayManager().getPayDay(args[0]).setAccount(args[2]);
                    Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, Common.getInstance().getLanguageManager().getString("account_changed"));
                } else {
                    Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, Common.getInstance().getLanguageManager().getString("account_not_exist"));
                }
            } else if (args[1].equalsIgnoreCase("currency")) {
                if (Common.getInstance().getCurrencyManager().getCurrency(args[2]) != null) {
                    Common.getInstance().getPaydayManager().getPayDay(args[0]).setCurrencyId(Common.getInstance().getCurrencyManager().getCurrency(args[2]).getDatabaseID());
                    Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, Common.getInstance().getLanguageManager().getString("currency_changed"));
                } else {
                    Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, Common.getInstance().getLanguageManager().getString("currency_not_exist"));
                }
            } else if (args[1].equalsIgnoreCase("world")) {
                if (Common.getInstance().getServerCaller().worldExist(args[2])) {
                    Common.getInstance().getPaydayManager().getPayDay(args[0]).setWorldName(args[2]);
                    Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, Common.getInstance().getLanguageManager().getString("world_changed"));
                } else {
                    Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, Common.getInstance().getLanguageManager().getString("world_not_exist"));
                }
            } else {
                Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, Common.getInstance().getLanguageManager().getString("invalid_edit_mode"));
            }
        } else {
            Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, Common.getInstance().getLanguageManager().getString("payday_not_found"));
        }
    }

    @Override
    public String help() {
        return Common.getInstance().getLanguageManager().getString("payday_modify_cmd_help");
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
        return "craftconomy.payday.command.modify";
    }
}
