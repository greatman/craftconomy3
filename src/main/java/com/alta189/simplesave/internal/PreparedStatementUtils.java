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

import javax.sql.rowset.serial.SerialBlob;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class PreparedStatementUtils {
	public static void setObject(PreparedStatement statement, int index, Object o) throws SQLException {
		if (o == null) {
			statement.setObject(index, null);
			return;
		}
		Class<?> clazz = o.getClass();
		if (clazz.equals(int.class) || clazz.equals(Integer.class)) {
			statement.setInt(index, ((Number) o).intValue());
		} else if (clazz.equals(long.class) || clazz.equals(Long.class)) {
			statement.setLong(index, ((Number) o).longValue());
		} else if (clazz.equals(double.class) || clazz.equals(Double.class)) {
			statement.setDouble(index, ((Number) o).doubleValue());
		} else if (clazz.equals(String.class)) {
			statement.setString(index, (String) o);
		} else if (clazz.equals(boolean.class) || clazz.equals(Boolean.class)) {
			statement.setInt(index, ((Boolean) o) ? 1 : 0);
		} else if (clazz.equals(short.class) || clazz.equals(Short.class)) {
			statement.setShort(index, ((Number) o).shortValue());
		} else if (clazz.equals(float.class) || clazz.equals(Float.class)) {
			statement.setFloat(index, ((Number) o).floatValue());
		} else if (clazz.equals(byte.class) || clazz.equals(Byte.class)) {
			statement.setByte(index, ((Number) o).byteValue());
		} else {
			ByteArrayOutputStream bos = null;
			ObjectOutput out = null;
			try {
				bos = new ByteArrayOutputStream();
				out = new ObjectOutputStream(bos);
				out.writeObject(o);
				byte[] bytes = bos.toByteArray();
				statement.setBlob(index, new SerialBlob(bytes));
			} catch (IOException e) {
				throw new RuntimeException(e);
			} finally {
				if (out != null) {
					try {
						out.close();
					} catch (IOException ignored) {
					}
				}
				if (bos != null) {
					try {
						bos.close();
					} catch (IOException ignored) {
					}
				}
			}
		}
	}
}
