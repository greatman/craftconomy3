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
package com.greatmancode.craftconomy3;

import java.io.File;
import java.util.logging.Level;

import org.spout.api.chat.ChatArguments;
import org.spout.api.player.Player;

public class SpoutCaller implements Caller{

	public void disablePlugin() {
		SpoutLoader.getInstance().getPluginLoader().disablePlugin(SpoutLoader.getInstance());
	}
	
	public boolean checkPermission(String playerName, String perm) {
		boolean result = false;
		Player p = SpoutLoader.getInstance().getEngine().getPlayer(playerName, true);
		if (p != null)
		{
			result = p.hasPermission(perm);
		} else {
			//It's the console
			result = true;
		}
		return result;
		
	}
	
	public void sendMessage(String playerName, String message) {
		Player p = SpoutLoader.getInstance().getEngine().getPlayer(playerName, true);
		if (p != null)
		{
			p.sendMessage(ChatArguments.fromString(CHAT_PREFIX + message));
		} else {
			Common.getInstance().getLogger().log(Level.INFO, CHAT_PREFIX + message);
		}
	}
	
	public String getPlayerWorld(String playerName) {
		String worldName = "";
		Player p = SpoutLoader.getInstance().getEngine().getPlayer(playerName, true);
		if (p != null)
		{
			worldName = p.getWorld().getName();
		}
		return worldName;
	}

	public boolean isOnline(String playerName) {
		return SpoutLoader.getInstance().getEngine().getPlayer(playerName, true) != null;
	}
	public String addColor(String str) {
		//Useless with Spout
		return null;
	}

	@Override
	public boolean worldExist(String worldName) {
		return SpoutLoader.getInstance().getEngine().getWorld(worldName) != null;
	}

	@Override
	public String getDefaultWorld() {
		return SpoutLoader.getInstance().getEngine().getWorlds().iterator().next().getName();
	}

	@Override
	public File getDataFolder() {
		return SpoutLoader.getInstance().getDataFolder();
	}
}
