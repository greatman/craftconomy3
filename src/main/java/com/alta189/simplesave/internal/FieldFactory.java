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

import com.alta189.simplesave.exceptions.FieldRegistrationException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class FieldFactory {
	public static List<FieldRegistration> getFields(Class<?> clazz) throws FieldRegistrationException {
		final List<FieldRegistration> fields = new ArrayList<FieldRegistration>();
		while (clazz!=null){
			for (Field field : clazz.getDeclaredFields()) {
				FieldRegistration fieldRegistration = getField(field);
				if (fieldRegistration != null) {
					for (FieldRegistration f : fields){
						if (f.getName().equals(fieldRegistration.getName()))
							throw new FieldRegistrationException("Duplicate fields!");
					}
					fields.add(fieldRegistration);
				}
			}
			clazz = clazz.getSuperclass();
		}
		return fields;
	}

	public static FieldRegistration getField(Field field) throws FieldRegistrationException {
		// Check if the field has the Field annotation
		com.alta189.simplesave.Field fieldAnnotation = getFieldAnnotation(field);
		if (fieldAnnotation == null) {
			return null;
		}

		if (Modifier.isStatic(field.getModifiers())) {
			throw new FieldRegistrationException("Field cannot be static!");
		}

		Class<?> type = field.getType();

		// Check if the field has a valid type
		if (validType(type)) {
			return new FieldRegistration(field, type);
		} else if (SerializedClassBuilder.validClass(type)) {
			return new FieldRegistration(field, type, true);
		} else {
			throw new FieldRegistrationException("The type '" + type.getCanonicalName() + "' is not a valid type");
		}
	}

	public static com.alta189.simplesave.Field getFieldAnnotation(Field field) {
		com.alta189.simplesave.Field fieldAnnotation = null;
		for (Annotation annotation : field.getDeclaredAnnotations()) {
			if (annotation instanceof com.alta189.simplesave.Field) {
				fieldAnnotation = (com.alta189.simplesave.Field) annotation;
				break;
			}
		}
		return fieldAnnotation;
	}

	public static boolean hasFieldAnnotation(Field field) {
		for (Annotation annotation : field.getDeclaredAnnotations()) {
			if (annotation.getClass().equals(com.alta189.simplesave.Field.class)) {
				return true;
			}
		}
		return false;
	}

	public static boolean validType(Class<?> type) {
		if (type.equals(int.class) || type.equals(Integer.class)) {
			return true;
		} else if (type.equals(long.class) || type.equals(Long.class)) {
			return true;
		} else if (type.equals(double.class) || type.equals(Double.class)) {
			return true;
		} else if (type.equals(String.class)) {
			return true;
		} else if (type.equals(boolean.class) || type.equals(Boolean.class)) {
			return true;
		} else if (type.equals(short.class) || type.equals(Short.class)) {
			return true;
		} else if (type.equals(float.class) || type.equals(Float.class)) {
			return true;
		} else if (type.equals(byte.class) || type.equals(Byte.class)) {
			return true;
		}
		Class<?> checkclazz = type;
		while (checkclazz!=null){
			for (Class<?> i : checkclazz.getInterfaces())
				if (i.getName().equals("java.io.Serializable"))
					return true;
			checkclazz = checkclazz.getSuperclass();
		}
		return false;
	}
}
