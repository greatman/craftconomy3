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
package com.greatmancode.craftconomy3.commands.money;

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.account.Account;
import com.greatmancode.craftconomy3.database.tables.LogTable;
import com.greatmancode.tools.commands.interfaces.CommandExecutor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

class LogCommandThread implements Runnable {
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
    private final Account user;

    public LogCommandThread(String sender, int page, Account user) {
        this.sender = sender;
        this.page = page;
        this.user = user;
    }

    @Override
    public void run() {
        String ret = Common.getInstance().getLanguageManager().parse("money_log_header", page, user.getAccountName()) + "\n";
        try {
            Connection connection = Common.getInstance().getDatabaseManager().getDatabase().getConnection();
            PreparedStatement statement = connection.prepareStatement(LogTable.SELECT_ENTRY_LIMIT);
            statement.setString(1, user.getAccountName());
            statement.setInt(2, (page - 1) * NUMBER_ELEMENTS);
            statement.setInt(3, NUMBER_ELEMENTS);
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                ret += "{{DARK_GREEN}}Time: {{WHITE}}" + set.getTimestamp("timestamp") + " {{DARK_GREEN}}Type: {{WHITE}}" + set.getString("type") + " {{DARK_GREEN}} Amount: {{WHITE}}" + Common.getInstance().format(set.getString("worldName"), Common.getInstance().getCurrencyManager().getCurrency(set.getInt("currency_id")), set.getDouble("amount")) + " {{DARK_GREEN}}Cause: {{WHITE}}" + set.getString("cause");
                if (set.getString("causeReason") != null) {
                    ret += " {{DARK_GREEN}}Reason: {{WHITE}}" + set.getString("causeReason");
                }
                ret += "\n";
            }
            Common.getInstance().getServerCaller().getSchedulerCaller().delay(new TopCommandThreadEnd(sender, ret), 0, false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
        Account user = Common.getInstance().getAccountManager().getAccount(sender);
        if (args.length == 2 && Common.getInstance().getServerCaller().getPlayerCaller().checkPermission(sender, "craftconomy.money.log.others")) {
            if (Common.getInstance().getAccountManager().exist(args[1])) {
                user = Common.getInstance().getAccountManager().getAccount(args[1]);
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
}