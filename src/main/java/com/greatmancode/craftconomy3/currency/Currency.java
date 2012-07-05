package com.greatmancode.craftconomy3.currency;

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.database.tables.CurrencyTable;

public class Currency {

	private int databaseID;
	private CurrencyTable entry = new CurrencyTable();

	public Currency(int databaseID, String name, String plural, String minor, String minorPlural) {
		setName(name);
		setPlural(plural);
		setMinor(minor);
		setMinorPlural(minorPlural);
	}

	public String getName() {
		return entry.name;
	}

	public void setName(String name) {
		entry.name = name;
		save();
	}

	public String getPlural() {
		return entry.plural;
	}

	public void setPlural(String plural) {
		entry.plural = plural;
		save();
	}

	public String getMinor() {
		return entry.minor;
	}

	public void setMinor(String minor) {
		entry.minor = minor;
		save();
	}

	public String getMinorPlural() {
		return entry.minorplural;
	}

	public void setMinorPlural(String minorPlural) {
		entry.minorplural = minorPlural;
		save();
	}

	public int getDatabaseID() {
		return databaseID;
	}
	
	private void save() {
		Common.getInstance().getDatabaseManager().getDatabase().save(entry);
	}
	
}
