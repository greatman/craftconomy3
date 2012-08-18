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
package com.greatmancode.craftconomy3.commands;

import java.util.HashMap;

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.commands.bank.*;
import com.greatmancode.craftconomy3.commands.config.*;
import com.greatmancode.craftconomy3.commands.currency.*;
import com.greatmancode.craftconomy3.commands.money.*;
import com.greatmancode.craftconomy3.commands.setup.*;

/**
 * Generic CommandLoader. Loads all the sub-commands.
 * @author greatman
 * 
 */
public class CommandLoader {

	private HashMap<String, CraftconomyCommand> moneyCmdList = new HashMap<String, CraftconomyCommand>();
	private HashMap<String, CraftconomyCommand> bankCmdList = new HashMap<String, CraftconomyCommand>();
	private HashMap<String, CraftconomyCommand> setupCmdList = new HashMap<String, CraftconomyCommand>();
	private HashMap<String, CraftconomyCommand> currencyCmdList = new HashMap<String, CraftconomyCommand>();
	private HashMap<String, CraftconomyCommand> configCmdList = new HashMap<String, CraftconomyCommand>();
	private HashMap<String, CraftconomyCommand> paydayCmdList = new HashMap<String, CraftconomyCommand>();
	private CommandManager cmdLoader;

	public CommandLoader() {
		moneyCmdList.put("", new MainCommand());
		moneyCmdList.put("all", new AllCommand());
		moneyCmdList.put("pay", new PayCommand());
		moneyCmdList.put("give", new GiveCommand());
		moneyCmdList.put("take", new TakeCommand());
		moneyCmdList.put("set", new SetCommand());
		moneyCmdList.put("delete", new DeleteCommand());
		moneyCmdList.put("create", new CreateCommand());
		moneyCmdList.put("delete", new DeleteCommand());
		moneyCmdList.put("help", new HelpCommand());
		moneyCmdList.put("balance", new BalanceCommand());
		moneyCmdList.put("top", new TopCommand());

		bankCmdList.put("create", new BankCreateCommand());
		bankCmdList.put("balance", new BankBalanceCommand());
		bankCmdList.put("deposit", new BankDepositCommand());
		bankCmdList.put("withdraw", new BankWithdrawCommand());
		bankCmdList.put("set", new BankSetCommand());
		bankCmdList.put("help", new BankHelpCommand());
		bankCmdList.put("give", new BankGiveCommand());
		bankCmdList.put("take", new BankTakeCommand());
		bankCmdList.put("perm", new BankPermCommand());
		
		setupCmdList.put("", new SetupMainCommand());
		setupCmdList.put("database", new SetupDatabaseCommand());
		setupCmdList.put("multiworld", new SetupMultiWorldCommand());
		setupCmdList.put("currency", new SetupCurrencyCommand());
		setupCmdList.put("basic", new SetupBasicCommand());
		setupCmdList.put("convert", new SetupConvertCommand());
		
		currencyCmdList.put("add", new CurrencyAddCommand());
		currencyCmdList.put("delete", new CurrencyDeleteCommand());
		currencyCmdList.put("edit", new CurrencyEditCommand());
		currencyCmdList.put("info", new CurrencyInfoCommand());
		currencyCmdList.put("help", new CurrencyHelpCommand());
		
		configCmdList.put("holdings", new ConfigHoldingsCommand());
		configCmdList.put("longmode", new ConfigLongModeCommand());
		configCmdList.put("bankprice", new ConfigBankPriceCommand());
		configCmdList.put("help", new ConfigHelpCommand());

		if (!Common.isBukkit()) {
			cmdLoader = new SpoutCommandManager();
		}
	}

	/**
	 * Get the list of sub-commands of the /money command.
	 * @return A HashMap containing the sub-commands.
	 */
	public HashMap<String, CraftconomyCommand> getMoneyCmdList() {
		return moneyCmdList;
	}

	/**
	 * Get the list of sub-commands of the /bank command
	 * @return A HashMap containing the sub-commands.
	 */
	public HashMap<String, CraftconomyCommand> getBankCmdList() {
		return bankCmdList;
	}
	
	public HashMap<String,CraftconomyCommand> getSetupCmdList() {
		return setupCmdList;
	}
	
	public HashMap<String,CraftconomyCommand> getCurrencyCmdList() {
		return currencyCmdList;
	}
	
	public HashMap<String,CraftconomyCommand> getConfigCmdList() {
		return configCmdList;
	}

	public HashMap<String, CraftconomyCommand> getPaydayCmdList() {
		return paydayCmdList;
	}
}
