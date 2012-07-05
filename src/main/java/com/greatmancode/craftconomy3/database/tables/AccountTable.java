package com.greatmancode.craftconomy3.database.tables;

import com.alta189.simplesave.Field;
import com.alta189.simplesave.Id;
import com.alta189.simplesave.Table;

@Table("Account")
public class AccountTable {

	@Id
	public int id;
	
	@Field
	public String name;
}
