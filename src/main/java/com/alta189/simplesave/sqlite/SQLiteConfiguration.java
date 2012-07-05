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
package com.alta189.simplesave.sqlite;

import com.alta189.simplesave.Configuration;

public class SQLiteConfiguration extends Configuration {
	public SQLiteConfiguration() {
		super("sqlite");
		setPath(SQLiteConstants.DefaultPath);
	}

	public SQLiteConfiguration(String path) {
		super("sqlite");
		this.setProperty(SQLiteConstants.Path, path);
	}

	public String getPath() {
		return getProperty(SQLiteConstants.Path);
	}

	public SQLiteConfiguration setPath(String path) {
		this.setProperty(SQLiteConstants.Path, path);
		return this;
	}
}
