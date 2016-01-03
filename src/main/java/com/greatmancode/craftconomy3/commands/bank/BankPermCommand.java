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
package com.greatmancode.craftconomy3.commands.bank;

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.account.Account;
import com.greatmancode.tools.commands.interfaces.CommandExecutor;

public class BankPermCommand extends CommandExecutor {
    @Override
    public void execute(String sender, String[] args) {
        if (Common.getInstance().getAccountManager().exist(args[0], true)) {
            Account account = Common.getInstance().getAccountManager().getAccount(args[0], true);
            if (account.getAccountACL().canAcl(sender) || account.getAccountACL().isOwner(sender) || Common.getInstance().getServerCaller().getPlayerCaller().checkPermission(sender, "craftconomy.bank.perm.others")) {

                if ("deposit".equalsIgnoreCase(args[1])) {
                    account.getAccountACL().setDeposit(args[2], Boolean.parseBoolean(args[3]));
                } else if ("withdraw".equalsIgnoreCase(args[1])) {
                    account.getAccountACL().setWithdraw(args[2], Boolean.parseBoolean(args[3]));
                } else if ("acl".equalsIgnoreCase(args[1])) {
                    account.getAccountACL().setAcl(args[2], Boolean.parseBoolean(args[3]));
                } else if ("show".equalsIgnoreCase(args[1])) {
                    account.getAccountACL().setShow(args[2], Boolean.parseBoolean(args[3]));
                } else {
                    Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, Common.getInstance().getLanguageManager().getString("invalid_flag"));
                    return;
                }
                Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, Common.getInstance().getLanguageManager().parse("bank_flag_set", args[1], args[2], args[3]));
            } else {
                Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, Common.getInstance().getLanguageManager().getString("cant_modify_acl"));
            }
        } else {
            Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, Common.getInstance().getLanguageManager().getString("account_not_exist!"));
        }
    }

    @Override
    public String help() {
        return Common.getInstance().getLanguageManager().getString("bank_perm_cmd_help");
    }

    @Override
    public int maxArgs() {
        return 4;
    }

    @Override
    public int minArgs() {
        return 4;
    }

    @Override
    public boolean playerOnly() {
        return false;
    }

    @Override
    public String getPermissionNode() {
        return "craftconomy.bank.perm";
    }
}
