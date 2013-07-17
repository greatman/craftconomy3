/*
 * This file is part of Craftconomy3.
 *
 * Copyright (c) 2011-2013, Greatman <http://github.com/greatman/>
 *
 * Craftconomy3 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Craftconomy3 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Craftconomy3.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.greatmancode.craftconomy3.converter.converters;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.converter.Converter;
import com.greatmancode.tools.configuration.Config;
import com.greatmancode.tools.interfaces.caller.ServerCaller;

public class Essentials extends Converter {

	public Essentials() {
		getDbTypes().add("flatfile");
	}
	@Override
	public List<String> getDbInfo() {
		return new ArrayList<String>();
	}

	@Override
	public boolean connect() {
		return true;
	}

	@Override
	public boolean importData(String sender) {
		File accountsFolder = new File("plugins/Essentials/userdata/");

		if (!accountsFolder.isDirectory()){
			return false;
		}

		File[] accounts = accountsFolder.listFiles(new FilenameFilter(){
			public boolean accept(File file, String name){
				return name.toLowerCase().endsWith(".yml");
			}
		});
		List<User> userList = new ArrayList<User>();

		int i = 0;
		for (File account : accounts){
			Config config = Common.getInstance().getConfigurationManager().loadFile(accountsFolder, account.getName());
			String name = account.getName().replace(".yml", "");

			try {
				double money = Double.parseDouble(config.getString("money"));
				if (money != -1){
					userList.add(new User(name, money));
				}
			} catch (NumberFormatException e) {
				Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, "{{DARK_RED}}Account {{WHITE}}" + name + " {{DARK_RED}} didin't import due to a error!");
			} catch (NullPointerException e) {
				Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, "{{DARK_RED}}Account {{WHITE}}" + name + " {{DARK_RED}} didin't import due to a error!");
			}


			if (i % 10 == 0) {
				Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, i + " {{DARK_GREEN}}accounts loaded.");
			}
			i++;
		}
		addAccountToString(userList);
		addBalance(sender, userList);
		return true;
	}
}
