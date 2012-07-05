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

import com.alta189.simplesave.internal.reflection.EmptyInjector;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Field;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({ "all" })
public class ResultSetUtils {
	private static final EmptyInjector injector = new EmptyInjector();

	public static <E> List<E> buildResultList(TableRegistration table, Class<E> clazz, ResultSet set) {
		List<E> result = new ArrayList<E>();
		try {
			while (set.next()) {
				result.add(buildResult(table, clazz, set));
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return result;
	}

	public static <E> E buildResult(TableRegistration table, Class<E> clazz, ResultSet set) {
		E result = (E) injector.newInstance(clazz);
		setField(table.getId(), result, set);

		for (FieldRegistration field : table.getFields()) {
			setField(field, result, set);
		}

		return result;
	}

	public static <E> void setField(FieldRegistration fieldRegistration, E object, ResultSet set) {
		try {
			Field field = fieldRegistration.getField();
			field.setAccessible(true);
			if (fieldRegistration.isSerializable()) {
				String result = set.getString(fieldRegistration.getName());
				field.set(object, TableUtils.deserializeField(fieldRegistration, result));
			} else {
				if (fieldRegistration.getType().equals(int.class)) {
					field.setInt(object, set.getInt(fieldRegistration.getName()));
				} else if (fieldRegistration.getType().equals(Integer.class)) {
					field.set(object, set.getObject(fieldRegistration.getName()));
				} else if (fieldRegistration.getType().equals(long.class)) {
					field.setLong(object, set.getLong(fieldRegistration.getName()));
				} else if (fieldRegistration.getType().equals(Long.class)) {
					field.set(object, set.getObject(fieldRegistration.getName()));
				} else if (fieldRegistration.getType().equals(double.class)) {
					field.setDouble(object, set.getDouble(fieldRegistration.getName()));
				} else if (fieldRegistration.getType().equals(Double.class)) {
					field.set(object, set.getObject(fieldRegistration.getName()));
				} else if (fieldRegistration.getType().equals(String.class)) {
					field.set(object, set.getString(fieldRegistration.getName()));
				} else if (fieldRegistration.getType().equals(boolean.class)) {
					int i = set.getInt(fieldRegistration.getName());
					if (i == 1) {
						field.setBoolean(object, true);
					} else {
						field.setBoolean(object, false);
					}
				} else if (fieldRegistration.getType().equals(Boolean.class)) {
					int i = set.getInt(fieldRegistration.getName());
					if (i == 1) {
						field.set(object, Boolean.TRUE);
					} else {
						field.set(object, Boolean.FALSE);
					}
				} else if (fieldRegistration.getType().equals(short.class)) {
					field.setShort(object, set.getShort(fieldRegistration.getName()));
				} else if (fieldRegistration.getType().equals(Short.class)) {
					field.set(object, set.getObject(fieldRegistration.getName()));
				} else if (fieldRegistration.getType().equals(float.class)) {
					field.setFloat(object, set.getFloat(fieldRegistration.getName()));
				} else if (fieldRegistration.getType().equals(Float.class)) {
					field.set(object, set.getObject(fieldRegistration.getName()));
				} else if (fieldRegistration.getType().equals(byte.class)) {
					field.setByte(object, set.getByte(fieldRegistration.getName()));
				} else if (fieldRegistration.getType().equals(Byte.class)) {
					field.set(object, set.getObject(fieldRegistration.getName()));
				} else {
					Blob b = set.getBlob(fieldRegistration.getName());
					ObjectInputStream is = new ObjectInputStream(b.getBinaryStream());
					Object o = null;
					try {
						o = is.readObject();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					} finally {
						is.close();
					}
					field.set(object, o);
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
