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
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import com.greatmancode.craftconomy3.commands.interfaces.CommandManager;
import com.greatmancode.craftconomy3.commands.managers.BukkitCommandManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

/**
 * Server caller for Craftbukkit
 * @author greatman
 */
public class BukkitCaller implements Caller {
	private static final long TICK_LENGTH = 20L;
	private final BukkitLoader loader;

	public BukkitCaller(Loader loader) {
		this.loader = (BukkitLoader) loader;
	}

	@Override
	public void disablePlugin() {
		loader.getPluginLoader().disablePlugin(loader);
	}

	@Override
	public boolean checkPermission(String playerName, String perm) {
		boolean result = false;
		Player p = loader.getServer().getPlayerExact(playerName);
		if (p != null) {
			if (p.isOp()) {
				result = true;
			} else {
				result = p.hasPermission(perm);
			}
		} else {
			// It's the console
			result = true;
		}
		return result;
	}

	@Override
	public void sendMessage(String playerName, String message) {
		Player p = loader.getServer().getPlayerExact(playerName);
		if (p != null) {
			p.sendMessage(addColor(CHAT_PREFIX + message));
		} else {
			loader.getServer().getConsoleSender().sendMessage(addColor(CHAT_PREFIX + message));
		}
	}

	@Override
	public String getPlayerWorld(String playerName) {
		String result = "";
		Player p = loader.getServer().getPlayerExact(playerName);
		if (p != null) {
			result = p.getWorld().getName();
		}
		return result;
	}

	@Override
	public boolean isOnline(String playerName) {
		return loader.getServer().getPlayerExact(playerName) != null;
	}

	@Override
	public String addColor(String str) {
		String coloredString = str;
		coloredString = coloredString.replace("{{BLACK}}", ChatColor.BLACK.toString());
		coloredString = coloredString.replace("{{DARK_BLUE}}", ChatColor.DARK_BLUE.toString());
		coloredString = coloredString.replace("{{DARK_GREEN}}", ChatColor.DARK_GREEN.toString());
		coloredString = coloredString.replace("{{DARK_CYAN}}", ChatColor.DARK_AQUA.toString());
		coloredString = coloredString.replace("{{DARK_RED}}", ChatColor.DARK_RED.toString());
		coloredString = coloredString.replace("{{PURPLE}}", ChatColor.DARK_PURPLE.toString());
		coloredString = coloredString.replace("{{GOLD}}", ChatColor.GOLD.toString());
		coloredString = coloredString.replace("{{GRAY}}", ChatColor.GRAY.toString());
		coloredString = coloredString.replace("{{DARK_GRAY}}", ChatColor.DARK_GRAY.toString());
		coloredString = coloredString.replace("{{BLUE}}", ChatColor.BLUE.toString());
		coloredString = coloredString.replace("{{BRIGHT_GREEN}}", ChatColor.GREEN.toString());
		coloredString = coloredString.replace("{{CYAN}}", ChatColor.AQUA.toString());
		coloredString = coloredString.replace("{{RED}}", ChatColor.RED.toString());
		coloredString = coloredString.replace("{{PINK}}", ChatColor.LIGHT_PURPLE.toString());
		coloredString = coloredString.replace("{{YELLOW}}", ChatColor.YELLOW.toString());
		coloredString = coloredString.replace("{{WHITE}}", ChatColor.WHITE.toString());
		return coloredString;
	}

	@Override
	public String getDefaultWorld() {
		return loader.getServer().getWorlds().get(0).getName();
	}

	@Override
	public boolean worldExist(String worldName) {
		return loader.getServer().getWorld(worldName) != null;
	}

	@Override
	public File getDataFolder() {
		return loader.getDataFolder();
	}

	@Override
	public int schedule(Runnable entry, long firstStart, long repeating) {
		return schedule(entry, firstStart, repeating, false);
	}

	@Override
	public int schedule(Runnable entry, long firstStart, long repeating, boolean async) {
		if (!async) {
			return loader.getServer().getScheduler().scheduleSyncRepeatingTask(loader, entry, firstStart * TICK_LENGTH, repeating * TICK_LENGTH);
		} else {
			return loader.getServer().getScheduler().runTaskTimerAsynchronously(loader, entry, firstStart * TICK_LENGTH, repeating * TICK_LENGTH).getTaskId();
		}
	}

	@Override
	public List<String> getOnlinePlayers() {
		List<String> list = new ArrayList<String>();
		Player[] pList = loader.getServer().getOnlinePlayers();
		for (Player p : pList) {
			list.add(p.getName());
		}
		return list;
	}

	@Override
	public void cancelSchedule(int id) {
		loader.getServer().getScheduler().cancelTask(id);
	}

	@Override
	public int delay(Runnable entry, long start) {
		return delay(entry, start, false);
	}

	@Override
	public int delay(Runnable entry, long start, boolean async) {
		if (!async) {
			return loader.getServer().getScheduler().scheduleSyncDelayedTask(loader, entry, start * TICK_LENGTH);
		} else {
			return loader.getServer().getScheduler().runTaskLaterAsynchronously(loader, entry, start * TICK_LENGTH).getTaskId();
		}
	}

	@Override
	public void addCommand(String name, String help, CommandManager manager) {
		if (manager instanceof BukkitCommandManager) {
			loader.getCommand(name).setExecutor((BukkitCommandManager) manager);
		}
	}

	@Override
	public String getServerVersion() {
		return Bukkit.getBukkitVersion();
	}

	@Override
	public String getPluginVersion() {
		return loader.getDescription().getVersion();
	}

	@Override
	public boolean isOp(String playerName) {
		return loader.getServer().getOfflinePlayer(playerName).isOp();
	}

	@Override
	public void loadLibrary(String path) {
		try {
			loader.getPluginClassLoader().addURL(new File(path).toURI().toURL());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			Common.getInstance().sendConsoleMessage(Level.SEVERE, String.format(Common.getInstance().getLanguageManager().getString("invalid_library"), path, e.getMessage()));
		}
	}

	@Override
	public void registerPermission(String permissionNode) {
		if (permissionNode != null) {
			try {
				loader.getServer().getPluginManager().addPermission(new Permission(permissionNode));
			} catch (IllegalArgumentException e) {
				Common.getInstance().sendConsoleMessage(Level.SEVERE, e.getMessage());
			}
		}
	}

	@Override
	public boolean isOnlineMode() {
		return Bukkit.getServer().getOnlineMode();
	}
}
