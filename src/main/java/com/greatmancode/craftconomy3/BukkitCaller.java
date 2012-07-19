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
			p.sendMessage(addColor(CHAT_PREFIX + message));
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
		    str = str.replace("{{BLACK}}", ChatColor.BLACK.toString());
		    str = str.replace("{{DARK_BLUE}}", ChatColor.DARK_BLUE.toString());
		    str = str.replace("{{DARK_GREEN}}", ChatColor.DARK_GREEN.toString());
		    str = str.replace("{{DARK_CYAN}}", ChatColor.DARK_AQUA.toString());
		    str = str.replace("{{DARK_RED}}", ChatColor.DARK_RED.toString());
		    str = str.replace("{{PURPLE}}", ChatColor.DARK_PURPLE.toString());
		    str = str.replace("{{GOLD}}", ChatColor.GOLD.toString());
		    str = str.replace("{{GRAY}}", ChatColor.GRAY.toString());
		    str = str.replace("{{DARK_GRAY}}", ChatColor.DARK_GRAY.toString());
		    str = str.replace("{{BLUE}}", ChatColor.BLUE.toString());
		    str = str.replace("{{BRIGHT_GREEN}}", ChatColor.GREEN.toString());
		    str = str.replace("{{CYAN}}", ChatColor.AQUA.toString());
		    str = str.replace("{{RED}}", ChatColor.RED.toString());
		    str = str.replace("{{PINK}}", ChatColor.LIGHT_PURPLE.toString());
		    str = str.replace("{{YELLOW}}", ChatColor.YELLOW.toString());
		    str = str.replace("{{WHITE}}", ChatColor.WHITE.toString());
		    return str;
	}
	
	public String getDefaultWorld() {
		return BukkitLoader.getInstance().getServer().getWorlds().get(0).getName();
	}

	@Override
	public boolean worldExist(String worldName) {
		return BukkitLoader.getInstance().getServer().getWorld(worldName) != null;
	}
}
