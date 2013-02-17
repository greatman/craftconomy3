/*
 * This file is part of Craftconomy3.
 *
 * Copyright (c) 2011-2013, Greatman <http://github.com/greatman/>
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
import java.util.ArrayList;
import java.util.List;

import com.greatmancode.craftconomy3.commands.interfaces.CommandManager;

/**
 * Special caller for unit tests.
 * @author Greatman
 */
public class UnitTestCaller implements Caller {

	public static final String worldName = "UnitTestWorld";
	public static final String worldName2 = "UnitTestWorld2";
	@Override
	public void disablePlugin() {
	}

	@Override
	public boolean checkPermission(String playerName, String perm) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void sendMessage(String playerName, String message) {
		System.out.println(playerName + ":" + message);
	}

	@Override
	public String getPlayerWorld(String playerName) {
		return "UnitTestWorld";
	}

	@Override
	public boolean isOnline(String playerName) {
		return playerName.equals("console");
	}

	@Override
	public String addColor(String message) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean worldExist(String worldName) {
		return worldName.equalsIgnoreCase(this.worldName) || worldName.equalsIgnoreCase(worldName2);
	}

	@Override
	public String getDefaultWorld() {
		return "UnitTestWorld";
	}

	@Override
	public File getDataFolder() {
		File file = new File(UnitTestCaller.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		file = new File(file.getParentFile(), "unittests");
		file.mkdir();
		return file;
	}

	@Override
	public int schedule(Runnable entry, long firstStart, long repeating) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int schedule(Runnable entry, long firstStart, long repeating, boolean async) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void cancelSchedule(int id) {
		// TODO Auto-generated method stub

	}

	@Override
	public int delay(Runnable entry, long start) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int delay(Runnable entry, long start, boolean async) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<String> getOnlinePlayers() {
		List<String> list = new ArrayList<String>();
		list.add("UnitTestPlayer");
		return list;
	}

	@Override
	public void addCommand(String name, String help, CommandManager manager) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getServerVersion() {
		return "UnitTest 1.0";
	}

	@Override
	public String getPluginVersion() {
		return "1.0";
	}

	@Override
	public boolean isOp(String playerName) {
		return playerName.equals("UnitTestPlayer");
	}

	@Override
	public void loadLibrary(String path) {
		// TODO Auto-generated method stub

	}

	@Override
	public void registerPermission(String permissionNode) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isOnlineMode() {
		return true;
	}
}
