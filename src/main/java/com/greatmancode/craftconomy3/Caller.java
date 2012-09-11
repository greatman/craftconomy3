/*
 * This file is part of Craftconomy3.
 *
 * Copyright (c) 2011-2012, Greatman <http://github.com/greatman/>
 *
 * Craftconomy3 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Craftconomy3 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Craftconomy3.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.greatmancode.craftconomy3;

import java.io.File;
import java.util.List;

/**
 * Represents a server Caller
 * @author greatman
 * 
 */
public interface Caller {

	public static final String CHAT_PREFIX = "{{DARK_GREEN}}[{{WHITE}}Money{{DARK_GREEN}}]{{WHITE}} ";

	/**
	 * Disable the plugin
	 */
	public void disablePlugin();

	/**
	 * Check the permissions of a player
	 * @param playerName The player name to check
	 * @param perm The permission node to check
	 * @return True if the player have the permission. Else false (Always true for the Console)
	 */
	public boolean checkPermission(String playerName, String perm);

	/**
	 * Sends a message to a player
	 * @param playerName The player name to send the message
	 * @param message The message to send
	 */
	public void sendMessage(String playerName, String message);

	/**
	 * Retrieve the world name that a player is currently in
	 * @param playerName The player name to retrieve the world
	 * @return The world name the player is currently in. Returns "" when the player is offline
	 */
	public String getPlayerWorld(String playerName);

	/**
	 * Checks if a player is online
	 * @param playerName The player name
	 * @return True if the player is online. Else false.
	 */
	public boolean isOnline(String playerName);

	/**
	 * Add color in a message
	 * @param message The message to add color in
	 * @return The message with colors.
	 */
	public String addColor(String message);

	/**
	 * Checks if a world exist.
	 * @param worldName The world name to check
	 * @return True if the world exist. Else false.
	 */
	public boolean worldExist(String worldName);

	/**
	 * Retrieve the default world of the server
	 * @return The default world name
	 */
	public String getDefaultWorld();

	/**
	 * Get the data folder (Aka. the plugin folder)
	 * @return The data folder
	 */
	public File getDataFolder();

	/**
	 * Add a entry in the DB Metrics graph
	 * @param dbName The name of the database system
	 */
	public void addDbGraph(String dbName);

	/**
	 * Add a entry in the Multiworld Metrics graph
	 * @param enabled True if multiworld is enabled else false.
	 */
	public void addMultiworldGraph(boolean enabled);

	/**
	 * Starts Metrics
	 */
	public void startMetrics();
	
	/**
	 * Schedule something to be run each X seconds.
	 * @param entry the runnable class
	 * @param firstStart When we should run this class first?
	 * @param repeating What is the interval to be run at? (In seconds)
	 */
	public int schedule(Runnable entry, long firstStart, long repeating);

	public int schedule(Runnable entry, long firstStart, long repeating, boolean async);
	
	public void cancelSchedule(int id);

	public int delay(Runnable entry, long start);
	
	public int delay(Runnable entry, long start, boolean async);
	
	public List<String> getOnlinePlayers();
}
