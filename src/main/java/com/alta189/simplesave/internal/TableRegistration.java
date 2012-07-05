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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TableRegistration {
	private final String name;
	private final Class<?> clazz;
	private final Map<String, FieldRegistration> fields = new HashMap<String, FieldRegistration>();
	private IdRegistration id;

	public TableRegistration(String name, Class<?> clazz) {
		this.name = name;
		this.clazz = clazz;
	}

	public String getName() {
		return name;
	}

	public Class<?> getTableClass() {
		return clazz;
	}

	public IdRegistration getId() {
		return id;
	}

	public void setId(IdRegistration id) {
		this.id = id;
	}

	public Collection<FieldRegistration> getFields() {
		return fields.values();
	}

	public void addFields(Collection<FieldRegistration> fields) {
		for (FieldRegistration field : fields) {
			addField(field.getName(), field);
		}
	}

	public void addField(String name, FieldRegistration field) {
		fields.put(name.toLowerCase(), field);
	}
}
