package com.greatmancode.craftconomy3.language;

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.configuration.Config;

public class LanguageManager {

	private Config file;
	public LanguageManager() {
		file = Common.getInstance().getConfigurationManager().loadFile(Common.getInstance().getServerCaller().getDataFolder(), "lang.yml");
	}
	
	public String getString(String name) {
		return file.getString(name);
	}
}
