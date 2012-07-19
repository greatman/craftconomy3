package com.greatmancode.craftconomy3;

import org.spout.api.chat.ChatArguments;
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
			p.sendMessage(ChatArguments.fromString(CHAT_PREFIX + message));
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

	public boolean isOnline(String playerName) {
		return SpoutLoader.getInstance().getEngine().getPlayer(playerName, true) != null;
	}
	public String addColor(String str) {
		//Useless with Spout
		return null;
	}

	@Override
	public boolean worldExist(String worldName) {
		return SpoutLoader.getInstance().getEngine().getWorld(worldName) != null;
	}

	@Override
	public String getDefaultWorld() {
		return SpoutLoader.getInstance().getEngine().getWorlds().iterator().next().getName();
	}
}
