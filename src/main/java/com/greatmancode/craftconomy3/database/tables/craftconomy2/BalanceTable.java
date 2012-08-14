package com.greatmancode.craftconomy3.database.tables.craftconomy2;

import com.alta189.simplesave.Field;
import com.alta189.simplesave.Id;
import com.alta189.simplesave.Table;

@Table("Balance")
public class BalanceTable {

	@Id
	public int id;
	
	@Field
	public double balance;
	
	@Field
	public int currency_id;
	
	@Field
	public String worldName;
	
	@Field
	public int username_id;
}
