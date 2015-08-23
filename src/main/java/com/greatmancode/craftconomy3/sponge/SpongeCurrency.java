package com.greatmancode.craftconomy3.sponge;

import com.google.common.base.Optional;
import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.utils.NoExchangeRate;
import org.spongepowered.api.service.economy.Currency;

import java.math.BigDecimal;

/**
 * Created by greatman on 15-08-22.
 */
public class SpongeCurrency implements Currency {

    private com.greatmancode.craftconomy3.currency.Currency currency;

    public SpongeCurrency(com.greatmancode.craftconomy3.currency.Currency currency) {
        this.currency = currency;
    }
    @Override
    public String getDisplayName() {
        return currency.getName();
    }

    @Override
    public Optional<String> getPluralDisplayName() {
        return Optional.of(currency.getPlural());
    }

    @Override
    public Optional<String> getSymbol() {
        return Optional.of(currency.getSign());
    }

    @Override
    public String format(BigDecimal amount) {
        return Common.getInstance().format(null, currency, amount.doubleValue(), Common.getInstance().getDisplayFormat());
    }

    @Override
    public String format(BigDecimal amount, int numFractionDigits) {
        //TODO Support numFractionDigits
        return Common.getInstance().format(null, currency, amount.doubleValue(), Common.getInstance().getDisplayFormat());
    }

    @Override
    public Optional<BigDecimal> convertTo(Currency currencyTo, BigDecimal amount) {
        com.greatmancode.craftconomy3.currency.Currency cc3Currency = Common.getInstance().getCurrencyManager().getCurrency(currencyTo.getDisplayName());
        try {
            double exchangeRate = currency.getExchangeRate(cc3Currency);
            double value = amount.doubleValue() * exchangeRate;
            return Optional.of(new BigDecimal(value));
        } catch (NoExchangeRate noExchangeRate) {
            return Optional.absent();
        }
    }

    @Override
    public boolean canConvertTo(Currency currencyTo) {
        com.greatmancode.craftconomy3.currency.Currency cc3Currency = Common.getInstance().getCurrencyManager().getCurrency(currencyTo.getDisplayName());
        try {
            currency.getExchangeRate(cc3Currency);
            return true;
        } catch (NoExchangeRate noExchangeRate) {
            return false;
        }
    }

    @Override
    public int getDefaultFractionDigits() {
        return 2;
    }

    @Override
    public boolean isDefault() {
        return false;
    }
}
