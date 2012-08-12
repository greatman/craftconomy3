package com.greatmancode.craftconomy3.database.tables;

import com.alta189.simplesave.Field;
import com.alta189.simplesave.Table;

@Table("config")
public class ConfigTable {

	@Field
	public String name;
	
	@Field
	public String value;
}
