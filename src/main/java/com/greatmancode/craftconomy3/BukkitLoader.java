package com.greatmancode.craftconomy3;

import org.bukkit.plugin.java.JavaPlugin;

import com.greatmancode.craftconomy3.commands.BukkitCommandManager;

public class BukkitLoader extends JavaPlugin {

	private static BukkitLoader instance = null;
	public void onEnable() {
		instance = this;
		new Common(true,getLogger()).initialize();
		this.getCommand("money").setExecutor(new BukkitCommandManager());
	}
	
	public void onDisable() {
		
	}
	
	public static BukkitLoader getInstance() {
		return instance;
	}
}
