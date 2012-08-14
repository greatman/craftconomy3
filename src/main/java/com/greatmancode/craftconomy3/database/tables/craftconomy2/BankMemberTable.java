package com.greatmancode.craftconomy3.database.tables.craftconomy2;

import com.alta189.simplesave.Field;
import com.alta189.simplesave.Table;

@Table("BankMember")
public class BankMemberTable {

	@Field
	public int bank_id;
	
	@Field
	public String playerName;
}
