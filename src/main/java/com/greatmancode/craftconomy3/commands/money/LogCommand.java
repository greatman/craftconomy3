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
import com.greatmancode.craftconomy3.account.Account;
import com.greatmancode.craftconomy3.currency.Currency;
import com.greatmancode.tools.commands.interfaces.CommandExecutor;

import java.sql.Timestamp;

class LogCommandThread implements Runnable {

    class LogCommandThreadEnd implements Runnable {
        private final String sender;
        private final String ret;

        public LogCommandThreadEnd(String sender, String ret) {
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
    private final Account user;

    public LogCommandThread(String sender, int page, Account user) {
        this.sender = sender;
        this.page = page;
        this.user = user;
    }

    @Override
    public void run() {
        String ret = Common.getInstance().getLanguageManager().parse("money_log_header", page, user.getAccountName()) + "\n";
        for (LogCommand.LogEntry entry : Common.getInstance().getStorageHandler().getStorageEngine().getLog(user, page)) {
            ret += "{{DARK_GREEN}}Time: {{WHITE}}" + entry.timestamp + " {{DARK_GREEN}}Type: {{WHITE}}" + entry.type + " {{DARK_GREEN}} Amount: {{WHITE}}" + Common.getInstance().format(entry.worldName, entry.currency, entry.amount) + " {{DARK_GREEN}}Cause: {{WHITE}}" + entry.cause;
            if (entry.causeReason != null) {
                ret += " {{DARK_GREEN}}Reason: {{WHITE}}" + entry.causeReason;
            }
            ret += "\n";
        }
        Common.getInstance().getServerCaller().getSchedulerCaller().delay(new LogCommandThreadEnd(sender, ret), 0, false);
    }
}

public class LogCommand extends CommandExecutor {
    @Override
    public void execute(String sender, String[] args) {
        int page = 1;
        if (args.length >= 1) {
            try {
                page = Integer.parseInt(args[0]);
                if (page < 1) {
                    page = 1;
                }
            } catch (NumberFormatException e) {
                Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, Common.getInstance().getLanguageManager().getString("invalid_page"));
                return;
            }
        }
        Account user = Common.getInstance().getAccountManager().getAccount(sender, false);
        if (args.length == 2 && Common.getInstance().getServerCaller().getPlayerCaller().checkPermission(sender, "craftconomy.money.log.others")) {
            if (Common.getInstance().getAccountManager().exist(args[1], false)) {
                user = Common.getInstance().getAccountManager().getAccount(args[1], false);
            }
        }
        Common.getInstance().getServerCaller().getSchedulerCaller().delay(new LogCommandThread(sender, page, user), 0, false);
    }

    @Override
    public String help() {
        return Common.getInstance().getLanguageManager().getString("money_log_cmd_help");
    }

    @Override
    public int maxArgs() {
        return 2;
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
        return "craftconomy.money.log";
    }

    public static class LogEntry {
        public Timestamp timestamp;
        public String type, worldName, cause, causeReason;
        public Currency currency;
        public double amount;

        public LogEntry(Timestamp timestamp, String type, String worldName, String cause, String causeReason, Currency currency, double amount) {
            this.timestamp = timestamp;
            this.type = type;
            this.worldName = worldName;
            this.cause = cause;
            this.causeReason = causeReason;
            this.currency = currency;
            this.amount = amount;
        }
        
    }
}
