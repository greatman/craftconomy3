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

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.currency.Currency;
import com.greatmancode.craftconomy3.groups.WorldGroupsManager;
import com.greatmancode.tools.commands.interfaces.CommandExecutor;

import java.util.List;

class TopCommandThread implements Runnable {
    public static final int NUMBER_ELEMENTS = 10;

    class TopCommandThreadEnd implements Runnable {
        private final String sender;
        private final String ret;

        public TopCommandThreadEnd(String sender, String ret) {
            this.sender = sender;
            this.ret = ret;
        }

        @Override
        public void run() {
            Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, ret);
        }
    }

    private final String sender;
    private final int page;
    private final String world;
    private final Currency currency;

    public TopCommandThread(String sender, int page, String world, Currency currency) {
        this.sender = sender;
        this.page = page;
        this.world = world;
        this.currency = currency;
    }

    @Override
    public void run() {
        String ret = Common.getInstance().getLanguageManager().parse("money_top_header", page, world) + "\n";
        List<TopCommand.TopEntry> list = Common.getInstance().getStorageHandler().getStorageEngine().getTopEntry(page, currency, world);
        for (int i = 0; i < list.size(); i++) {
            TopCommand.TopEntry entry = list.get(i);
            ret += "" + ((page - 1) * NUMBER_ELEMENTS + i + 1) + ": {{DARK_GREEN}}" + entry.username + " {{WHITE}}" + Common.getInstance().format(null, currency, entry.balance) + "\n";
        }

        Common.getInstance().getServerCaller().getSchedulerCaller().delay(new TopCommandThreadEnd(sender, ret), 0, false);
    }
}

public class TopCommand extends CommandExecutor {
    @Override
    public void execute(String sender, String[] args) {
        int page = 1;
        Currency currency;
        if (args.length == 0) {
            currency = Common.getInstance().getCurrencyManager().getDefaultCurrency();
        } else {
            if (Common.getInstance().getCurrencyManager().getCurrency(args[0]) != null) {
                currency = Common.getInstance().getCurrencyManager().getCurrency(args[0]);
            } else {
                Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, Common.getInstance().getLanguageManager().getString("currency_not_exist"));
                return;
            }
        }

        if (args.length > 1) {
            try {
                page = Integer.parseInt(args[1]);
                if (page < 1) {
                    page = 1;
                }
            } catch (NumberFormatException e) {
                Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, Common.getInstance().getLanguageManager().getString("invalid_page"));
                return;
            }
        }

        String world = WorldGroupsManager.DEFAULT_GROUP_NAME;
        if (args.length > 2) {
            world = args[2];
        }

        Common.getInstance().getServerCaller().getSchedulerCaller().delay(new TopCommandThread(sender, page, world, currency), 0, false);
    }

    @Override
    public String help() {
        return Common.getInstance().getLanguageManager().getString("money_top_cmd_help");
    }

    @Override
    public int maxArgs() {
        return 3;
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
        return "craftconomy.money.top";
    }

    public static class TopEntry {
        public String username;
        public Currency currency;
        public double balance;

        public TopEntry(String username, Currency currency, double balance) {
            this.username = username;
            this.currency = currency;
            this.balance = balance;
        }
    }
}