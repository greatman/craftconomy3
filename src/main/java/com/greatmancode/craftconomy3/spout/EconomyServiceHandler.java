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
package com.greatmancode.craftconomy3.spout;

import org.spout.api.plugin.services.EconomyService;

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.currency.CurrencyManager;

public class EconomyServiceHandler extends EconomyService {

	@Override
	public boolean has(String name, double amount) {
		return Common.getInstance().getAccountManager().getAccount(name).hasEnough(amount, Common.getInstance().getAccountManager().getAccount(name).getWorldPlayerCurrentlyIn(), Common.getInstance().getCurrencyManager().getCurrency(CurrencyManager.defaultCurrencyID).getName());
	}

	@Override
	public double get(String name) {
		return Common.getInstance().getAccountManager().getAccount(name).getBalance(Common.getInstance().getAccountManager().getAccount(name).getWorldPlayerCurrentlyIn(), Common.getInstance().getCurrencyManager().getCurrency(CurrencyManager.defaultCurrencyID).getName());
	}

	@Override
	public boolean withdraw(String name, double amount) {
		boolean result = false;
		if (Common.getInstance().getAccountManager().getAccount(name).hasEnough(amount, Common.getInstance().getAccountManager().getAccount(name).getWorldPlayerCurrentlyIn(), Common.getInstance().getCurrencyManager().getCurrency(CurrencyManager.defaultCurrencyID).getName())) {
			Common.getInstance().getAccountManager().getAccount(name).withdraw(amount, Common.getInstance().getAccountManager().getAccount(name).getWorldPlayerCurrentlyIn(), Common.getInstance().getCurrencyManager().getCurrency(CurrencyManager.defaultCurrencyID).getName());
		}

		return result;
	}

	@Override
	public boolean deposit(String name, double amount) {
		Common.getInstance().getAccountManager().getAccount(name).deposit(amount, Common.getInstance().getAccountManager().getAccount(name).getWorldPlayerCurrentlyIn(), Common.getInstance().getCurrencyManager().getCurrency(CurrencyManager.defaultCurrencyID).getName());
		return true;
	}

	@Override
	public boolean exists(String name) {
		// If the account doesn't exist, it's created automaticly anyway.
		return true;
	}

	@Override
	public String getCurrencyNameSingular() {
		return Common.getInstance().getCurrencyManager().getCurrency(CurrencyManager.defaultCurrencyID).getName();
	}

	@Override
	public String getCurrencyNamePlural() {
		return Common.getInstance().getCurrencyManager().getCurrency(CurrencyManager.defaultCurrencyID).getPlural();
	}

	@Override
	public int numSignificantDigits() {
		return 2;
	}

	@Override
	public String getCurrencySymbol() {
		return null;
	}

	@Override
	public String format(double amount) {
		return Common.getInstance().format(Common.getInstance().getServerCaller().getDefaultWorld(), Common.getInstance().getCurrencyManager().getCurrency(CurrencyManager.defaultCurrencyID), amount);
	}

	@Override
	public String formatShort(double amount) {
		return format(amount);
	}

}
