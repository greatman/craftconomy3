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
package com.alta189.simplesave.h2;

import com.alta189.simplesave.Configuration;

public class H2Configuration extends Configuration {

	public static final String H2_DATABASE = "h2.database";

	public H2Configuration() {
		super("h2");
	}

	public String getDatabase() {
		return getProperty(H2_DATABASE);
	}

	public H2Configuration setDatabase(String database) {
		setProperty(H2_DATABASE, database);
		return this;
	}
}
