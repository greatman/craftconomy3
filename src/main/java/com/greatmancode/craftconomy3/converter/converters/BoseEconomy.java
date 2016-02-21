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

import com.greatmancode.craftconomy3.Cause;
import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.account.Account;
import com.greatmancode.craftconomy3.converter.Converter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class BoseEconomy extends Converter {
    private static final String TAB_CHECK = "\\t+";
    private BufferedReader flatFileReader = null;
    private List<User> userList = new ArrayList<>();

    public BoseEconomy() {
        getDbTypes().add("flatfile");
    }

    @Override
    public List<String> getDbInfo() {
        if (getDbInfoList().size() == 0) {
            getDbInfoList().add("filename");
        }
        return getDbInfoList();
    }

    @Override
    public boolean connect() {
        boolean result = false;
        File dbFile = new File(Common.getInstance().getServerCaller().getDataFolder(), this.getDbConnectInfo().get("filename"));
        if (dbFile.exists()) {
            try {
                flatFileReader = new BufferedReader(new FileReader(dbFile));
                result = true;
            } catch (FileNotFoundException e) {
                Common.getInstance().getLogger().severe("BoseEconomy database file not found!");
            }
        }
        return result;
    }

    @Override
    public boolean importData(String sender) {
        String line = "";
        try {
            int i = 0;
            while (line != null) {
                if (i % ALERT_EACH_X_ACCOUNT == 0) {
                    Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, i + " {{DARK_GREEN}}accounts imported.");
                }
                line = flatFileReader.readLine();
                if (line != null && Pattern.compile("[?a-zA-Z0-9\\s{_-]+").matcher(line).matches()) {
                    String username = line.split(" ")[0];
                    line = flatFileReader.readLine();
                    // Line for account type
                    String type = line.split(" ")[1];
                    if ("player".equalsIgnoreCase(type)) {
                        accountImporter(sender, username);
                        i++;
                    } else if ("type".equalsIgnoreCase(type)) {
                        bankImporter(username);
                        i++;
                    }
                }
            }
            flatFileReader.close();
            addAccountToString(sender,userList);
        } catch (IOException e) {
            Common.getInstance().getLogger().severe("Error while reading bose file!" + e.getMessage());
        }
        return true;
    }

    private void bankImporter(String username) throws IOException {
        String line = flatFileReader.readLine();
        double amount = Double.parseDouble(line.split(" ")[1]);
        Common.getInstance().getAccountManager().getAccount(username, true).set(amount, Common.getInstance().getServerCaller().getDefaultWorld(), Common.getInstance().getCurrencyManager().getDefaultCurrency().getName(), Cause.CONVERT, null);
        line = flatFileReader.readLine();
        if (line.contains("members")) {
            line = flatFileReader.readLine();
            line = line.replaceAll(TAB_CHECK, "");
            while (!"}".equals(line)) {
                Common.getInstance().getAccountManager().getAccount(username,true).getAccountACL().set(line, true, true, false, true, false);
                line = flatFileReader.readLine();
                line = line.replaceAll(TAB_CHECK, "");
            }
        }
        line = flatFileReader.readLine();
        if ("owners".contains(line)) {
            line = flatFileReader.readLine();
            line = line.replaceAll(TAB_CHECK, "");
            while (!"}".equals(line)) {
                Common.getInstance().getAccountManager().getAccount(username, true).getAccountACL().set(line, true, true, true, true, true);
                line = flatFileReader.readLine();
                line = line.replaceAll(TAB_CHECK, "");
            }
        }
    }

    private void accountImporter(String sender, String username) throws IOException {
        String line = flatFileReader.readLine();
        double amount = Double.parseDouble(line.split(" ")[1]);
        userList.add(new User(username, amount));
    }
}
