package com.greatmancode.craftconomy3;

public interface Caller {

	public void disablePlugin();
	public boolean checkPermission(String playerName, String perm);
	public void sendMessage(String playerName, String message);
	public String getPlayerWorld(String playerName);
	public boolean isOnline(String playerName);
	public String addColor(String message);
}
