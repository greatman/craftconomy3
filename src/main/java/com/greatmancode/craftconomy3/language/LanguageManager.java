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
package com.greatmancode.craftconomy3.language;

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.configuration.Config;

/**
 * This class handles the multi-language support of Craftconomy.
 */
public class LanguageManager {
	private final Config file;

	/**
	 * Load the language file.
	 */
	public LanguageManager() {
		file = Common.getInstance().getConfigurationManager().loadFile(Common.getInstance().getServerCaller().getDataFolder(), "lang.yml");
	}

	/**
	 * Get a string from the language file
	 * @param name The key of the string.
	 * @return The language string.
	 */
	public String getString(String name) {
		return file.getString(name);
	}
}
