package com.greatmancode.craftconomy3.database.tables;

import com.alta189.simplesave.Field;
import com.alta189.simplesave.Id;
import com.alta189.simplesave.Table;

@Table("Currency")
public class CurrencyTable {

	@Id
	public int id;
	
	@Field
	public String name;
	
	@Field
	public String plural;
	
	@Field 
	public String minor;
	
	@Field
	public String minorplural;
	
}
