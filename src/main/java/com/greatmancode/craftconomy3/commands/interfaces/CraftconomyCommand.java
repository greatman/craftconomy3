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
package com.greatmancode.craftconomy3.commands.interfaces;

import com.greatmancode.craftconomy3.Common;

/**
 * Represents a command
 * @author greatman
 */
public abstract class CraftconomyCommand {
	/**
	 * The execution of the command
	 * @param sender The sender of the command
	 * @param args A String array of all the arguments
	 */
	public abstract void execute(String sender, String[] args);

	/**
	 * Checks if the command sender can execute this command.
	 * @param sender The sender to check
	 * @return True if the sender have permission else False
	 */
	public boolean permission(String sender) {
		boolean result = true;
		if (getPermissionNode() != null) {
			result = Common.getInstance().getServerCaller().checkPermission(sender, getPermissionNode());
		}
		return result;
	}

	/**
	 * Returns a usage/help line about the command
	 * @return A string containing the usage/help about the command.
	 */
	public abstract String help();

	/**
	 * The maximum number of arguments that this command take
	 * @return The maximum number of arguments
	 */
	public abstract int maxArgs();

	/**
	 * The minimum number of arguments this command take
	 * @return The minimum number of arguments
	 */
	public abstract int minArgs();

	/**
	 * State if this command is for Players only
	 * @return True if the command is for player only else false.
	 */
	public abstract boolean playerOnly();

	/**
	 * Returns the permission node of this command.
	 * @return The permission node of this command.
	 */
	public abstract String getPermissionNode();
}
