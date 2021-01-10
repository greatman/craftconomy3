package com.greatmancode.craftconomy3.events;

import com.greatmancode.craftconomy3.account.Account;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PayAccountEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    boolean cancelled = false;
    Account fromAccount;
    Account toAccount;
    double payAmount;

    public PayAccountEvent(Account from, Account to, double amount) {
        fromAccount = from;
        toAccount = to;
        payAmount = amount;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public Account getFromAccount() {
        return fromAccount;
    }

    public Account getToAccount() {
        return toAccount;
    }

    public double getPayAmount() {
        return payAmount;
    }
}
