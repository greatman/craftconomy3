package com.greatmancode.craftconomy3;

import org.spout.api.chat.style.ChatStyle;
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
			p.sendMessage(addColor("&a[&fMoney&a]&f" + message));
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
	    str = str.replace("&0", ChatStyle.BLACK.toString());
	    str = str.replace("&1", ChatStyle.DARK_BLUE.toString());
	    str = str.replace("&2", ChatStyle.DARK_GREEN.toString());
	    str = str.replace("&3", ChatStyle.DARK_BLUE.toString());
	    str = str.replace("&4", ChatStyle.DARK_RED.toString());
	    str = str.replace("&5", ChatStyle.PURPLE.toString());
	    str = str.replace("&6", ChatStyle.GOLD.toString());
	    str = str.replace("&7", ChatStyle.GRAY.toString());
	    str = str.replace("&8", ChatStyle.DARK_GRAY.toString());
	    str = str.replace("&9", ChatStyle.BLUE.toString());
	    str = str.replace("&a", ChatStyle.BRIGHT_GREEN.toString());
	    str = str.replace("&b", ChatStyle.BLUE.toString());
	    str = str.replace("&c", ChatStyle.RED.toString());
	    str = str.replace("&d", ChatStyle.PINK.toString());
	    str = str.replace("&e", ChatStyle.YELLOW.toString());
	    str = str.replace("&f", ChatStyle.WHITE.toString());
	    return str;
}
}
