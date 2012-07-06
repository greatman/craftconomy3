package com.greatmancode.craftconomy3.currency;

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.database.tables.CurrencyTable;

public class Currency {

	private CurrencyTable entry = new CurrencyTable();

	public Currency(int databaseID, String name, String plural, String minor, String minorPlural) {
		entry.name = name;
		entry.plural = plural;
		entry.minor = minor;
		entry.minorplural = minorPlural;
		entry.id = databaseID;
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
		return entry.id;
	}
	
	private void save() {
		Common.getInstance().getDatabaseManager().getDatabase().save(entry);
	}
	
}
