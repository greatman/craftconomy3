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

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.PluginClassLoader;

import com.greatmancode.craftconomy3.events.BukkitListener;

/**
 * Class used when the plugin is loaded from Craftbukkit
 * @author greatman
 * 
 */
public class BukkitLoader extends JavaPlugin implements Loader{

	public void onEnable() {
		new Common(this, getLogger()).initialize();
		this.getServer().getPluginManager().registerEvents(new BukkitListener(), this);
	}

	public void onDisable() {
		Common.getInstance().disable();
	}
	
	/**
	 * Retrieve the PluginClassLoader of Bukkit
	 * @return The PluginClassLoader of Bukkit
	 */
	public PluginClassLoader getPluginClassLoader() {
		return (PluginClassLoader) this.getClassLoader();
	}

	@Override
	public ServerType getServerType() {
		return ServerType.BUKKIT;
	}
}
