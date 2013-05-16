/*
 * This file is part of Craftconomy3.
 *
 * Copyright (c) 2011-2013, Greatman <http://github.com/greatman/>
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

import com.greatmancode.craftconomy3.events.BukkitListener;
import com.greatmancode.tools.ServerType;
import com.greatmancode.tools.interfaces.Loader;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.PluginClassLoader;

/**
 * Class used when the plugin is loaded from Craftbukkit
 * @author greatman
 */
public class BukkitLoader extends com.greatmancode.tools.interfaces.BukkitLoader {

	/**
	 * Called when the plugin is loaded.
	 */
	public void onEnable() {
		new Common(this, getLogger()).onEnable();
		if (Common.isInitialized()) {
			this.getServer().getPluginManager().registerEvents(new BukkitListener(), this);
		}
	}

	/**
	 * Called when the plugin is unloaded.
	 */
	public void onDisable() {
		Common.getInstance().onDisable();
	}

	@Override
	public ServerType getServerType() {
		return ServerType.BUKKIT;
	}
}
