package com.greatmancode.craftconomy3;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

import com.greatmancode.craftconomy3.commands.interfaces.CommandManager;

public class ForgeCaller implements Caller {

	private ForgeLoader loader;
	public ForgeCaller(Loader loader) {
		this.loader = (ForgeLoader) loader;
	}
	@Override
	public void disablePlugin() {
		// TODO: Can't on Forge I beleive

	}

	@Override
	public boolean checkPermission(String playerName, String perm) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void sendMessage(String playerName, String message) {
		List players = MinecraftServer.getServer().getConfigurationManager().playerEntityList;

        for (Object o : players)
        {
            EntityPlayer p = (EntityPlayer)o;

            if (p.getEntityName().equalsIgnoreCase(playerName))
            {
                p.addChatMessage(addColor(CHAT_PREFIX + message));
                break;
            }
        }
	}

	@Override
	public String getPlayerWorld(String playerName) {
		String worldName = "";
		List players = MinecraftServer.getServer().getConfigurationManager().playerEntityList;

        for (Object o : players)
        {
            EntityPlayer p = (EntityPlayer)o;

            if (p.getEntityName().equalsIgnoreCase(playerName))
            {
               worldName = p.worldObj.getWorldInfo().getWorldName();
               break;
            }
        }
        return worldName;
	}

	@Override
	public boolean isOnline(String playerName) {
		boolean result = false;
		List players = MinecraftServer.getServer().getConfigurationManager().playerEntityList;

        for (Object o : players)
        {
            EntityPlayer p = (EntityPlayer)o;

            if (p.getEntityName().equalsIgnoreCase(playerName))
            {
               result = true;
               break;
            }
        }
        return result;
	}

	@Override
	public String addColor(String message) {
		String coloredString = message;
		coloredString = coloredString.replace("{{BLACK}}", '\u00A7' + "1");
		coloredString = coloredString.replace("{{DARK_BLUE}}", '\u00A7' + "2");
		coloredString = coloredString.replace("{{DARK_GREEN}}", '\u00A7' + "3");
		coloredString = coloredString.replace("{{DARK_CYAN}}", '\u00A7' + "4");
		coloredString = coloredString.replace("{{DARK_RED}}", '\u00A7' + "5");
		coloredString = coloredString.replace("{{PURPLE}}", '\u00A7' + "6");
		coloredString = coloredString.replace("{{GOLD}}", '\u00A7' + "7");
		coloredString = coloredString.replace("{{GRAY}}", '\u00A7' + "8");
		coloredString = coloredString.replace("{{DARK_GRAY}}", '\u00A7' + "9");
		coloredString = coloredString.replace("{{BLUE}}", '\u00A7' + "0");
		coloredString = coloredString.replace("{{BRIGHT_GREEN}}", '\u00A7' + "a");
		coloredString = coloredString.replace("{{CYAN}}", '\u00A7' + "b");
		coloredString = coloredString.replace("{{RED}}", '\u00A7' + "c");
		coloredString = coloredString.replace("{{PINK}}", '\u00A7' + "d");
		coloredString = coloredString.replace("{{YELLOW}}", '\u00A7' + "e");
		coloredString = coloredString.replace("{{WHITE}}", '\u00A7' + "f");
		return coloredString;
	}

	@Override
	public boolean worldExist(String worldName) {
		return MinecraftServer.getServer().getWorldName().equals(worldName);
	}

	@Override
	public String getDefaultWorld() {
		return MinecraftServer.getServer().getWorldName();
	}

	@Override
	public File getDataFolder() {
		return new File(new File(ForgeCaller.class.getProtectionDomain().getCodeSource().getLocation().getPath(), "plugins"), "craftconomy3");
	}

	@Override
	public int schedule(Runnable entry, long firstStart, long repeating) {
		// TODO Ehhhhhh
		return 0;
	}

	@Override
	public int schedule(Runnable entry, long firstStart, long repeating, boolean async) {
		// TODO Ehhhhhh
		return 0;
	}

	@Override
	public void cancelSchedule(int id) {
		// TODO Ehhhhhh

	}

	@Override
	public int delay(Runnable entry, long start) {
		// TODO Ehhhhhh
		return 0;
	}

	@Override
	public int delay(Runnable entry, long start, boolean async) {
		// TODO Ehhhhhh
		return 0;
	}

	@Override
	public List<String> getOnlinePlayers() {
		List<String> playerList = new ArrayList<String>();
		List players = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
		
        for (Object o : players)
        {
        	playerList.add(((EntityPlayer)o).getEntityName());
        }
		return playerList;
	}

	@Override
	public void addCommand(String name, String help, CommandManager manager) {
		// TODO Auto-generated method stub
		ICommandManager cmdManager = MinecraftServer.getServer().getCommandManager();
		if (cmdManager instanceof ServerCommandManager) {
			ServerCommandManager scm = (ServerCommandManager) cmdManager;
			scm.registerCommand((ICommand) manager);
		}
	}

	@Override
	public String getServerVersion() {
		return "MinecraftForge (MC: " + MinecraftServer.getServer().getMinecraftVersion() + ")";
	}

	@Override
	public String getPluginVersion() {
		return "${version} b${BUILD_NUMBER}";
	}

	@Override
	public boolean isOp(String playerName) {
		boolean result = false;

        if (MinecraftServer.getServer().getConfigurationManager().getOps().contains(playerName))
        {
        	result = true;
        }
        return result;
	}

	@Override
	public void loadLibrary(String path) {
		// TODO Ehhhh
	}

	@Override
	public void registerPermission(String permissionNode) {
		// TODO No such things as permission?
		
	}

	@Override
	public boolean isOnlineMode() {
		return MinecraftServer.getServer().isServerInOnlineMode();
	}

}
