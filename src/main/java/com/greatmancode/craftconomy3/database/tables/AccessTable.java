package com.greatmancode.craftconomy3.database.tables;

import com.alta189.simplesave.Field;
import com.alta189.simplesave.Table;

@Table("acl")
public class AccessTable {

	@Field
	public int account_id;
	
	@Field
	public String playerName;
	
}
