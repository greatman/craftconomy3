/*
 * This file is part of SimpleSave
 *
 * SimpleSave is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SimpleSave is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.alta189.simplesave;

import java.util.HashMap;
import java.util.Map;

public class Configuration {
	private final Map<String, String> properties;
	private final String driver;

	public Configuration(String driver) {
		this(new HashMap<String, String>(), driver);
	}

	public Configuration(Map<String, String> properties, String driver) {
		this.properties = properties;
		this.driver = driver.toLowerCase();
	}

	public final String getDriver() {
		return driver;
	}

	public final Map<String, String> getProperties() {
		return properties;
	}

	public final String getProperty(String property) {
		return properties.get(property);
	}

	public final Configuration setProperty(String property, String value) {
		properties.put(property, value);
		return this;
	}

	public final Configuration removeProperty(String property) {
		properties.remove(property);
		return this;
	}

	public final boolean containsProperty(String property) {
		return properties.containsKey(property);
	}
}
