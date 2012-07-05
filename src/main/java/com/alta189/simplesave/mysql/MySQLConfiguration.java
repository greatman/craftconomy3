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
package com.alta189.simplesave.mysql;

import com.alta189.simplesave.Configuration;

public class MySQLConfiguration extends Configuration {
	public MySQLConfiguration() {
		super("mysql");

		// Set defaults
		setUser(MySQLConstants.DefaultUser);
		setPassword(MySQLConstants.DefaultPass);
		setPort(MySQLConstants.DefaultPort);
	}

	public String getUser() {
		return getProperty(MySQLConstants.User);
	}

	public MySQLConfiguration setUser(String user) {
		setProperty(MySQLConstants.User, user);
		return this;
	}

	public String getPassword() {
		return getProperty(MySQLConstants.Password);
	}

	public MySQLConfiguration setPassword(String password) {
		setProperty(MySQLConstants.Password, password);
		return this;
	}

	public String getHost() {
		return getProperty(MySQLConstants.Host);
	}

	public MySQLConfiguration setHost(String host) {
		setProperty(MySQLConstants.Host, host);
		return this;
	}

	public int getPort() {
		return Integer.valueOf(getProperty(MySQLConstants.Port));
	}

	public MySQLConfiguration setPort(int port) {
		setProperty(MySQLConstants.Port, Integer.toString(port));
		return this;
	}

	public String getDatabase() {
		return getProperty(MySQLConstants.Database);
	}

	public MySQLConfiguration setDatabase(String database) {
		setProperty(MySQLConstants.Database, database);
		return this;
	}
}
