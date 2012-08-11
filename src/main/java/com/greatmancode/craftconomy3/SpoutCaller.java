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
import org.spout.api.entity.Player;

import com.greatmancode.craftconomy3.utils.MetricsSpout;
import com.greatmancode.craftconomy3.utils.MetricsSpout.Graph;

/**
 * Server caller for Spout
 * @author greatman
 * 
 */
public class SpoutCaller implements Caller {

	@Override
	public void disablePlugin() {
		CC3SpoutLoader.getInstance().getPluginLoader().disablePlugin(CC3SpoutLoader.getInstance());
	}

	@Override
	public boolean checkPermission(String playerName, String perm) {
		boolean result = false;
		Player p = CC3SpoutLoader.getInstance().getEngine().getPlayer(playerName, true);
		if (p != null) {
			result = p.hasPermission(perm);
		} else {
			// It's the console
			result = true;
		}
		return result;

	}

	@Override
	public void sendMessage(String playerName, String message) {
		Player p = CC3SpoutLoader.getInstance().getEngine().getPlayer(playerName, true);
		if (p != null) {
			p.sendMessage(ChatArguments.fromString(CHAT_PREFIX + message));
		} else {
			Common.getInstance().getLogger().log(Level.INFO, CHAT_PREFIX + message);
		}
	}

	@Override
	public String getPlayerWorld(String playerName) {
		String worldName = "";
		Player p = CC3SpoutLoader.getInstance().getEngine().getPlayer(playerName, true);
		if (p != null) {
			worldName = p.getWorld().getName();
		}
		return worldName;
	}

	@Override
	public boolean isOnline(String playerName) {
		return CC3SpoutLoader.getInstance().getEngine().getPlayer(playerName, true) != null;
	}

	@Override
	public String addColor(String str) {
		// Useless with Spout
		return null;
	}

	@Override
	public boolean worldExist(String worldName) {
		return CC3SpoutLoader.getInstance().getEngine().getWorld(worldName) != null;
	}

	@Override
	public String getDefaultWorld() {
		return CC3SpoutLoader.getInstance().getEngine().getWorlds().iterator().next().getName();
	}

	@Override
	public File getDataFolder() {
		return CC3SpoutLoader.getInstance().getDataFolder();
	}

	@Override
	public void addDbGraph(String dbType) {
		Graph graph = CC3SpoutLoader.getInstance().getMetrics().createGraph("Database Engine");
		graph.addPlotter(new MetricsSpout.Plotter(dbType) {

			@Override
			public int getValue() {
				// TODO Auto-generated method stub
				return 1;
			}
		});
	}

	@Override
	public void addMultiworldGraph(boolean enabled) {
		Graph graph = CC3SpoutLoader.getInstance().getMetrics().createGraph("Multiworld");
		String stringEnabled = "No";
		if (enabled) {
			stringEnabled = "Yes";
		}
		graph.addPlotter(new MetricsSpout.Plotter(stringEnabled) {

			@Override
			public int getValue() {
				// TODO Auto-generated method stub
				return 1;
			}
		});
	}

	@Override
	public void startMetrics() {
		CC3SpoutLoader.getInstance().getMetrics().start();
	}
}
