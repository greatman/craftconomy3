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
package com.greatmancode.craftconomy3.events;

import com.greatmancode.craftconomy3.Common;

public class EventManager {
	/**
	 * Event handler for when a player is connecting to the server.
	 * @param playerName The player name.
	 */
	public void playerJoinEvent(String playerName) {
		if (Common.getInstance().getConfigurationManager().getConfig().getBoolean("System.CheckNewVersion") && Common.getInstance().getServerCaller().isOp(playerName) && Common.getInstance().getVersionChecker().isOld()) {
			Common.getInstance().getServerCaller().sendMessage(playerName, "{{DARK_CYAN}}Craftconomy is out of date! New version is " + Common.getInstance().getVersionChecker().getNewVersion());
		}
	}
}
