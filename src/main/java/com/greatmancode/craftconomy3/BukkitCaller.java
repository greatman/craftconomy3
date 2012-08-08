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

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class BukkitCaller implements Caller{

	public void disablePlugin() {
		BukkitLoader.getInstance().getPluginLoader().disablePlugin(BukkitLoader.getInstance());
	}

	public boolean checkPermission(String playerName, String perm) {
		boolean result = false;
		Player p = BukkitLoader.getInstance().getServer().getPlayerExact(playerName);
		if (p != null)
		{
			result = p.hasPermission(perm);
		}
		return result;
	}
	public void sendMessage(String playerName, String message) {
		Player p = BukkitLoader.getInstance().getServer().getPlayerExact(playerName);
		if (p != null)
		{
			p.sendMessage(addColor(CHAT_PREFIX + message));
		}
	}

	public String getPlayerWorld(String playerName) {
		String result = "";
		Player p = BukkitLoader.getInstance().getServer().getPlayerExact(playerName);
		if (p != null)
		{
			result = p.getWorld().getName();
		}
		return result;
	}

	public boolean isOnline(String playerName) {
		return BukkitLoader.getInstance().getServer().getPlayerExact(playerName) != null;
	}

	@Override
	public String addColor(String str) {
		    str = str.replace("{{BLACK}}", ChatColor.BLACK.toString());
		    str = str.replace("{{DARK_BLUE}}", ChatColor.DARK_BLUE.toString());
		    str = str.replace("{{DARK_GREEN}}", ChatColor.DARK_GREEN.toString());
		    str = str.replace("{{DARK_CYAN}}", ChatColor.DARK_AQUA.toString());
		    str = str.replace("{{DARK_RED}}", ChatColor.DARK_RED.toString());
		    str = str.replace("{{PURPLE}}", ChatColor.DARK_PURPLE.toString());
		    str = str.replace("{{GOLD}}", ChatColor.GOLD.toString());
		    str = str.replace("{{GRAY}}", ChatColor.GRAY.toString());
		    str = str.replace("{{DARK_GRAY}}", ChatColor.DARK_GRAY.toString());
		    str = str.replace("{{BLUE}}", ChatColor.BLUE.toString());
		    str = str.replace("{{BRIGHT_GREEN}}", ChatColor.GREEN.toString());
		    str = str.replace("{{CYAN}}", ChatColor.AQUA.toString());
		    str = str.replace("{{RED}}", ChatColor.RED.toString());
		    str = str.replace("{{PINK}}", ChatColor.LIGHT_PURPLE.toString());
		    str = str.replace("{{YELLOW}}", ChatColor.YELLOW.toString());
		    str = str.replace("{{WHITE}}", ChatColor.WHITE.toString());
		    return str;
	}
	
	public String getDefaultWorld() {
		return BukkitLoader.getInstance().getServer().getWorlds().get(0).getName();
	}

	@Override
	public boolean worldExist(String worldName) {
		return BukkitLoader.getInstance().getServer().getWorld(worldName) != null;
	}

	@Override
	public File getDataFolder() {
		return BukkitLoader.getInstance().getDataFolder();
	}
	
	
}
