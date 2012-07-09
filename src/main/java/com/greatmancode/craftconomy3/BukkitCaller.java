package com.greatmancode.craftconomy3;

import org.bukkit.ChatColor;
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
		Player p = BukkitLoader.getInstance().getServer().getPlayerExact(playerName);
		if (p != null)
		{
			p.sendMessage(addColor("&a[&fMoney&a]&f" + message));
		}
	}

	public String getPlayerWorld(String playerName) {
		String result = "";
		Player p = BukkitLoader.getInstance().getServer().getPlayerExact(playerName);
		if (p != null)
		{
			result = p.getWorld().getName();
		}
		return result;
	}

	public boolean isOnline(String playerName) {
		return BukkitLoader.getInstance().getServer().getPlayerExact(playerName) != null;
	}

	@Override
	public String addColor(String str) {
		    str = str.replace("&0", ChatColor.BLACK.toString());
		    str = str.replace("&1", ChatColor.DARK_BLUE.toString());
		    str = str.replace("&2", ChatColor.DARK_GREEN.toString());
		    str = str.replace("&3", ChatColor.DARK_AQUA.toString());
		    str = str.replace("&4", ChatColor.DARK_RED.toString());
		    str = str.replace("&5", ChatColor.DARK_PURPLE.toString());
		    str = str.replace("&6", ChatColor.GOLD.toString());
		    str = str.replace("&7", ChatColor.GRAY.toString());
		    str = str.replace("&8", ChatColor.DARK_GRAY.toString());
		    str = str.replace("&9", ChatColor.BLUE.toString());
		    str = str.replace("&a", ChatColor.GREEN.toString());
		    str = str.replace("&b", ChatColor.AQUA.toString());
		    str = str.replace("&c", ChatColor.RED.toString());
		    str = str.replace("&d", ChatColor.LIGHT_PURPLE.toString());
		    str = str.replace("&e", ChatColor.YELLOW.toString());
		    str = str.replace("&f", ChatColor.WHITE.toString());
		    return str;
	}
}
