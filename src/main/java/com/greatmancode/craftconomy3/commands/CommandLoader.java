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
import java.util.Map;

import com.greatmancode.craftconomy3.BukkitCaller;
import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.SpoutCaller;
import com.greatmancode.craftconomy3.commands.bank.BankBalanceCommand;
import com.greatmancode.craftconomy3.commands.bank.BankCreateCommand;
import com.greatmancode.craftconomy3.commands.bank.BankDepositCommand;
import com.greatmancode.craftconomy3.commands.bank.BankGiveCommand;
import com.greatmancode.craftconomy3.commands.bank.BankHelpCommand;
import com.greatmancode.craftconomy3.commands.bank.BankPermCommand;
import com.greatmancode.craftconomy3.commands.bank.BankSetCommand;
import com.greatmancode.craftconomy3.commands.bank.BankTakeCommand;
import com.greatmancode.craftconomy3.commands.bank.BankWithdrawCommand;
import com.greatmancode.craftconomy3.commands.config.ConfigBankPriceCommand;
import com.greatmancode.craftconomy3.commands.config.ConfigFormatCommand;
import com.greatmancode.craftconomy3.commands.config.ConfigHelpCommand;
import com.greatmancode.craftconomy3.commands.config.ConfigHoldingsCommand;
import com.greatmancode.craftconomy3.commands.currency.CurrencyAddCommand;
import com.greatmancode.craftconomy3.commands.currency.CurrencyDefaultCommand;
import com.greatmancode.craftconomy3.commands.currency.CurrencyDeleteCommand;
import com.greatmancode.craftconomy3.commands.currency.CurrencyEditCommand;
import com.greatmancode.craftconomy3.commands.currency.CurrencyHelpCommand;
import com.greatmancode.craftconomy3.commands.currency.CurrencyInfoCommand;
import com.greatmancode.craftconomy3.commands.money.AllCommand;
import com.greatmancode.craftconomy3.commands.money.BalanceCommand;
import com.greatmancode.craftconomy3.commands.money.CreateCommand;
import com.greatmancode.craftconomy3.commands.money.DeleteCommand;
import com.greatmancode.craftconomy3.commands.money.GiveCommand;
import com.greatmancode.craftconomy3.commands.money.HelpCommand;
import com.greatmancode.craftconomy3.commands.money.MainCommand;
import com.greatmancode.craftconomy3.commands.money.PayCommand;
import com.greatmancode.craftconomy3.commands.money.SetCommand;
import com.greatmancode.craftconomy3.commands.money.TakeCommand;
import com.greatmancode.craftconomy3.commands.money.TopCommand;
import com.greatmancode.craftconomy3.commands.payday.PayDayCreateCommand;
import com.greatmancode.craftconomy3.commands.payday.PayDayDeleteCommand;
import com.greatmancode.craftconomy3.commands.payday.PayDayHelpCommand;
import com.greatmancode.craftconomy3.commands.payday.PayDayInfoCommand;
import com.greatmancode.craftconomy3.commands.payday.PayDayListCommand;
import com.greatmancode.craftconomy3.commands.payday.PayDayModifyCommand;
import com.greatmancode.craftconomy3.commands.setup.SetupBasicCommand;
import com.greatmancode.craftconomy3.commands.setup.SetupConvertCommand;
import com.greatmancode.craftconomy3.commands.setup.SetupCurrencyCommand;
import com.greatmancode.craftconomy3.commands.setup.SetupDatabaseCommand;
import com.greatmancode.craftconomy3.commands.setup.SetupMainCommand;
import com.greatmancode.craftconomy3.commands.setup.SetupMultiWorldCommand;

/**
 * Generic CommandLoader. Loads all the sub-commands.
 * 
 * @author greatman
 * 
 */
public class CommandLoader {

	private Map<String, CommandHandler> commandList = new HashMap<String, CommandHandler>();
	private CommandManager manager;
	private boolean initialized = false;

	public CommandLoader() {
		if (Common.getInstance().getServerCaller() instanceof BukkitCaller) {
			manager = new BukkitCommandManager();
		} else if (Common.getInstance().getServerCaller() instanceof SpoutCaller) {
			manager = new SpoutCommandManager();
		}
	}

