/**
 * This file is part of Craftconomy3.
 *
 * Copyright (c) 2011-2016, Greatman <http://github.com/greatman/>
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

import com.greatmancode.craftconomy3.Common;

import java.util.Map;
import java.util.Map.Entry;

/**
 * World Group Manager
 */
public class WorldGroupsManager {
    public static final String DEFAULT_GROUP_NAME = "default";
    private final Map<String, WorldGroup> list;

    public WorldGroupsManager() {
        list = Common.getInstance().getStorageHandler().getStorageEngine().getWorldGroups();
        Common.getInstance().addMetricsGraph("WorldGroupCount", list.size() + "");
    }

    /**
     * Add a world to a group
     *
     * @param groupName the group name
     * @param world     the world to add
     */
    public void addWorldToGroup(String groupName, String world) {
        if (!groupName.equalsIgnoreCase(DEFAULT_GROUP_NAME)) {
            if (list.containsKey(groupName)) {
                list.get(groupName).addWorld(world);
            } else {
                WorldGroup group = new WorldGroup(groupName);
                group.addWorld(world);
            }
        }
    }

    /**
     * Retrieve the name of the worldgroup a world belongs to
     *
     * @param world The world name
     * @return The worldgroup name linked to this world
     */
    public String getWorldGroupName(String world) {
        String result = DEFAULT_GROUP_NAME;
        for (Entry<String, WorldGroup> entry : list.entrySet()) {
            if (entry.getValue().worldExist(world)) {
                result = entry.getKey();
            }
        }
        return result;
    }

    /**
     * Remove a world from a worldgroup
     *
     * @param world The world to reset to default
     */
    public void removeWorldFromGroup(String world) {
        String groupName = getWorldGroupName(world);
        if (!groupName.equals(DEFAULT_GROUP_NAME)) {
            list.get(groupName).removeWorld(world);
        }
    }

    /**
     * Remove a world group. Reverting all the world into this group to the default one.
     *
     * @param group The group to remove.
     */
    public void removeGroup(String group) {
        if (worldGroupExist(group)) {
            Common.getInstance().getStorageHandler().getStorageEngine().removeWorldGroup(group);
            list.remove(group);
        }
    }

    /**
     * Check if a world group exists
     *
     * @param name The world group name
     * @return True if the world exist else false
     */
    public boolean worldGroupExist(String name) {
        return list.containsKey(name);
    }

    /**
     * Create a world group
     *
     * @param name the world group name.
     */
    public void addWorldGroup(String name) {
        if (!worldGroupExist(name)) {
            list.put(name, new WorldGroup(name));
        }
    }
}
