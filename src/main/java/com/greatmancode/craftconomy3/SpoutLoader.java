package com.greatmancode.craftconomy3;

import org.spout.api.plugin.CommonPlugin;

public class SpoutLoader extends CommonPlugin {

	private static SpoutLoader instance = null;
	@Override
	public void onEnable() {
		instance = this;
		new Common(false);
	}

	@Override
	public void onDisable() {

	}
	
	public static SpoutLoader getInstance() {
		return instance;
	}

}
