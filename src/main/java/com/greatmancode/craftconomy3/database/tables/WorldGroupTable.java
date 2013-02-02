package com.greatmancode.craftconomy3.database.tables;

import com.alta189.simplesave.Field;
import com.alta189.simplesave.Id;
import com.alta189.simplesave.Table;

@Table("cc3_worldgroup")
public class WorldGroupTable {

	@Id
	public int id;

	@Field
	public String groupName;

	@Field
	public String worldName;
}
