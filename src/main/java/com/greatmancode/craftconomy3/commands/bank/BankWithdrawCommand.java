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

import com.greatmancode.craftconomy3.Cause;
import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.account.Account;
import com.greatmancode.craftconomy3.currency.Currency;
import com.greatmancode.tools.commands.interfaces.CommandExecutor;
import com.greatmancode.tools.utils.Tools;

public class BankWithdrawCommand extends CommandExecutor {
    @Override
    public void execute(String sender, String[] args) {
        if (Common.getInstance().getAccountManager().exist(args[0], true)) {
            Account bankAccount = Common.getInstance().getAccountManager().getAccount(args[0], true);
            if (bankAccount.getAccountACL().canWithdraw(sender) || Common.getInstance().getServerCaller().getPlayerCaller().checkPermission(sender, "craftconomy.bank.withdraw.others")) {
                if (Tools.isValidDouble(args[1])) {
                    double amount = Double.parseDouble(args[1]);
                    Currency currency = Common.getInstance().getCurrencyManager().getDefaultCurrency();
                    if (args.length > 2) {
                        if (Common.getInstance().getCurrencyManager().getCurrency(args[2]) != null) {
                            currency = Common.getInstance().getCurrencyManager().getCurrency(args[2]);
                        } else {
                            Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, Common.getInstance().getLanguageManager().getString("currency_not_exist"));
                            return;
                        }
                    }
                    Account playerAccount = Common.getInstance().getAccountManager().getAccount(sender, false);
                    if (bankAccount.hasEnough(amount, Account.getWorldGroupOfPlayerCurrentlyIn(sender), currency.getName())) {
                        bankAccount.withdraw(amount, Account.getWorldGroupOfPlayerCurrentlyIn(sender), currency.getName(), Cause.BANK_WITHDRAW, sender);
                        playerAccount.deposit(amount, Account.getWorldGroupOfPlayerCurrentlyIn(sender), currency.getName(), Cause.BANK_WITHDRAW, bankAccount.getAccountName());
                        //TODO: Language
                        Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, "{{DARK_GREEN}}Withdrawed {{WHITE}}" + Common.getInstance().format(null, currency, amount) + "{{DARK_GREEN}} from the {{WHITE}}" + args[0] + "{{DARK_GREEN}} bank Account.");
                    } else {
                        Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, Common.getInstance().getLanguageManager().getString("bank_not_enough_money"));
                    }
                } else {
                    Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, Common.getInstance().getLanguageManager().getString("invalid_amount"));
                }
            } else {
                Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, Common.getInstance().getLanguageManager().getString("cant_withdraw_bank"));
            }
        } else {
            Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, Common.getInstance().getLanguageManager().getString("account_not_exist"));
        }
    }

    @Override
    public String help() {
        return Common.getInstance().getLanguageManager().getString("bank_withdraw_cmd_help");
    }

    @Override
    public int maxArgs() {
        return 3;
    }

    @Override
    public int minArgs() {
        return 2;
    }

    @Override
    public boolean playerOnly() {
        return true;
    }

    @Override
    public String getPermissionNode() {
        return "craftconomy.bank.withdraw";
    }
}
