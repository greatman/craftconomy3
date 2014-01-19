/*
 * This file is part of Craftconomy3.
 *
 * Copyright (c) 2011-2013, Greatman <http://github.com/greatman/>
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

import com.alta189.simplesave.query.OrderQuery;
import com.alta189.simplesave.query.QueryResult;
import com.alta189.simplesave.query.SelectQuery;
import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.account.Account;
import com.greatmancode.craftconomy3.database.tables.LogTable;
import com.greatmancode.tools.commands.interfaces.CommandExecutor;

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
		SelectQuery<LogTable> logQuery = Common.getInstance().getDatabaseManager().getDatabase().select(LogTable.class);
		logQuery.where().equal("username_id", user.getAccountID());
		logQuery.order().getPairs().add(new OrderQuery.OrderPair("id", OrderQuery.Order.DESC));

		logQuery.limit().setLimit((page - 1) * NUMBER_ELEMENTS, NUMBER_ELEMENTS);
		QueryResult<LogTable> logResult = logQuery.execute();
		for (int i = 0; i < logResult.find().size(); i++) {
			LogTable r = logResult.find().get(i);

			// Is it better to do 50 query or to get ALL the username-id pairs?
			// I choose the first solution. This is done async and will save lot
			// of memory on large server with lots of players/account.
			//TODO: Language
			ret += "{{WHITE}}" + ((page - 1) * NUMBER_ELEMENTS + i + 1) + ": {{DARK_GREEN}}Time: {{WHITE}}" + r.getTimestamp() + " {{DARK_GREEN}}Type: {{WHITE}}" + r.getType() + " {{DARK_GREEN}} Amount: {{WHITE}}" + Common.getInstance().format(r.getWorldName(), Common.getInstance().getCurrencyManager().getCurrency(r.getCurrencyName()), r.getAmount()) + " {{DARK_GREEN}}Cause: {{WHITE}}" + r.getCause();
			if (r.getCauseReason() != null) {
				ret += " {{DARK_GREEN}}Reason: {{WHITE}}" + r.getCauseReason();
			}
			ret += "\n";
		}

		Common.getInstance().getServerCaller().getSchedulerCaller().delay(new TopCommandThreadEnd(sender, ret), 0, false);
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