package com.greatmancode.craftconomy3.commands;

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.currency.Currency;
import com.greatmancode.tools.commands.CommandSender;
import com.greatmancode.tools.commands.interfaces.CommandExecutor;
import org.bukkit.World;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 13/12/2018.
 */
public abstract class AbstractCommand extends CommandExecutor {

    public AbstractCommand(String name) {
        super(name);
    }

    protected void sendMessage(CommandSender sender, String message){
        Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender,message,getName());
    }

    protected Currency checkCurrencyExists(CommandSender sender, String currencyName){
        if (Common.getInstance().getCurrencyManager().getCurrency(currencyName) != null) {
            return Common.getInstance().getCurrencyManager().getCurrency(currencyName);
        } else {
            sendMessage(sender, Common.getInstance().getLanguageManager().getString("currency_not_exist"));
            return null;
        }
    }
    protected boolean checkWorldExists(CommandSender sender, String worldName){
        if (!Common.getInstance().getServerCaller().worldExist(worldName)){
            sendMessage(sender, Common.getInstance().getLanguageManager().getString("world_not_exist"));
            return false;
        }
        return true;
    }
}
