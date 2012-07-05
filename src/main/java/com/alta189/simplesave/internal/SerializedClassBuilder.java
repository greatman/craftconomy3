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
package com.alta189.simplesave.internal;

import com.alta189.simplesave.exceptions.SerializeException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class SerializedClassBuilder {
	public static boolean validClass(Class<?> clazz) {
		try {
			Method serialize = clazz.getDeclaredMethod("serialize");
			if (!serialize.getReturnType().equals(String.class)) {
				System.out.println("Class '" + clazz.getCanonicalName() + "' is not serializable because it does not return a String");
				return false;
			}
			if (!Modifier.isPublic(serialize.getModifiers())) {
				System.out.println("Class '" + clazz.getCanonicalName() + "' is not serializable because the method 'serialize' is not public");
				return false;
			}
			if (!Modifier.isStatic(serialize.getModifiers())) {
				System.out.println("Class '" + clazz.getCanonicalName() + "' is not serializable because the method 'serialize' is static");
				return false;
			}

			Method deserialize = clazz.getDeclaredMethod("deserialize", String.class);
			if (!deserialize.getReturnType().equals(clazz)) {
				System.out.println("Class '" + clazz.getCanonicalName() + "' is not deserializable because the method 'deserialize' does not return the class '" + clazz.getCanonicalName() + "'");
				return false;
			}

			if (!Modifier.isStatic(deserialize.getModifiers())) {
				System.out.println("Class '" + clazz.getCanonicalName() + "' is not deserializable because the method 'deserialize' is not static");
				return false;
			}
			if (!Modifier.isPublic(deserialize.getModifiers())) {
				System.out.println("Class '" + clazz.getCanonicalName() + "' is not deserializable because the method 'deserialize' is not public");
				return false;
			}
		} catch (NoSuchMethodException e) {
			System.out.println("Class '" + clazz.getCanonicalName() + "' does not have either the serialize and/or deserialize method(s)");
			return false;
		}
		return true;
	}

	public static Object deserialize(Class<?> clazz, String data) {
		try {
			Method deserialize = clazz.getDeclaredMethod("deserialize", String.class);
			deserialize.setAccessible(true);
			return deserialize.invoke(null, data);
		} catch (NoSuchMethodException e) {
			throw new SerializeException("Could not deserialize data", e.getCause());
		} catch (InvocationTargetException e) {
			throw new SerializeException("Could not deserialize data", e.getCause());
		} catch (IllegalAccessException e) {
			throw new SerializeException("Could not deserialize data", e.getCause());
		}
	}

	public static String serialize(Class<?> clazz, Object object) {
		try {
			Method serialize = clazz.getDeclaredMethod("serialize");
			serialize.setAccessible(true);
			return (String) serialize.invoke(object);
		} catch (NoSuchMethodException e) {
			throw new SerializeException("Could not serialize Class '" + clazz.getCanonicalName() + "'", e.getCause());
		} catch (InvocationTargetException e) {
			throw new SerializeException("Could not serialize Class '" + clazz.getCanonicalName() + "'", e.getCause());
		} catch (IllegalAccessException e) {
			throw new SerializeException("Could not serialize Class '" + clazz.getCanonicalName() + "'", e.getCause());
		} catch (ClassCastException e) {
			throw new SerializeException("Could not serialize Class '" + clazz.getCanonicalName() + "'", e.getCause());
		}
	}
}
