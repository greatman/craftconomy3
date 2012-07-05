package com.greatmancode.craftconomy3.database.tables;

import com.alta189.simplesave.Field;
import com.alta189.simplesave.Id;
import com.alta189.simplesave.Table;

@Table("balance")
public class BalanceTable {

	@Id
	public int id;
	
	@Field
	public int username_id;
	
	@Field
	public int currency_id;
	
	@Field
	public String worldName;
	
	@Field
	public double balance;
}
