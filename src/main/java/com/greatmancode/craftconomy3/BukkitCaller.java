package com.greatmancode.craftconomy3;

public class BukkitCaller {

	public static void disablePlugin() {
		BukkitLoader.getInstance().getPluginLoader().disablePlugin(BukkitLoader.getInstance());
	}
}
