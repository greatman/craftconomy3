package com.alta189.simplesave.internal.reflection;

import com.alta189.simplesave.Configuration;
import com.alta189.simplesave.Database;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class DatabaseInjector {
	public static Database newInstance(Class<?> clazz, Configuration config) {
		try {
			Constructor<?> constructor = clazz.getConstructor(Configuration.class);
			return (Database) constructor.newInstance(config);
		} catch (NoSuchMethodException e) {
			throw new InjectorException("Could not create a new instance of class '" + clazz.getCanonicalName() + "'", e.getCause());
		} catch (InvocationTargetException e) {
			throw new InjectorException("Could not create a new instance of class '" + clazz.getCanonicalName() + "'", e.getCause());
		} catch (InstantiationException e) {
			throw new InjectorException("Could not create a new instance of class '" + clazz.getCanonicalName() + "'", e.getCause());
		} catch (IllegalAccessException e) {
			throw new InjectorException("Could not create a new instance of class '" + clazz.getCanonicalName() + "'", e.getCause());
		} catch (ClassCastException e) {
			throw new InjectorException("Could not create a new instance of class '" + clazz.getCanonicalName() + "'", e.getCause());
		}
	}
}
