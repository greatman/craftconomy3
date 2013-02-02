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
