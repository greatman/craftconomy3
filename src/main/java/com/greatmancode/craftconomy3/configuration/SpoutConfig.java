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
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.spout.api.exception.ConfigurationException;
import org.spout.api.util.config.yaml.YamlConfiguration;

import com.greatmancode.craftconomy3.Common;

public class SpoutConfig extends Config {

	private YamlConfiguration config = null;

	public SpoutConfig() {
		File file = new File(Common.getInstance().getServerCaller().getDataFolder(), "config.yml");
		if (!file.exists()) {
			URL inputURL = getClass().getResource("/config.yml");
			try {
				FileUtils.copyURLToFile(inputURL, file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		config = new YamlConfiguration(file);
		try {
			config.load();
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int getInt(String path) {
		return config.getNode(path).getInt();
	}

	@Override
	public long getLong(String path) {
		return config.getNode(path).getLong();
	}

	@Override
	public double getDouble(String path) {
		return config.getNode(path).getDouble();
	}

	@Override
	public String getString(String path) {
		return config.getNode(path).getString();
	}

	@Override
	public boolean getBoolean(String path) {
		return config.getNode(path).getBoolean();
	}
	
	@Override
	public void setValue(String path, Object value) {
		config.getNode(path).setValue(value);
		try {
			config.save();
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}

}
