package com.greatmancode.craftconomy3.sponge;

import com.google.common.base.Optional;
import com.greatmancode.craftconomy3.Common;
import com.greatmancode.tools.interfaces.SpongeLoader;
import org.spongepowered.api.service.ProviderExistsException;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.context.ContextCalculator;
import org.spongepowered.api.service.economy.Account;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.Wallet;
import org.spongepowered.api.service.economy.transaction.TransactionCallback;
import org.spongepowered.api.service.economy.transaction.TransactionResult;

import java.util.*;

/**
 * Created by greatman on 15-08-22.
 */
public class SpongeService implements EconomyService {

    private List<TransactionCallback> callbacks;
    public SpongeService() {
        SpongeLoader loader = (SpongeLoader) Common.getInstance().getServerCaller().getLoader();
        try {
            loader.getGame().getServiceManager().setProvider(loader, EconomyService.class, this);
            System.out.println("WE ARE NOW A REGISTERED SPONGE ECON PROVIDER");
        } catch (ProviderExistsException e) {
            e.printStackTrace();
        }
    }
    @Override
    public Currency getDefaultCurrency() {
        return new SpongeCurrency(Common.getInstance().getCurrencyManager().getDefaultCurrency());
    }

    @Override
    public Collection<Currency> getCurrencies() {
        List<Currency> currencies = new ArrayList<>();
        for (String name : Common.getInstance().getCurrencyManager().getCurrencyNames()) {
            currencies.add(new SpongeCurrency(Common.getInstance().getCurrencyManager().getCurrency(name)));
        }
        return currencies;
    }

    @Override
    public Optional<Account> getAccount(String identifier) {
        return null;
    }

    @Override
    public Collection<Account> getAccounts(UUID uuid, Optional<Set<Context>> contexts) {
        //
        return null;
    }

    @Override
    public Wallet getWallet(UUID uuid) {
        return new SpongeWallet(uuid, this);
    }

    @Override
    public void registerTransactionCallback(TransactionCallback callback) {
        callbacks.add(callback);
    }

    @Override
    public void registerContextCalculator(ContextCalculator<Account> calculator) {

    }

    public void notify(TransactionResult result) {
        for (TransactionCallback callback : callbacks) {
            callback.notify(result);
        }
    }
}
