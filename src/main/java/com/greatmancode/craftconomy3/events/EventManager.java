/*
 * This file is part of Craftconomy3.
 *
 * Copyright (c) 2011-2014, Greatman <http://github.com/greatman/>
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
package com.greatmancode.craftconomy3.events;

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.tools.events.interfaces.EventHandler;
import com.greatmancode.tools.events.interfaces.Listener;
import com.greatmancode.tools.events.playerEvent.PlayerJoinEvent;
import com.greatmancode.tools.utils.Updater;

/**
 * This class contains code shared for events.
 */
public class EventManager implements Listener {

    /**
     * Event handler for when a player is connecting to the server.
     */
    @EventHandler
    public void playerJoinEvent(PlayerJoinEvent event) {
        if (Common.getInstance().getMainConfig().getBoolean("System.CheckNewVersion") && Common.getInstance().getServerCaller().getPlayerCaller().isOp(event.getPlayer().getName()) && Common.getInstance().getVersionChecker().getResult() == Updater.UpdateResult.UPDATE_AVAILABLE) {
            Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(event.getPlayer().getName(), "{{DARK_CYAN}}Craftconomy is out of date! New version is " + Common.getInstance().getVersionChecker().getLatestName());
        }

        if (Common.getInstance().getMainConfig().getBoolean("System.CreateOnLogin")) {
            Common.getInstance().getAccountManager().getAccount(event.getPlayer().getName());
        }
    }
}
