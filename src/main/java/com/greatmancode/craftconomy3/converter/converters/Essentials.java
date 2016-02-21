/**
 * This file is part of Craftconomy3.
 *
 * Copyright (c) 2011-2016, Greatman <http://github.com/greatman/>
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

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.converter.Converter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Essentials extends Converter {

    public Essentials() {
        getDbTypes().add("flatfile");
    }

    @Override
    public List<String> getDbInfo() {
        return new ArrayList<>();
    }

    @Override
    public boolean connect() {
        return true;
    }

    @Override
    public boolean importData(String sender) {
        File accountsFolder = new File("plugins/Essentials/userdata/");

        if (!accountsFolder.isDirectory()) {
            return false;
        }

        File[] accounts = accountsFolder.listFiles(new FilenameFilter() {
            public boolean accept(File file, String name) {
                return name.toLowerCase().endsWith(".yml");
            }
        });
        List<User> userList = new ArrayList<>();
        Common.getInstance().getLogger().info("Amount of accounts found:" + accounts.length);
        int i = 0;
        String line;
        for (File account : accounts) {
            String uuid = null;
            String name = null;
            double money = 0;
            boolean haveMoney = false;
            if (account.getName().contains("-")) {
                uuid = account.getName().replace(".yml", "");
            }
            try {
                BufferedReader reader = new BufferedReader(new FileReader(account));
                try {
                    while ((line = reader.readLine()) != null) {
                        //This is a UUID essentials file.

                        //money: '0.0'
                        if (line.startsWith("money:")) {
                            String value = line.replace("money: '", "");
                            if (value.contains("money")) {
                                value = line.replace("money: ", "");
                            }
                            money = Double.parseDouble(value.substring(0, value.length() - 1));
                            haveMoney = true;
                        } else if (line.startsWith("lastAccountName:")) {
                            name = line.replace("lastAccountName: ", "").trim().replace("\'", "").replace("\"", "");
                        }
                    }
                } catch (NumberFormatException e) {
                    Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, "{{DARK_RED}}The account "+uuid+" don't have a valid money value!");

                }
                if (haveMoney) {
                    userList.add(new User(name,uuid,money));
                }
                if (i % 10 == 0) {
                    Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, i + " {{DARK_GREEN}}accounts loaded.");
                }
                i++;
                reader.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        addAccountToString(sender, userList);
        return true;
    }
}
