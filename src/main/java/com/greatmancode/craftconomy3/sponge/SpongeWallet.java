package com.greatmancode.craftconomy3.sponge;

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.account.Account;
import org.spongepowered.api.service.economy.Wallet;

import java.util.UUID;

/**
 * Created by greatman on 15-08-22.
 */
public class SpongeWallet extends SpongeAccount implements Wallet {

    private UUID uuid;

    public SpongeWallet(UUID uuid, SpongeService spongeService) {
        super(Common.getInstance().getStorageHandler().getStorageEngine().getAccount(uuid), spongeService);
        this.uuid = uuid;
    }

    @Override
    public UUID getOwnerId() {
        return uuid;
    }
}
