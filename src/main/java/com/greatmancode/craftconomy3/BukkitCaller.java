package com.greatmancode.craftconomy3;

import org.bukkit.entity.Player;

public class BukkitCaller implements Caller{

	public void disablePlugin() {
		BukkitLoader.getInstance().getPluginLoader().disablePlugin(BukkitLoader.getInstance());
	}

	public boolean checkPermission(String playerName, String perm) {
		boolean result = false;
		Player p = BukkitLoader.getInstance().getServer().getPlayerExact(playerName);
		if (p != null)
		{
			result = p.hasPermission(perm);
		}
		return result;
	}
	public void sendMessage(String playerName, String message) {
		Player p = BukkitLoader.getInstance().getServer().getPlayer(playerName);
		if (p != null)
		{
			p.sendMessage(message);
		}
	}

	@Override
	public String getPlayerWorld(String playerName) {
		// TODO Auto-generated method stub
		return null;
	}
}
