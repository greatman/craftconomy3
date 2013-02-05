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
package com.greatmancode.craftconomy3.spout;

import java.util.List;

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.DisplayFormat;
import com.greatmancode.craftconomy3.currency.Currency;

import org.spout.api.plugin.services.EconomyService;

/**
 * The Economy service hook for the Spout Server.
 */
public class EconomyServiceHandler extends EconomyService {
	@Override
	public boolean has(String name, double amount) {
		return Common.getInstance().getAccountManager().getAccount(name).hasEnough(amount, Common.getInstance().getAccountManager().getAccount(name).getWorldGroupOfPlayerCurrentlyIn(), Common.getInstance().getCurrencyManager().getDefaultCurrency().getName());
	}

	@Override
	public double get(String name) {
		try {
			return get(name, Common.getInstance().getCurrencyManager().getDefaultCurrency().getName());
		} catch (UnknownCurrencyException e) {
			return 0;
		}
	}

	@Override
	public boolean exists(String name) {
		return Common.getInstance().getAccountManager().exist(name);
	}

	@Override
	public String getCurrencyNameSingular() {
		return Common.getInstance().getCurrencyManager().getDefaultCurrency().getName();
	}

	@Override
	public String getCurrencyNamePlural() {
		return Common.getInstance().getCurrencyManager().getDefaultCurrency().getPlural();
	}

	@Override
	public int numSignificantDigits() {
		return 2;
	}

	@Override
	public String getCurrencySymbol() {
		return Common.getInstance().getCurrencyManager().getDefaultCurrency().getSign();
	}

	@Override
	public String format(double amount) {
		return Common.getInstance().format(Common.getInstance().getServerCaller().getDefaultWorld(), Common.getInstance().getCurrencyManager().getDefaultCurrency(), amount);
	}

	@Override
	public String formatShort(double amount) {
		return format(amount);
	}

	@Override
	public boolean hasMulticurrencySupport() {
		return true;
	}

	@Override
	public List<String> getCurrencyNames() {
		return Common.getInstance().getCurrencyManager().getCurrencyNames();
	}

	@Override
	public String getCurrencyNamePlural(String name) {
		String plural = null;
		Currency currency = Common.getInstance().getCurrencyManager().getCurrency(name);
		if (currency != null) {
			plural = currency.getPlural();
		}
		return plural;
	}

	@Override
	public String getCurrencySymbol(String name) throws UnknownCurrencyException {
		String sign = null;
		Currency currency = Common.getInstance().getCurrencyManager().getCurrency(name);
		if (currency == null) {
			throw new UnknownCurrencyException();
		}
		sign = currency.getSign();
		return sign;
	}

	@Override
	public String format(String name, double amount) {
		return Common.getInstance().format(null, Common.getInstance().getCurrencyManager().getCurrency(name), amount);
	}

	@Override
	public String formatShort(String name, double amount) {
		return Common.getInstance().format(null, Common.getInstance().getCurrencyManager().getCurrency(name), amount, DisplayFormat.SIGN);
	}

	@Override
	protected boolean onWithdraw(String name, double amount, String currency) throws UnknownCurrencyException {
		boolean result = false;
		Currency currencyEntry = Common.getInstance().getCurrencyManager().getDefaultCurrency();
		if (currency != null) {
			currencyEntry = Common.getInstance().getCurrencyManager().getCurrency(currency);
		}

		if (currencyEntry == null) {
			throw new UnknownCurrencyException();
		}
		if (Common.getInstance().getAccountManager().getAccount(name).hasEnough(amount, Common.getInstance().getAccountManager().getAccount(name).getWorldGroupOfPlayerCurrentlyIn(), currencyEntry.getName())) {
			Common.getInstance().getAccountManager().getAccount(name).withdraw(amount, Common.getInstance().getAccountManager().getAccount(name).getWorldGroupOfPlayerCurrentlyIn(), currencyEntry.getName());
			result = true;
		}

		return result;
	}

	@Override
	protected boolean onDeposit(String name, double amount, String currency) throws UnknownCurrencyException {
		Currency currencyEntry = Common.getInstance().getCurrencyManager().getDefaultCurrency();
		if (currency != null) {
			currencyEntry = Common.getInstance().getCurrencyManager().getCurrency(currency);
		}
		if (currencyEntry == null) {
			throw new UnknownCurrencyException();
		}
		Common.getInstance().getAccountManager().getAccount(name).deposit(amount, Common.getInstance().getAccountManager().getAccount(name).getWorldGroupOfPlayerCurrentlyIn(), currencyEntry.getName());
		return true;
	}

	@Override
	public boolean has(String name, double amount, String currency) throws UnknownCurrencyException {
		boolean result = false;
		Currency currencyEntry = Common.getInstance().getCurrencyManager().getCurrency(currency);
		if (currencyEntry == null) {
			throw new UnknownCurrencyException();
		}
		if (Common.getInstance().getAccountManager().getAccount(name).hasEnough(amount, Common.getInstance().getAccountManager().getAccount(name).getWorldGroupOfPlayerCurrentlyIn(), currencyEntry.getName())) {
			result = true;
		}
		return result;
	}

	@Override
	public double get(String name, String currency) throws UnknownCurrencyException {
		double result = 0.0;
		Currency currencyEntry = Common.getInstance().getCurrencyManager().getCurrency(currency);
		if (currencyEntry == null) {
			throw new UnknownCurrencyException();
		}
		if (currency != null) {
			result = Common.getInstance().getAccountManager().getAccount(name).getBalance(Common.getInstance().getAccountManager().getAccount(name).getWorldGroupOfPlayerCurrentlyIn(), currencyEntry.getName());
		}
		return result;
	}

	@Override
	public boolean create(String name) {
		Common.getInstance().getAccountManager().getAccount(name);
		return true;
	}

	@Override
	public List<String> getTopAccounts(int start, int end, boolean playersOnly) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getTopAccounts(int start, int end, String currency, boolean playersOnly) throws UnknownCurrencyException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getExchangeRate(String currencyFrom, String currencyTo) {
		return 1;
	}

	@Override
	public boolean canWithdraw(String name, double amount) {
		return has(name, amount);
	}

	@Override
	public boolean canWithdraw(String name, double amount, String currency) throws UnknownCurrencyException {
		return has(name, amount, currency);
	}

	@Override
	public boolean canHold(String name, double amount) {
		// Can't fail atm. There's no limit.
		return true;
	}

	@Override
	public boolean canHold(String name, double amount, String currency) throws UnknownCurrencyException {
		// TODO Auto-generated method stub
		return true;
	}
}
