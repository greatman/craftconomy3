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
import java.util.concurrent.TimeUnit;

import org.spout.api.chat.ChatArguments;
import org.spout.api.entity.Player;
import org.spout.api.scheduler.TaskPriority;
import org.spout.api.Server;
import org.spout.api.Spout;

import com.greatmancode.craftconomy3.commands.CommandManager;
import com.greatmancode.craftconomy3.commands.SpoutCommandManager;

/**
 * Server caller for Spout
 * 
 * @author greatman
 * 
 */
public class SpoutCaller implements Caller {

	private SpoutLoader loader;

	public SpoutCaller(Loader loader) {
		this.loader = (SpoutLoader) loader;
	}

	@Override
	public void disablePlugin() {
		loader.getPluginLoader().disablePlugin(loader);
	}

	@Override
	public boolean checkPermission(String playerName, String perm) {
		boolean result = false;
		Player p = ((Server) loader.getEngine()).getPlayer(playerName, true);
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
		Player p = ((Server) loader.getEngine()).getPlayer(playerName, true);
		if (p != null) {
			p.sendMessage(ChatArguments.fromFormatString(CHAT_PREFIX + message));
		} else {
			loader.getEngine().getCommandSource().sendMessage(ChatArguments.fromFormatString(CHAT_PREFIX + message));
		}
	}

	@Override
	public String getPlayerWorld(String playerName) {
		String worldName = "";
		Player p = ((Server) loader.getEngine()).getPlayer(playerName, true);
		if (p != null) {
			worldName = p.getWorld().getName();
		}
		return worldName;
	}

	@Override
	public boolean isOnline(String playerName) {
		return ((Server) loader.getEngine()).getPlayer(playerName, true) != null;
	}

	@Override
	public String addColor(String str) {
		// Useless with Spout
		return null;
	}

	@Override
	public boolean worldExist(String worldName) {
		return loader.getEngine().getWorld(worldName) != null;
	}

	@Override
	public String getDefaultWorld() {
		return loader.getEngine().getWorlds().iterator().next().getName();
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
			return loader.getEngine().getScheduler().scheduleSyncRepeatingTask(loader, entry, TimeUnit.MILLISECONDS.convert(firstStart, TimeUnit.SECONDS), TimeUnit.MILLISECONDS.convert(repeating, TimeUnit.SECONDS), TaskPriority.NORMAL);
		} else {
			return loader.getEngine().getScheduler().scheduleAsyncRepeatingTask(loader, entry, TimeUnit.MILLISECONDS.convert(firstStart, TimeUnit.SECONDS), TimeUnit.MILLISECONDS.convert(repeating, TimeUnit.SECONDS), TaskPriority.NORMAL);
		}
	}

	@Override
	public List<String> getOnlinePlayers() {
		List<String> list = new ArrayList<String>();
		Player[] pList = ((Server) loader.getEngine()).getOnlinePlayers();
		for (Player p : pList) {
			list.add(p.getName());
		}
		return list;
	}

	@Override
	public void cancelSchedule(int id) {
		loader.getEngine().getScheduler().cancelTask(id);
	}

	@Override
	public int delay(Runnable entry, long start) {
		return delay(entry, start, false);
	}

	@Override
	public int delay(Runnable entry, long start, boolean async) {
		if (!async) {
			return loader.getEngine().getScheduler().scheduleSyncDelayedTask(loader, entry, TimeUnit.MILLISECONDS.convert(start, TimeUnit.SECONDS), TaskPriority.NORMAL);
		} else {
			return loader.getEngine().getScheduler().scheduleAsyncDelayedTask(loader, entry, TimeUnit.MILLISECONDS.convert(start, TimeUnit.SECONDS), TaskPriority.NORMAL);
		}
	}

	@Override
	public void addCommand(String name, String help, CommandManager manager) {
		if (manager instanceof SpoutCommandManager) {
			loader.getEngine().getRootCommand().addSubCommand(loader, name).setHelp(help).setExecutor((SpoutCommandManager) manager);
		}

	}

	@Override
	public String getServerVersion() {
		return "SpoutServer " + Spout.getAPIVersion();
	}

	@Override
	public String getPluginVersion() {
		return loader.getDescription().getVersion();
	}

	@Override
	public boolean isOp(String playerName) {
		// TODO: Hmm... There's not really a OP in Spout. Maybe use a permission flag?
		return false;
	}

	@Override
	public void loadLibrary(String path) {
		loader.loadLibrary(new File(path));
	}

	@Override
	public void registerPermission(String permissionNode) {
		//TODO: Not needed on spout?
	}
}
