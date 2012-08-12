package com.greatmancode.craftconomy3.database.tables;

import com.alta189.simplesave.Field;
import com.alta189.simplesave.Id;
import com.alta189.simplesave.Table;

@Table("config")
public class ConfigTable {

	@Id
	public int id; //To prevent possible bugs
	
	@Field
	public String name;
	
	@Field
	public String value;
}
