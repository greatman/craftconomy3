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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
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
        String line;
		for (File account : accounts){
            try {
                BufferedReader reader = new BufferedReader(new FileReader(account));
                while ((line = reader.readLine()) != null) {
                //money: '0.0'
                    if (line.contains("money")) {
                        String value = line.replace("money: '", "");
                        value = value.substring(0, value.length() - 1);
                        double money = Double.parseDouble(value);
                        String name = account.getName().replace(".yml", "");
                        userList.add(new User(name, money));
                    }
                }
                if (i % 10 == 0) {
                    Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, i + " {{DARK_GREEN}}accounts loaded.");
                }
                i++;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
		}
		addAccountToString(userList);
		addBalance(sender, userList);
		return true;
	}
}
