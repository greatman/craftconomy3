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
package com.greatmancode.craftconomy3.groups;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.database.tables.WorldGroupTable;

public class WorldGroupsManager {

	public static final String DEFAULT_GROUP_NAME = "default";
	private final Map<String, WorldGroup> list = new HashMap<String, WorldGroup>();

	public WorldGroupsManager() {
		for (WorldGroupTable group : Common.getInstance().getDatabaseManager().getDatabase().select(WorldGroupTable.class).execute().find()) {
			list.put(group.groupName, new WorldGroup(group.groupName));
		}

	}

	public void addWorldToGroup(String groupName, String world) {
		if (!groupName.equalsIgnoreCase(DEFAULT_GROUP_NAME)) {
			if (list.containsKey(groupName)) {
				list.get(groupName).addWorld(world);
			}
			else {
				WorldGroup group = new WorldGroup(groupName);
				group.addWorld(world);
			}
		}
	}

	public String getWorldGroupName(String world) {
		String result = DEFAULT_GROUP_NAME;
		Iterator<Entry<String,WorldGroup>> iterator = list.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, WorldGroup> entry = iterator.next();
			if (entry.getValue().worldExist(world)) {
				result = entry.getKey();
			}
		}
		return result;
	}

	public void removeWorldFromGroup(String world) {
		String groupName = getWorldGroupName(world);
		if (!groupName.equals(DEFAULT_GROUP_NAME)) {
			list.get(groupName).removeWorld(world);
		}
	}

	/**
	 * Remove a world group. Reverting all the world into this group to the default one.
	 * @param group The group to remove.
	 */
	public void removeGroup(String group) {
		if (worldGroupExist(group)) {
			Common.getInstance().getDatabaseManager().getDatabase().remove(list.get(group).table);
			list.remove(group);
		}
	}
	public boolean worldGroupExist(String name) {
		return list.containsKey(name);
	}

	public void addWorldGroup(String name) {
		if (!worldGroupExist(name)) {
			list.put(name, new WorldGroup(name));
		}
	}
}
