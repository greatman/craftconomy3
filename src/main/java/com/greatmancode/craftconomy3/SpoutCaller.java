package com.greatmancode.craftconomy3;

import org.spout.api.ChatColor;
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
	    str = str.replace("&0", ChatColor.BLACK.toString());
	    str = str.replace("&1", ChatColor.DARK_BLUE.toString());
	    str = str.replace("&2", ChatColor.DARK_GREEN.toString());
	    str = str.replace("&3", ChatColor.DARK_BLUE.toString());
	    str = str.replace("&4", ChatColor.DARK_RED.toString());
	    str = str.replace("&5", ChatColor.PURPLE.toString());
	    str = str.replace("&6", ChatColor.GOLD.toString());
	    str = str.replace("&7", ChatColor.GRAY.toString());
	    str = str.replace("&8", ChatColor.DARK_GRAY.toString());
	    str = str.replace("&9", ChatColor.BLUE.toString());
	    str = str.replace("&a", ChatColor.BRIGHT_GREEN.toString());
	    str = str.replace("&b", ChatColor.BLUE.toString());
	    str = str.replace("&c", ChatColor.RED.toString());
	    str = str.replace("&d", ChatColor.PINK.toString());
	    str = str.replace("&e", ChatColor.YELLOW.toString());
	    str = str.replace("&f", ChatColor.WHITE.toString());
	    return str;
}
}
