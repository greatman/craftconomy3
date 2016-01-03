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
package com.greatmancode.craftconomy3.commands.money;

import com.greatmancode.craftconomy3.Cause;
import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.account.Account;
import com.greatmancode.craftconomy3.currency.Currency;
import com.greatmancode.craftconomy3.utils.NoExchangeRate;
import com.greatmancode.tools.commands.interfaces.CommandExecutor;
import com.greatmancode.tools.utils.Tools;

public class ExchangeCommand extends CommandExecutor {
    @Override
    public void execute(String sender, String[] args) {
        if (Tools.isValidDouble(args[2])) {
            double amount = Double.parseDouble(args[2]);
            Currency currency1 = Common.getInstance().getCurrencyManager().getCurrency(args[0]);
            Currency currency2 = Common.getInstance().getCurrencyManager().getCurrency(args[1]);
            if (currency1 != null && currency2 != null) {
                try {
                    double exchangeRate = currency1.getExchangeRate(currency2);
                    Account account = Common.getInstance().getAccountManager().getAccount(sender, false);
                    if (account.hasEnough(amount, Common.getInstance().getServerCaller().getPlayerCaller().getPlayerWorld(sender), currency1.getName())) {
                        double value = amount * exchangeRate;
                        account.withdraw(amount, Common.getInstance().getServerCaller().getPlayerCaller().getPlayerWorld(sender), currency1.getName(), Cause.EXCHANGE, currency2.getName());
                        account.deposit(value, Common.getInstance().getServerCaller().getPlayerCaller().getPlayerWorld(sender), currency2.getName(), Cause.EXCHANGE, currency1.getName());
                        Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, Common.getInstance().getLanguageManager().parse("exchange_done", amount, currency1.getName(), value, currency2.getName()));
                    }
                } catch (NoExchangeRate noExchangeRate) {
                    Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, Common.getInstance().getLanguageManager().parse("no_exchange_rate", currency1.getName(), currency2.getName()));
                }
            } else {
                Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, Common.getInstance().getLanguageManager().getString("currency_not_exist"));
            }
        } else {
            Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, Common.getInstance().getLanguageManager().getString("invalid_amount"));
        }
    }

    @Override
    public String help() {
        return Common.getInstance().getLanguageManager().getString("money_exchange_cmd_help");
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
        return true;
    }

    @Override
    public String getPermissionNode() {
        return "craftconomy.money.exchange";
    }
}
