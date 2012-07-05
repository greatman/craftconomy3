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

import com.alta189.simplesave.exceptions.UnknownDriverException;
import com.alta189.simplesave.h2.H2Database;
import com.alta189.simplesave.internal.reflection.DatabaseInjector;
import com.alta189.simplesave.mysql.MySQLDatabase;
import com.alta189.simplesave.sqlite.SQLiteDatabase;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DatabaseFactory {
	private static final Map<String, Class<? extends Database>> registeredDatabases = new HashMap<String, Class<? extends Database>>();
	private static final Object key = new Object();

	static {
		try {
			Class.forName(MySQLDatabase.class.getCanonicalName());
			Class.forName(H2Database.class.getCanonicalName());
			Class.forName(SQLiteDatabase.class.getCanonicalName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void registerDatabase(Class<? extends Database> clazz) {
		synchronized (key) {
			String type = getDatabaseType(clazz).toLowerCase();
			registeredDatabases.put(type, clazz);
		}
	}

	public static void unregisterDatabase(Class<? extends Database> clazz) {
		unregisterDatabase(getDatabaseType(clazz));
	}

	public static void unregisterDatabase(String type) {
		synchronized (key) {
			registeredDatabases.remove(type.toLowerCase());
		}
	}

	public static Class<? extends Database> getRegisteredDatabase(String type) {
		synchronized (key) {
			return registeredDatabases.get(type.toLowerCase());
		}
	}

	public static Map<String, Class<? extends Database>> getRegisteredDatabases() {
		return Collections.unmodifiableMap(registeredDatabases);
	}

	public static Database createNewDatabase(Configuration config) {
		Class<? extends Database> clazz = getRegisteredDatabase(config.getDriver());
		if (clazz == null) {
			throw new UnknownDriverException("A Database could not be found for driver '" + config.getDriver() + "'");
		}

		return DatabaseInjector.newInstance(clazz, config);
	}

	private static String getDatabaseType(Class<? extends Database> clazz) {
		try {
			Method method = clazz.getDeclaredMethod("getDriver");

			if (!Modifier.isStatic(method.getModifiers())) {
				throw new IllegalArgumentException(clazz.getCanonicalName() + " does not have a static getDriver()");
			}

			if (!method.getReturnType().equals(String.class)) {
				throw new IllegalArgumentException(clazz.getCanonicalName() + "'s getDriver() does not return a String");
			}

			return (String) method.invoke(null);
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException(clazz.getCanonicalName() + " does not have getDriver()", e);
		} catch (InvocationTargetException e) {
			throw new IllegalArgumentException("There was an exception when registering '" + clazz.getCanonicalName() + "' with the DatabaseFactory", e);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException("There was an exception when registering '" + clazz.getCanonicalName() + "' with the DatabaseFactory", e);
		}
	}
}
