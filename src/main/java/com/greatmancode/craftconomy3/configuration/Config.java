package com.greatmancode.craftconomy3.configuration;

public abstract class Config {
	public abstract int getInt(String path);
	public abstract long getLong(String path);
	public abstract double getDouble(String path);
	public abstract String getString(String path);
	
}
