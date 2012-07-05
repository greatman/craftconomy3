package com.greatmancode.craftconomy3.configuration;

import java.io.File;

import com.greatmancode.craftconomy3.BukkitLoader;
import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.SpoutLoader;

public class ConfigurationManager {

	private Config config = null;

	private File dataFolder = null;
	
	public ConfigurationManager() {
		if (Common.isBukkit()) {
			config = new BukkitConfig();
			dataFolder = BukkitLoader.getInstance().getDataFolder();
		} else {
			config = new SpoutConfig();
			dataFolder = SpoutLoader.getInstance().getDataFolder();
		}
		
	}

	public Config getConfig() {
		return config;
	}
	
	public File getDataFolder() {
		return dataFolder;
	}
}
