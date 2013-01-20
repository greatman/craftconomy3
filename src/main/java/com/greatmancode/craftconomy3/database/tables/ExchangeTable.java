package com.greatmancode.craftconomy3.database.tables;

import com.alta189.simplesave.Field;
import com.alta189.simplesave.Id;
import com.alta189.simplesave.Table;

@Table("cc3_exchange")
public class ExchangeTable {

	@Id
	public int id;
	
	@Field
	public int from_currency_id;
	
	@Field
	public int to_currency_id;
	
	@Field
	public double amount;
}
