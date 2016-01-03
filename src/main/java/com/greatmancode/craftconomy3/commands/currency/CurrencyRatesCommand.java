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
import com.greatmancode.craftconomy3.currency.Currency;
import com.greatmancode.tools.commands.interfaces.CommandExecutor;

public class CurrencyRatesCommand extends CommandExecutor {
    @Override
    public void execute(String sender, String[] args) {
        Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, Common.getInstance().getLanguageManager().getString("rates_header"));
        for (CurrencyRateEntry entry : Common.getInstance().getStorageHandler().getStorageEngine().getCurrencyExchanges()) {
            Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, "1 " + entry.from.getName() + " => " + entry.amount + " " + entry.to.getName());
        }
    }

    @Override
    public String help() {
        return Common.getInstance().getLanguageManager().getString("currency_rates_cmd_help");
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
        return "craftconomy.rates";
    }

    public static class CurrencyRateEntry {
        private double amount;
        private Currency from, to;

        public CurrencyRateEntry(Currency from, Currency to, double amount) {
            this.amount = amount;
            this.from = from;
            this.to = to;
        }

        public double getAmount() {
            return amount;
        }

        public Currency getFrom() {
            return from;
        }

        public Currency getTo() {
            return to;
        }
    }
}
