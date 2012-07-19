package com.greatmancode.craftconomy3.spout;

import org.spout.api.plugin.services.EconomyService;

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.currency.CurrencyManager;

public class EconomyServiceHandler extends EconomyService {

	@Override
	public boolean has(String name, double amount) {
		return Common.getInstance().getAccountHandler().getAccount(name).hasEnough(amount, Common.getInstance().getAccountHandler().getAccount(name).getWorldPlayerCurrentlyIn(), Common.getInstance().getCurrencyManager().getCurrency(CurrencyManager.DefaultCurrencyID).getName());
	}

	@Override
	public double get(String name) {
		return Common.getInstance().getAccountHandler().getAccount(name).getBalance(Common.getInstance().getAccountHandler().getAccount(name).getWorldPlayerCurrentlyIn(), Common.getInstance().getCurrencyManager().getCurrency(CurrencyManager.DefaultCurrencyID).getName());
	}

	@Override
	public boolean withdraw(String name, double amount) {
		boolean result = false;
		if (Common.getInstance().getAccountHandler().getAccount(name).hasEnough(amount, Common.getInstance().getAccountHandler().getAccount(name).getWorldPlayerCurrentlyIn(), Common.getInstance().getCurrencyManager().getCurrency(CurrencyManager.DefaultCurrencyID).getName())) {
			Common.getInstance().getAccountHandler().getAccount(name).withdraw(amount, Common.getInstance().getAccountHandler().getAccount(name).getWorldPlayerCurrentlyIn(), Common.getInstance().getCurrencyManager().getCurrency(CurrencyManager.DefaultCurrencyID).getName());
		}

		return result;
	}

	@Override
	public boolean deposit(String name, double amount) {
		Common.getInstance().getAccountHandler().getAccount(name).deposit(amount, Common.getInstance().getAccountHandler().getAccount(name).getWorldPlayerCurrentlyIn(), Common.getInstance().getCurrencyManager().getCurrency(CurrencyManager.DefaultCurrencyID).getName());
		return true;
	}

	@Override
	public boolean exists(String name) {
		// If the account doesn't exist, it's created automaticly anyway.
		return true;
	}

	@Override
	public String getCurrencyNameSingular() {
		return Common.getInstance().getCurrencyManager().getCurrency(CurrencyManager.DefaultCurrencyID).getName();
	}

	@Override
	public String getCurrencyNamePlural() {
		return Common.getInstance().getCurrencyManager().getCurrency(CurrencyManager.DefaultCurrencyID).getPlural();
	}

	@Override
	public int numSignificantDigits() {
		return 2;
	}

}
