package com.greatmancode.craftconomy3.converter;

import java.util.List;

public interface Converter {

	public List<String> getDbTypes();
	public boolean setDbType(String dbType);
	public List<String> getDbInfo();
	public boolean setDbInfo(String field, String value);
	public boolean connect();
	public boolean importData(String sender);
	public boolean allSet();
}
