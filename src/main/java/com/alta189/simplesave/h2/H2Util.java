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

public class H2Util {
	public static String getTypeFromClass(Class<?> clazz) {
		if (clazz.equals(int.class) || clazz.equals(Integer.class)) {
			return "INT";
		} else if (clazz.equals(long.class) || clazz.equals(Long.class)) {
			return "BIGINT";
		} else if (clazz.equals(double.class) || clazz.equals(Double.class)) {
			return "DOUBLE";
		} else if (clazz.equals(String.class)) {
			return "TEXT";
		} else if (clazz.equals(boolean.class) || clazz.equals(Boolean.class)) {
			return "TINYINT";
		} else if (clazz.equals(short.class) || clazz.equals(Short.class)) {
			return "SMALLINT";
		} else if (clazz.equals(float.class) || clazz.equals(Float.class)) {
			return "FLOAT";
		} else if (clazz.equals(byte.class) || clazz.equals(Byte.class)) {
			return "TINYINT";
		}
		Class<?> checkclazz = clazz;
		while (checkclazz!=null){
			for (Class<?> i : checkclazz.getInterfaces())
				if (i.getName().equals("java.io.Serializable"))
					return "BLOB";
			checkclazz = checkclazz.getSuperclass();
		}
		return null;
	}
}
