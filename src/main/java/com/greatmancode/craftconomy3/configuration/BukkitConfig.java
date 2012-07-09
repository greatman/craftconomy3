package com.greatmancode.craftconomy3.configuration;

import java.io.File;

import com.greatmancode.craftconomy3.BukkitLoader;
import com.greatmancode.craftconomy3.Common;

public class BukkitConfig extends Config {

	public BukkitConfig() {
		File file = new File(Common.getInstance().getConfigurationManager().getDataFolder(), "config.yml");
		if (!file.exists())
		{
			BukkitLoader.getInstance().getConfig().options().copyDefaults(true);
			BukkitLoader.getInstance().saveConfig();
		}
	}
	@Override
	public int getInt(String path) {
		return BukkitLoader.getInstance().getConfig().getInt(path);
	}

	@Override
	public long getLong(String path) {
		return BukkitLoader.getInstance().getConfig().getLong(path);
	}

	@Override
	public double getDouble(String path) {
		return BukkitLoader.getInstance().getConfig().getDouble(path);
	}

	@Override
	public String getString(String path) {
		return BukkitLoader.getInstance().getConfig().getString(path);
	}
	@Override
	public boolean getBoolean(String path) {
		// TODO Auto-generated method stub
		return BukkitLoader.getInstance().getConfig().getBoolean(path);
	}

}
