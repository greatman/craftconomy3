package com.greatmancode.craftconomy3;

import org.spout.api.player.Player;

public class SpoutCaller implements Caller{

	public void disablePlugin() {
		SpoutLoader.getInstance().getPluginLoader().disablePlugin(SpoutLoader.getInstance());
	}
	
	public boolean checkPermission(String playerName, String perm) {
		boolean result = false;
		Player p = SpoutLoader.getInstance().getEngine().getPlayer(playerName, true);
		if (p != null)
		{
			result = p.hasPermission(perm);
		}
		return result;
		
	}
	
	public void sendMessage(String playerName, String message) {
		Player p = SpoutLoader.getInstance().getEngine().getPlayer(playerName, true);
		if (p != null)
		{
			p.sendMessage(message);
		}
	}
	
	public String getPlayerWorld(String playerName) {
		String worldName = "";
		Player p = SpoutLoader.getInstance().getEngine().getPlayer(playerName, true);
		if (p != null)
		{
			worldName = p.getEntity().getWorld().getName();
		}
		return worldName;
	}
}
