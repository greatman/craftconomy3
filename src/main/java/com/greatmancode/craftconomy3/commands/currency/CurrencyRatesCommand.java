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
package com.greatmancode.craftconomy3.commands.currency;

import java.util.List;

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.currency.Currency;
import com.greatmancode.craftconomy3.database.tables.ExchangeTable;
import com.greatmancode.tools.commands.interfaces.CommandExecutor;

public class CurrencyRatesCommand extends CommandExecutor {
	@Override
	public void execute(String sender, String[] args) {
		List<ExchangeTable> exchangeTableList = Common.getInstance().getDatabaseManager().getDatabase().select(ExchangeTable.class).execute().find();
		Common.getInstance().getServerCaller().sendMessage(sender, Common.getInstance().getLanguageManager().getString("rates_header"));
		for (ExchangeTable entry : exchangeTableList) {
			Currency currencyFrom = Common.getInstance().getCurrencyManager().getCurrency(entry.from_currency_id);
			Currency currencyTo = Common.getInstance().getCurrencyManager().getCurrency(entry.to_currency_id);
			Common.getInstance().getServerCaller().sendMessage(sender, "1 " + currencyFrom.getName() + " => " + entry.amount + " " + currencyTo.getName());
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
}
