package com.greatmancode.craftconomy3;

public class SpoutCaller {

	public static void disablePlugin() {
		SpoutLoader.getInstance().getPluginLoader().disablePlugin(SpoutLoader.getInstance());
	}
}
