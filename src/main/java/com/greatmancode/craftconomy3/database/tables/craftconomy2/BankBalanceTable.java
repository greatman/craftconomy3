package com.greatmancode.craftconomy3.database.tables.craftconomy2;

import com.alta189.simplesave.Field;
import com.alta189.simplesave.Id;
import com.alta189.simplesave.Table;

@Table("BankBalance")
public class BankBalanceTable {

	@Id
	public int id;
	
	@Field
	public int bank_id;
	
	@Field
	public double balance;
	
	@Field
	public int currency_id;
	
	@Field
	public String worldName;
}
