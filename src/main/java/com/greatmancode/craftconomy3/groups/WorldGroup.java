package com.greatmancode.craftconomy3.groups;

import java.util.ArrayList;
import java.util.List;

import com.greatmancode.craftconomy3.Common;

/**
 * Contains information about a world group.
 */
public class WorldGroup {

	private String name;
	private List<String> worldList = new ArrayList<String>();

	/**
	 * Initialize a world group.
	 * @param name The group name.
	 */
	public WorldGroup(String name) {
		this.name = name;
	}

	/**
	 * Add a world to this worldGroup. It needs to exist so it can be added!
	 * @param name The world name.
	 */
	public void addWorld(String name) {
		if (name != null && Common.getInstance().getServerCaller().worldExist(name)) {
			worldList.add(name);
		}

	}

	public boolean worldExist(String worldName) {
		return worldList.contains(worldName);
	}
}
