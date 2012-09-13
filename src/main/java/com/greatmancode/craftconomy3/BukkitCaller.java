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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.greatmancode.craftconomy3.utils.MetricsBukkit;
import com.greatmancode.craftconomy3.utils.MetricsBukkit.Graph;

/**
 * Server caller for Craftbukkit
 * @author greatman
 * 
 */
public class BukkitCaller implements Caller {

	@Override
	public void disablePlugin() {
		CC3BukkitLoader.getInstance().getPluginLoader().disablePlugin(CC3BukkitLoader.getInstance());
	}

	@Override
	public boolean checkPermission(String playerName, String perm) {
		boolean result = false;
		Player p = CC3BukkitLoader.getInstance().getServer().getPlayerExact(playerName);
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
		Player p = CC3BukkitLoader.getInstance().getServer().getPlayerExact(playerName);
		if (p != null) {
			p.sendMessage(addColor(CHAT_PREFIX + message));
		} else {
			Common.getInstance().getLogger().log(Level.INFO, addColor(CHAT_PREFIX + message));
		}
	}

	@Override
	public String getPlayerWorld(String playerName) {
		String result = "";
		Player p = CC3BukkitLoader.getInstance().getServer().getPlayerExact(playerName);
		if (p != null) {
			result = p.getWorld().getName();
		}
		return result;
	}

	@Override
	public boolean isOnline(String playerName) {
		return CC3BukkitLoader.getInstance().getServer().getPlayerExact(playerName) != null;
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

	@Override
	public String getDefaultWorld() {
		return CC3BukkitLoader.getInstance().getServer().getWorlds().get(0).getName();
	}

	@Override
	public boolean worldExist(String worldName) {
		return CC3BukkitLoader.getInstance().getServer().getWorld(worldName) != null;
	}

	@Override
	public File getDataFolder() {
		return CC3BukkitLoader.getInstance().getDataFolder();
	}

	@Override
	public void addDbGraph(String dbType) {
		Graph graph = CC3BukkitLoader.getInstance().getMetrics().createGraph("Database Engine");
		graph.addPlotter(new MetricsBukkit.Plotter(dbType) {

			@Override
			public int getValue() {
				return 1;
			}
		});
	}

	@Override
	public void addMultiworldGraph(boolean enabled) {
		Graph graph = CC3BukkitLoader.getInstance().getMetrics().createGraph("Multiworld");
		String stringEnabled = "No";
		if (enabled) {
			stringEnabled = "Yes";
		}
		graph.addPlotter(new MetricsBukkit.Plotter(stringEnabled) {

			@Override
			public int getValue() {
				return 1;
			}
		});
	}

	@Override
	public void startMetrics() {
		CC3BukkitLoader.getInstance().getMetrics().start();
	}

	@Override
	public int schedule(Runnable entry, long firstStart, long repeating) {
		return schedule(entry, firstStart, repeating, false);
	}

	@Override
	public int schedule(Runnable entry, long firstStart, long repeating, boolean async) {
		if(!async)
			return CC3BukkitLoader.getInstance().getServer().getScheduler().scheduleSyncRepeatingTask(CC3BukkitLoader.getInstance(), entry, firstStart * 20L, repeating * 20L);
		else
			return CC3BukkitLoader.getInstance().getServer().getScheduler().scheduleAsyncRepeatingTask(CC3BukkitLoader.getInstance(), entry, firstStart * 20L, repeating * 20L);
	}

	@Override
	public List<String> getOnlinePlayers() {
		List<String> list = new ArrayList<String>();
		Player[] pList = CC3BukkitLoader.getInstance().getServer().getOnlinePlayers();
		for (Player p: pList) {
			list.add(p.getName());
		}
		return list;
	}

	@Override
	public void cancelSchedule(int id) {
		CC3BukkitLoader.getInstance().getServer().getScheduler().cancelTask(id);
	}

	@Override
	public int delay(Runnable entry, long start) {
		return delay(entry, start, false);
	}

	@Override
	public int delay(Runnable entry, long start, boolean async) {
		if(!async)
			return CC3BukkitLoader.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(CC3BukkitLoader.getInstance(), entry, start * 20L);
		else
			return CC3BukkitLoader.getInstance().getServer().getScheduler().scheduleAsyncDelayedTask(CC3BukkitLoader.getInstance(), entry, start * 20L);
	}

}
