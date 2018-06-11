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
import com.greatmancode.craftconomy3.account.Account;
import com.greatmancode.craftconomy3.account.Balance;
import com.greatmancode.tools.commands.CommandSender;
import com.greatmancode.tools.commands.PlayerCommandSender;
import com.greatmancode.tools.commands.interfaces.CommandExecutor;

import java.util.UUID;

public class BankBalanceCommand extends CommandExecutor {
    @Override
    public void execute(CommandSender sender, String[] args) {
        UUID senderUUID = null;
        if(sender instanceof PlayerCommandSender) {
            PlayerCommandSender player = (PlayerCommandSender) sender;
            senderUUID= ((PlayerCommandSender) sender).getUuid();
        }
            if (Common.getInstance().getAccountManager().exist(args[0], true)) {
                Account account = Common.getInstance().getAccountManager().getAccount(args[0], true);
                if (account.getAccountACL().canShow(sender.getName()) || Common.getInstance().getServerCaller()
                        .getPlayerCaller().checkPermission(senderUUID, "craftconomy.bank.balance.others")) {
                    Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(senderUUID, Common.getInstance()
                            .getLanguageManager().getString("bank_statement"));
                    if (account.getAllBalance().isEmpty()) {
                        Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(senderUUID, Common
                                .getInstance().getLanguageManager().parse("bank_account_empty", account.getAccountName()));
                    } else {
                        for (Balance bl : account.getAllBalance()) {
                            Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(senderUUID, Common.getInstance().format(bl.getWorld(), bl.getCurrency(), bl.getBalance()));
                        }
                    }
                } else {
                    Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(senderUUID, Common.getInstance().getLanguageManager().getString("cant_check_bank_statement"));
                }
            } else {
                Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(senderUUID, Common.getInstance().getLanguageManager().getString("account_not_exist"));
            }
    }

    @Override
    public String help() {
        return Common.getInstance().getLanguageManager().getString("bank_balance_cmd_help");
    }

    @Override
    public int maxArgs() {
        return 1;
    }

    @Override
    public int minArgs() {
        return 1;
    }

    @Override
    public boolean playerOnly() {
        return false;
    }

    @Override
    public String getPermissionNode() {
        return "craftconomy.bank.balance";
    }
}