	public void initialize() {
		if (!initialized) {
			CommandHandler moneyCommand = new CommandHandler("money", "Main money command.", false);
			moneyCommand.registerCommand("", new MainCommand());
			moneyCommand.registerCommand("all", new AllCommand());
			moneyCommand.registerCommand("pay", new PayCommand());
			moneyCommand.registerCommand("give", new GiveCommand());
			moneyCommand.registerCommand("take", new TakeCommand());
			moneyCommand.registerCommand("set", new SetCommand());
			moneyCommand.registerCommand("delete", new DeleteCommand());
			moneyCommand.registerCommand("create", new CreateCommand());
			moneyCommand.registerCommand("help", new HelpCommand());
			moneyCommand.registerCommand("balance", new BalanceCommand());
			moneyCommand.registerCommand("top", new TopCommand());
			commandList.put("money", moneyCommand);

			CommandHandler bankCommand = new CommandHandler("bank", "Bank related commands.", false);
			bankCommand.registerCommand("create", new BankCreateCommand());
			bankCommand.registerCommand("balance", new BankBalanceCommand());
			bankCommand.registerCommand("deposit", new BankDepositCommand());
			bankCommand.registerCommand("withdraw", new BankWithdrawCommand());
			bankCommand.registerCommand("set", new BankSetCommand());
			bankCommand.registerCommand("", new BankHelpCommand());
			bankCommand.registerCommand("give", new BankGiveCommand());
			bankCommand.registerCommand("take", new BankTakeCommand());
			bankCommand.registerCommand("perm", new BankPermCommand());
			commandList.put("bank", bankCommand);

			CommandHandler setupCommand = new CommandHandler("ccsetup", "Setup related commands", true);
			setupCommand.registerCommand("", new SetupMainCommand());
			setupCommand.registerCommand("database", new SetupDatabaseCommand());
			setupCommand.registerCommand("multiworld", new SetupMultiWorldCommand());
			setupCommand.registerCommand("currency", new SetupCurrencyCommand());
			setupCommand.registerCommand("basic", new SetupBasicCommand());
			setupCommand.registerCommand("convert", new SetupConvertCommand());
			commandList.put("ccsetup", setupCommand);

			CommandHandler currencyCommand = new CommandHandler("currency", "Currency related commands", false);
			currencyCommand.registerCommand("add", new CurrencyAddCommand());
			currencyCommand.registerCommand("delete", new CurrencyDeleteCommand());
			currencyCommand.registerCommand("edit", new CurrencyEditCommand());
			currencyCommand.registerCommand("info", new CurrencyInfoCommand());
			currencyCommand.registerCommand("", new CurrencyHelpCommand());
			currencyCommand.registerCommand("default", new CurrencyDefaultCommand());
			commandList.put("currency", currencyCommand);

			CommandHandler configCommand = new CommandHandler("craftconomy", "config related commands", false);
			configCommand.registerCommand("holdings", new ConfigHoldingsCommand());
			configCommand.registerCommand("bankprice", new ConfigBankPriceCommand());
			configCommand.registerCommand("format", new ConfigFormatCommand());
			configCommand.registerCommand("", new ConfigHelpCommand());
			commandList.put("craftconomy", configCommand);

			CommandHandler paydayCommand = new CommandHandler("payday", "Payday related commands", false);
			paydayCommand.registerCommand("create", new PayDayCreateCommand());
			paydayCommand.registerCommand("delete", new PayDayDeleteCommand());
			paydayCommand.registerCommand("", new PayDayHelpCommand());
			paydayCommand.registerCommand("modify", new PayDayModifyCommand());
			paydayCommand.registerCommand("list", new PayDayListCommand());
			paydayCommand.registerCommand("info", new PayDayInfoCommand());
			commandList.put("payday", paydayCommand);
			initialized = true;
		}
	}

	public boolean commandExist(String commandName) {
		return commandList.containsKey(commandName);
	}

	public CommandHandler getCommandHandler(String commandName) {
		return commandList.get(commandName);
	}

	public CommandManager getCommandManager() {
		return manager;
	}
}
