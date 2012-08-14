package com.greatmancode.craftconomy3.database.tables.craftconomy2;

import com.alta189.simplesave.Field;
import com.alta189.simplesave.Id;
import com.alta189.simplesave.Table;

@Table("Bank")
public class BankTable {

	@Id
	public int id;
	
	@Field
	public String name;
	
	@Field
	public String owner;
}
