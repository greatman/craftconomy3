package com.greatmancode.craftconomy3.configuration;

import java.io.File;

import com.greatmancode.craftconomy3.BukkitLoader;
import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.SpoutLoader;

public class ConfigurationManager {

	private Config config = null;

	private File dataFolder = null;
	
	public ConfigurationManager() {
		
		
	}

	public void initialize() {
		if (Common.isBukkit()) {
			dataFolder = BukkitLoader.getInstance().getDataFolder();
			config = new BukkitConfig();
		} else {
			dataFolder = SpoutLoader.getInstance().getDataFolder();
			config = new SpoutConfig();
		}
	}
	public Config getConfig() {
		return config;
	}
	
	public File getDataFolder() {
		return dataFolder;
	}
}
