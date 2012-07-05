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

import com.alta189.simplesave.Table;
import com.alta189.simplesave.exceptions.FieldRegistrationException;
import com.alta189.simplesave.exceptions.TableRegistrationException;
import com.alta189.simplesave.internal.reflection.EmptyInjector;
import com.alta189.simplesave.internal.reflection.Injector;

import java.util.regex.Pattern;

public class TableFactory {
	public static TableRegistration buildTable(Class<?> clazz) throws TableRegistrationException {
		// Make sure that the class has the Table annotation
		if (!clazz.isAnnotationPresent(Table.class)) {
			throw new TableRegistrationException("Class '" + clazz.getCanonicalName() + "' does not have the Table annotation");
		}

		// Get the annotation and make sure that 'name' is defined
		Table table = clazz.getAnnotation(Table.class);
		if (table.value() == null || table.value().isEmpty()) {
			throw new TableRegistrationException("Class '" + clazz.getCanonicalName() + "' is missing a table name");
		}

		// Check that 'name' is only made up of allowed characters (Alphanumeric and '_')
		Pattern pattern = Pattern.compile("^[a-zA-Z0-9_]*$");
		if (!pattern.matcher(table.value()).find()) {
			throw new TableRegistrationException("Class '" + clazz.getCanonicalName() + "' table name has illegal characters in it. The name is limited to alphanumeric characters and '_'");
		}

		// Check that the class has an empty constructor
		Injector injector = new EmptyInjector();
		try {
			injector.newInstance(clazz);
		} catch (Exception e) {
			throw new TableRegistrationException("Class '" + clazz.getCanonicalName() + "' does not have an empty constructor", e);
		}

		// Create TableRegistration
		TableRegistration tableRegistration = new TableRegistration(table.value(), clazz);

		// Get Id
		IdRegistration idRegistration = IdFactory.getId(clazz);
		tableRegistration.setId(idRegistration);

		// Register fields
		try {
			tableRegistration.addFields(FieldFactory.getFields(clazz));
		} catch (FieldRegistrationException e) {
			throw new TableRegistrationException(e);
		}

		return tableRegistration;
	}
}
