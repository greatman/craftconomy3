/*
 * This file is part of Craftconomy3.
 *
 * Copyright (c) 2011-2014, Greatman <http://github.com/greatman/>
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
import com.greatmancode.craftconomy3.database.tables.WorldGroupTable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Contains information about a world group.
 */
public class WorldGroup {
    private String name;
    private List<String> worldList = new ArrayList<String>();

    /**
     * Initialize a world group.
     *
     * @param name The group name.
     */
    public WorldGroup(String name) {
        this.name = name;
        try {
            Connection connection = Common.getInstance().getDatabaseManager().getDatabase().getConnection();
            PreparedStatement statement = connection.prepareStatement(WorldGroupTable.SELECT_ENTRY);
            statement.setString(1, name);
            ResultSet set = statement.executeQuery();
            if (set.next()) {
                Collections.addAll(worldList, set.getString("worldList").split(","));
            } else {
                statement.close();
                statement = connection.prepareStatement(WorldGroupTable.INSERT_ENTRY);
                statement.setString(1, name);
                statement.executeUpdate();
            }
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Add a world to this worldGroup. It needs to exist so it can be added!
     *
     * @param name The world name.
     */
    public void addWorld(String name) {
        if (name != null && Common.getInstance().getServerCaller().worldExist(name) && !worldExist(name)) {
            worldList.add(name);
            save();
        }
    }

    /**
     * Remove a world from the group if it exists.
     *
     * @param world The world name
     */
    public void removeWorld(String world) {
        if (worldList.contains(world)) {
            worldList.remove(world);
            save();
        }
    }

    /**
     * Checks if a certain world is in this group.
     *
     * @param worldName The world name.
     * @return True if the world is in this world. Else false.
     */
    public boolean worldExist(String worldName) {
        return worldList.contains(worldName);
    }

    private void save() {
        String save = "";
        Iterator<String> iterator = worldList.iterator();
        while (iterator.hasNext()) {
            save += iterator.next();
            if (iterator.hasNext()) {
                save += ",";
            }
        }
        Common.getInstance().getStorageHandler().getStorageEngine().saveWorldGroup(name, save);
    }
}
