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
package com.greatmancode.craftconomy3.configuration;

import java.io.File;
import java.io.IOException;

import com.greatmancode.craftconomy3.Common;

import org.bukkit.configuration.file.YamlConfiguration;

public class BukkitConfig extends Config {
	private final YamlConfiguration configFile;
	private final File file;

	public BukkitConfig(File folder, String fileName) {
		file = new File(folder, fileName);

		if (!file.exists()) {
			initializeConfig(file, fileName);
		}
		configFile = YamlConfiguration.loadConfiguration(file);
	}

	@Override
	public int getInt(String path) {
		return configFile.getInt(path);
	}

	@Override
	public long getLong(String path) {
		return configFile.getLong(path);
	}

	@Override
	public double getDouble(String path) {
		return configFile.getDouble(path);
	}

	@Override
	public String getString(String path) {
		return configFile.getString(path);
	}

	@Override
	public boolean getBoolean(String path) {
		return configFile.getBoolean(path);
	}

	@Override
	public void setValue(String path, Object value) {
		configFile.set(path, value);
		try {
			configFile.save(file);
		} catch (IOException e) {
			Common.getInstance().getLogger().severe("Error while saving + " + file.getName() + ". Error: " + e.getMessage());
		}
	}

	@Override
	public boolean has(String path) {
		return configFile.contains(path);
	}
}
