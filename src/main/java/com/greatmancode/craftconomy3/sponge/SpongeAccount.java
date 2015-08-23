package com.greatmancode.craftconomy3.sponge;

import com.google.common.base.*;
import com.google.common.base.Optional;
import com.greatmancode.craftconomy3.Cause;
import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.account.Balance;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.economy.Account;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.transaction.*;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by greatman on 15-08-22.
 */
public class SpongeAccount implements Account {

    private com.greatmancode.craftconomy3.account.Account account;
    private SpongeService service;
    public SpongeAccount(com.greatmancode.craftconomy3.account.Account account, SpongeService service) {
        this.account = account;
        this.service = service;
    }
    @Override
    public BigDecimal getDefaultBalance(Currency currency) {
        return new BigDecimal(account.getBalance("default", currency.getDisplayName()));
    }

    @Override
    public BigDecimal getBalance(Currency currency, Set<Context> contexts) {
        String world = getWorld(contexts);
        return new BigDecimal(account.getBalance(world, currency.getDisplayName()));
    }

    @Override
    public Map<Currency, BigDecimal> getBalances(Set<Context> contexts) {
        Map<Currency, BigDecimal> output = new HashMap<>();
        String world = getWorld(contexts);
        List<Balance> balances = account.getAllWorldBalance(world);
        for (Balance entry : balances) {
            output.put(new SpongeCurrency(entry.getCurrency()), new BigDecimal(entry.getBalance()));
        }
        return output;
    }

    @Override
    public TransactionResult setBalances(Map<Currency, BigDecimal> amounts, final Set<Context> contexts) {
        TransactionResult result = new TransactionResult() {
            @Override
            public String getMessage() {
                return "";
            }

            @Override
            public ResultType getResult() {
                return ResultType.SUCCESS;
            }

            @Override
            public TransactionType getType() {
                //TODO Change to SET when added
                return TransactionType.DEPOSIT;
            }

            @Override
            public Optional<Set<Context>> getContexts() {
                return Optional.of(contexts);
            }
        };
        String world = getWorld(contexts);
        for (Map.Entry<Currency, BigDecimal> entry : amounts.entrySet()) {
            account.set(entry.getValue().doubleValue(), world, entry.getKey().getDisplayName(), Cause.VAULT, "Sponge setBalances");
        }
        return result;
    }

    @Override
    public TransactionResult resetBalances(Set<Context> contexts) {
        return null;
    }

    @Override
    public DepositResult deposit(Map<Currency, BigDecimal> amounts, Set<Context> contexts) {
        String world = getWorld(contexts);
        for (Map.Entry<Currency, BigDecimal> entry : amounts.entrySet()) {
            account.deposit(entry.getValue().doubleValue(), world, entry.getKey().getDisplayName(), Cause.VAULT, "Sponge deposit");
        }
        return new DepositResult(this, amounts, Optional.of(contexts), ResultType.SUCCESS, "");
    }

    @Override
    public WithdrawResult withdraw(Map<Currency, BigDecimal> amounts, Set<Context> contexts) {
        String world = getWorld(contexts);
        for (Map.Entry<Currency, BigDecimal> entry : amounts.entrySet()) {
            account.withdraw(entry.getValue().doubleValue(), world, entry.getKey().getDisplayName(), Cause.VAULT, "Sponge deposit");
        }
        return new WithdrawResult(this, amounts, Optional.of(contexts), ResultType.SUCCESS, "");
    }

    @Override
    public TransferResult transfer(Account to, Map<Currency, BigDecimal> amounts, Set<Context> contexts) {
        return null;
    }

    @Override
    public ExchangeResult exchange(Account exchanger, Map<Currency, BigDecimal> fromAmounts, Map<Currency, BigDecimal> withAmounts, Set<Context> contexts) {
        return null;
    }

    @Override
    public String getIdentifier() {
        return account.getAccountName();
    }

    @Override
    public Set<Context> getActiveContexts() {
        Set<Context> contexts = new HashSet<>();
        contexts.add(new Context("worldtype", "world"));
        return contexts;
    }
    public String getWorld(Set<Context> contexts) {
        String world = "default";
        for (Context entry: contexts) {
            if (entry.getKey().equals(Context.WORLD_KEY)) {
                world = entry.getValue();
            }
        }
        return world;
    }
}
