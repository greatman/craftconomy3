package com.greatmancode.craftconomy3.account;

import com.greatmancode.craftconomy3.currency.Currency;

public class Balance {

	private String world;
	private Currency currency;
	private double balance;
	public Balance(String world, Currency currency, double balance)
	{
		this.world = world;
		this.currency = currency;
		this.balance = balance;
	}
	public String getWorld() {
		return world;
	}
	public void setWorld(String world) {
		this.world = world;
	}
	public Currency getCurrency() {
		return currency;
	}
	public void setCurrency(Currency currency) {
		this.currency = currency;
	}
	public double getBalance() {
		return balance;
	}
	public void setBalance(double balance) {
		this.balance = balance;
	}
}
