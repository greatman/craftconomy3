package com.greatmancode.craftconomy3.groups;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class WorldGroupsManager {

	private final Map<String, WorldGroup> list = new HashMap<String, WorldGroup>();

	public void addWorldToGroup(String groupName, String world) {
		if (list.containsKey(groupName)) {
			list.get(groupName).addWorld(world);
		}
		else {
			WorldGroup group = new WorldGroup(groupName);
			group.addWorld(world);
		}
	}

	public String getWorldGroupName(String world) {
		String result = null;
		Iterator<Entry<String,WorldGroup>> iterator = list.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, WorldGroup> entry = iterator.next();
			if (entry.getValue().worldExist(world)) {
				result = entry.getKey();
			}
		}
		return result;
	}
}
