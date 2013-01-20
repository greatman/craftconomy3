/*
 * This file is part of Craftconomy3.
 *
 * Copyright (c) 2011-2012, Greatman <http://github.com/greatman/>
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
import com.greatmancode.craftconomy3.commands.interfaces.CraftconomyCommand;
import com.greatmancode.craftconomy3.currency.Currency;
import com.greatmancode.craftconomy3.utils.Tools;

public class ExchangeCommand extends CraftconomyCommand {

	@Override
	public void execute(String sender, String[] args) {
		/*if (Tools.isValidDouble(args[0]) && Tools.isValidDouble(args[2])) {
		 	Currency currency1 = Common.getInstance().getCurrencyManager().getCurrency(args[1]);
		 	Currency currency2 = Common.getInstance().getCurrencyManager().getCurrency(args[3]);
		 	if (currency1 != null && currency2 != null) {
		 		
		 	} else {
		 		Common.getInstance().getServerCaller().sendMessage(sender, Common.getInstance().getLanguageManager().getString("currency_not_exist"));
		 	}
		} else {
			Common.getInstance().getServerCaller().sendMessage(sender, Common.getInstance().getLanguageManager().getString("invalid_amount"));
		}*/

	}

	@Override
	public String help() {
		return Common.getInstance().getLanguageManager().getString("money_exchange_cmd_help");
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
		return true;
	}

	@Override
	public String getPermissionNode() {
		return "craftconomy.money.exchange";
	}

}
