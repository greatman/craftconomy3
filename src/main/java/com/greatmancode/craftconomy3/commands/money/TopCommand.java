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
import com.greatmancode.craftconomy3.currency.Currency;
import com.greatmancode.craftconomy3.database.tables.AccountTable;
import com.greatmancode.craftconomy3.database.tables.BalanceTable;
import com.greatmancode.craftconomy3.groups.WorldGroupsManager;
import com.greatmancode.tools.commands.interfaces.CommandExecutor;

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
			Common.getInstance().getServerCaller().sendMessage(sender, ret);
		}
	}

	private final String sender;
	private final int page;
	private final String world;
	private final int currency;

	public TopCommandThread(String sender, int page, String world, int currency) {
		this.sender = sender;
		this.page = page;
		this.world = world;
		this.currency = currency;
	}

	@Override
	public void run() {
		String ret = Common.getInstance().getLanguageManager().parse("money_top_header", page, world) + "\n";
		SelectQuery<BalanceTable> balanceQuery = Common.getInstance().getDatabaseManager().getDatabase().select(BalanceTable.class);
		balanceQuery.where().equal("worldName", world).and().equal("currency_id", currency);
		balanceQuery.order().getPairs().add(new OrderQuery.OrderPair("balance", OrderQuery.Order.DESC));
		balanceQuery.limit().setLimit((page - 1) * NUMBER_ELEMENTS, NUMBER_ELEMENTS);
		QueryResult<BalanceTable> balanceResult = balanceQuery.execute();
		for (int i = 0; i < balanceResult.find().size(); i++) {
			BalanceTable r = balanceResult.find().get(i);

			// Is it better to do 50 query or to get ALL the username-id pairs?
			// I choose the first solution. This is done async and will save lot
			// of memory on large server with lots of players/account.

			AccountTable usernameResult = Common.getInstance().getDatabaseManager().getDatabase().select(AccountTable.class).where().equal("id", r.getUsernameId()).execute().findOne();
			String username = "ERROR";
			if (usernameResult != null) {
				username = usernameResult.getName();
			}
			ret += "" + ((page - 1) * NUMBER_ELEMENTS + i + 1) + ": {{DARK_GREEN}}" + username + " {{WHITE}}" + Common.getInstance().format(null, Common.getInstance().getCurrencyManager().getCurrency(currency), r.getBalance()) + "\n";
		}

		Common.getInstance().getServerCaller().delay(new TopCommandThreadEnd(sender, ret), 0, false);
	}
}

public class TopCommand extends CommandExecutor {
	@Override
	public void execute(String sender, String[] args) {
		int page = 1;
		Currency currency = Common.getInstance().getCurrencyManager().getDefaultCurrency();
		if (args.length == 0) {
			currency = Common.getInstance().getCurrencyManager().getDefaultCurrency();
		} else {
			if (Common.getInstance().getCurrencyManager().getCurrency(args[0]) != null) {
				currency = Common.getInstance().getCurrencyManager().getCurrency(args[0]);
			} else {
				Common.getInstance().getServerCaller().sendMessage(sender, Common.getInstance().getLanguageManager().getString("currency_not_exist"));
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
				Common.getInstance().getServerCaller().sendMessage(sender, Common.getInstance().getLanguageManager().getString("invalid_page"));
				return;
			}
		}

		String world = WorldGroupsManager.DEFAULT_GROUP_NAME;
		if (args.length > 2) {
			world = args[2];
		}

		Common.getInstance().getServerCaller().delay(new TopCommandThread(sender, page, world, currency.getDatabaseID()), 0, false);
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
}