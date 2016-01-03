/**
 * This file is part of Craftconomy3.
 *
 * Copyright (c) 2011-2016, Greatman <http://github.com/greatman/>
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

import com.greatmancode.craftconomy3.Cause;
import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.TestInitializator;
import com.greatmancode.craftconomy3.account.Account;
import com.greatmancode.craftconomy3.commands.bank.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.logging.Level;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestBankCommands {

    private static final String BANK_ACCOUNT = "testbankaccount39";
    private static final String TEST_USER = "testuser39";
    private static final String TEST_USER2 = "testuser40";
    @Before
    public void setUp() {
        new TestInitializator();
        System.out.println("Initialized");
    }

    @After
    public void close() { Common.getInstance().onDisable();};

    @Test
    public void testBankCreateCommand() {
        BankCreateCommand command = new BankCreateCommand();
        System.out.println("Creating a bank account!");
        command.execute(TEST_USER, new String[]{BANK_ACCOUNT});
        System.out.println("DONE");
        assertFalse(Common.getInstance().getAccountManager().exist(BANK_ACCOUNT, true));
        Common.getInstance().getAccountManager().getAccount(TEST_USER,false).set(200, "default", Common.getInstance().getCurrencyManager().getDefaultCurrency().getName(), Cause.USER, "greatman");
        command.execute(TEST_USER, new String[]{BANK_ACCOUNT});
        assertTrue(Common.getInstance().getAccountManager().exist(BANK_ACCOUNT, true));
        command.execute(TEST_USER, new String[]{BANK_ACCOUNT});
    }

    @Test
    public void testBankGiveCommand() {
        BankGiveCommand command = new BankGiveCommand();
        Common.getInstance().getAccountManager().getAccount(BANK_ACCOUNT, true);
        double initialValue = Common.getInstance().getAccountManager().getAccount(BANK_ACCOUNT, true).getBalance("default", Common.getInstance().getCurrencyManager().getDefaultCurrency().getName());
        command.execute(TEST_USER, new String[]{BANK_ACCOUNT, "wow"});
        assertEquals(initialValue, Common.getInstance().getAccountManager().getAccount(BANK_ACCOUNT, true).getBalance("default", Common.getInstance().getCurrencyManager().getDefaultCurrency().getName()), 0);
        command.execute(TEST_USER, new String[]{BANK_ACCOUNT, "100"});
        assertEquals(initialValue + 100, Common.getInstance().getAccountManager().getAccount(BANK_ACCOUNT, true).getBalance("default", Common.getInstance().getCurrencyManager().getDefaultCurrency().getName()), 0);
    }

    @Test
    public void testBankTakeCommand() {
        BankTakeCommand command = new BankTakeCommand();
        Common.getInstance().getAccountManager().getAccount(BANK_ACCOUNT, true);
        command.execute(TEST_USER, new String[]{BANK_ACCOUNT, "100"});
        assertEquals(0, Common.getInstance().getAccountManager().getAccount(BANK_ACCOUNT, true).getBalance(Account.getWorldGroupOfPlayerCurrentlyIn(TEST_USER), Common.getInstance().getCurrencyManager().getDefaultCurrency().getName()), 0);
        Common.getInstance().getAccountManager().getAccount(BANK_ACCOUNT, true).deposit(200, Account.getWorldGroupOfPlayerCurrentlyIn(TEST_USER), Common.getInstance().getCurrencyManager().getDefaultCurrency().getName(), Cause.UNKNOWN, "unittest");
        command.execute(TEST_USER, new String[]{BANK_ACCOUNT, "100"});
        assertEquals(100, Common.getInstance().getAccountManager().getAccount(BANK_ACCOUNT, true).getBalance(Account.getWorldGroupOfPlayerCurrentlyIn(TEST_USER), Common.getInstance().getCurrencyManager().getDefaultCurrency().getName()), 0);
    }

    @Test
    public void testBankBalanceCommand() {
        BankBalanceCommand command = new BankBalanceCommand();
        Common.getInstance().getAccountManager().getAccount(BANK_ACCOUNT, true);
        command.execute(TEST_USER, new String[] {BANK_ACCOUNT});
        command.execute(TEST_USER, new String[] {"unknown"});

    }

    @Test
    public void testBankDepositCommand() {

    }

    @Test
    public void testBankIgnoreACLCommand() {
        Common.getInstance().getAccountManager().getAccount(BANK_ACCOUNT, true);
        BankIgnoreACLCommand command = new BankIgnoreACLCommand();
        assertFalse(Common.getInstance().getAccountManager().getAccount(BANK_ACCOUNT, true).ignoreACL());
        command.execute(TEST_USER, new String[]{BANK_ACCOUNT});
        assertTrue(Common.getInstance().getAccountManager().getAccount(BANK_ACCOUNT, true).ignoreACL());
        command.execute(TEST_USER, new String[]{BANK_ACCOUNT});
        assertFalse(Common.getInstance().getAccountManager().getAccount(BANK_ACCOUNT, true).ignoreACL());
        command.execute(TEST_USER, new String[]{"unknown"});
        assertFalse(Common.getInstance().getAccountManager().getAccount(BANK_ACCOUNT, true).ignoreACL());
    }

    @Test
    public void testBankListCommand() {
        //Can't use the global-defined accounts since we require the user to have exactly one account after adding it
        String BANK_LIST_USER = "banklistuser",
                BANK_LIST_ACC = "banklistacc";
        Account bank = Common.getInstance().getAccountManager().getAccount(BANK_LIST_ACC, true);
        bank.getAccountACL().set(BANK_LIST_USER, true, true, true, true, true);
        assertEquals(Common.getInstance().getStorageHandler().getStorageEngine().getBankAccountList(BANK_LIST_USER).length, 1);
    }

    @Test
    public void testBankPermCommand() {
        BankPermCommand command = new BankPermCommand();
        Common.getInstance().getAccountManager().getAccount(TEST_USER,false).set(200, "default", Common.getInstance().getCurrencyManager().getDefaultCurrency().getName(), Cause.USER, "greatman");
        new BankCreateCommand().execute(TEST_USER, new String[]{BANK_ACCOUNT});
        Account account = Common.getInstance().getAccountManager().getAccount(BANK_ACCOUNT, true);
        command.execute(TEST_USER, new String[] {BANK_ACCOUNT, "deposit", TEST_USER2, "true"});
        System.out.println("WOW SUPER");

        assertTrue(account.getAccountACL().canDeposit(TEST_USER2));
        assertFalse(account.getAccountACL().canAcl(TEST_USER2));
        assertFalse(account.getAccountACL().canShow(TEST_USER2));
        assertFalse(account.getAccountACL().canWithdraw(TEST_USER2));
        assertFalse(account.getAccountACL().isOwner(TEST_USER2));
        command.execute(TEST_USER, new String[] {BANK_ACCOUNT, "deposit", TEST_USER2, "false"});
        assertFalse(account.getAccountACL().canDeposit(TEST_USER2));
        assertFalse(account.getAccountACL().canAcl(TEST_USER2));
        assertFalse(account.getAccountACL().canShow(TEST_USER2));
        assertFalse(account.getAccountACL().canWithdraw(TEST_USER2));
        assertFalse(account.getAccountACL().isOwner(TEST_USER2));

        command.execute(TEST_USER, new String[] {BANK_ACCOUNT, "withdraw", TEST_USER2, "true"});
        assertFalse(account.getAccountACL().canDeposit(TEST_USER2));
        assertFalse(account.getAccountACL().canAcl(TEST_USER2));
        assertFalse(account.getAccountACL().canShow(TEST_USER2));
        assertTrue(account.getAccountACL().canWithdraw(TEST_USER2));
        assertFalse(account.getAccountACL().isOwner(TEST_USER2));

        command.execute(TEST_USER, new String[] {BANK_ACCOUNT, "withdraw", TEST_USER2, "false"});
        assertFalse(account.getAccountACL().canDeposit(TEST_USER2));
        assertFalse(account.getAccountACL().canAcl(TEST_USER2));
        assertFalse(account.getAccountACL().canShow(TEST_USER2));
        assertFalse(account.getAccountACL().canWithdraw(TEST_USER2));
        assertFalse(account.getAccountACL().isOwner(TEST_USER2));



        command.execute(TEST_USER, new String[] {BANK_ACCOUNT, "show", TEST_USER2, "true"});
        assertFalse(account.getAccountACL().canDeposit(TEST_USER2));
        assertFalse(account.getAccountACL().canAcl(TEST_USER2));
        assertTrue(account.getAccountACL().canShow(TEST_USER2));
        assertFalse(account.getAccountACL().canWithdraw(TEST_USER2));
        assertFalse(account.getAccountACL().isOwner(TEST_USER2));

        command.execute(TEST_USER, new String[] {BANK_ACCOUNT, "show", TEST_USER2, "false"});
        assertFalse(account.getAccountACL().canDeposit(TEST_USER2));
        assertFalse(account.getAccountACL().canAcl(TEST_USER2));
        assertFalse(account.getAccountACL().canShow(TEST_USER2));
        assertFalse(account.getAccountACL().canWithdraw(TEST_USER2));
        assertFalse(account.getAccountACL().isOwner(TEST_USER2));
    }

    @Test
    public void testBankSetCommand() {
        BankSetCommand command = new BankSetCommand();
        Common.getInstance().getAccountManager().getAccount(BANK_ACCOUNT, true);
        double initialValue = Common.getInstance().getAccountManager().getAccount(BANK_ACCOUNT, true).getBalance("default", Common.getInstance().getCurrencyManager().getDefaultCurrency().getName());
        command.execute(TEST_USER, new String[]{BANK_ACCOUNT, "wow"});
        assertEquals(initialValue, Common.getInstance().getAccountManager().getAccount(BANK_ACCOUNT, true).getBalance("default", Common.getInstance().getCurrencyManager().getDefaultCurrency().getName()), 0);
        command.execute(TEST_USER, new String[]{BANK_ACCOUNT, "100"});
        assertEquals(initialValue + 100, Common.getInstance().getAccountManager().getAccount(BANK_ACCOUNT, true).getBalance("default", Common.getInstance().getCurrencyManager().getDefaultCurrency().getName()), 0);
        command.execute(TEST_USER, new String[]{BANK_ACCOUNT, "0"});
        assertEquals(initialValue, Common.getInstance().getAccountManager().getAccount(BANK_ACCOUNT, true).getBalance("default", Common.getInstance().getCurrencyManager().getDefaultCurrency().getName()), 0);

    }

    @Test
    public void testBankWithdrawCommand() {
        BankWithdrawCommand command = new BankWithdrawCommand();
        Common.getInstance().getAccountManager().getAccount(TEST_USER,false).set(200, "default", Common.getInstance().getCurrencyManager().getDefaultCurrency().getName(), Cause.USER, "greatman");
        new BankCreateCommand().execute(TEST_USER, new String[]{BANK_ACCOUNT});
        Account account = Common.getInstance().getAccountManager().getAccount(BANK_ACCOUNT, true);
        account.set(200, "default", Common.getInstance().getCurrencyManager().getDefaultCurrency().getName(), Cause.UNKNOWN, null);
        account.withdraw(20, "default", Common.getInstance().getCurrencyManager().getDefaultCurrency().getName(), Cause.UNKNOWN, null);
        assertEquals(180, account.getBalance("default", Common.getInstance().getCurrencyManager().getDefaultCurrency().getName()), 0);
    }

    @Test
    public void testBankDeleteCommand() {
        new BankCreateCommand().execute(TEST_USER, new String[]{BANK_ACCOUNT});
        BankDeleteCommand command = new BankDeleteCommand();
        command.execute(TEST_USER, new String[] {BANK_ACCOUNT});
        assertFalse(Common.getInstance().getAccountManager().exist(BANK_ACCOUNT, true));
    }
}
