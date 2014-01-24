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

import com.alta189.simplesave.Database;
import com.alta189.simplesave.DatabaseFactory;
import com.alta189.simplesave.exceptions.ConnectionException;
import com.alta189.simplesave.exceptions.TableRegistrationException;
import com.alta189.simplesave.mysql.MySQLConfiguration;
import com.alta189.simplesave.sqlite.SQLiteConfiguration;
import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.converter.Converter;
import com.greatmancode.craftconomy3.database.tables.iconomy.IConomyTable;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

/**
 * Converter for iConomy 6.
 *
 * @author greatman
 */
public class Iconomy6 extends Converter {
    private BufferedReader flatFileReader = null;
    private Database db = null;

    public Iconomy6() {
        getDbTypes().add("flatfile");
        getDbTypes().add("minidb");
        getDbTypes().add("sqlite");
        getDbTypes().add("mysql");
    }

    @Override
    public List<String> getDbInfo() {

        if (getDbInfoList().size() == 0) {
            if (getSelectedDbType().equals("flatfile") || getSelectedDbType().equals("minidb") || getSelectedDbType().equals("sqlite")) {
                getDbInfoList().add("filename");
            } else if (getSelectedDbType().equals("mysql")) {
                getDbInfoList().add("address");
                getDbInfoList().add("port");
                getDbInfoList().add("username");
                getDbInfoList().add("password");
                getDbInfoList().add("database");
            }
        }
        return getDbInfoList();
    }

    @Override
    public boolean connect() {
        boolean result = false;
        if (getSelectedDbType().equals("flatfile") || getSelectedDbType().equals("minidb")) {
            result = loadFile();
        } else if (getSelectedDbType().equals("mysql")) {
            loadMySQL();
        } else if (getSelectedDbType().equals("sqlite")) {
            loadSQLite();
        }

        if (db != null) {

            try {
                db.registerTable(IConomyTable.class);
                db.setCheckTableOnRegistration(false);
                db.connect();
                result = true;
            } catch (TableRegistrationException e) {
                Common.getInstance().getLogger().severe("Unable to register iConomy tables. Reason: " + e.getMessage());
            } catch (ConnectionException e) {
                Common.getInstance().getLogger().severe("Unable to connect to iConomy database. Reason: " + e.getMessage());
            }
        }
        return result;
    }

    /**
     * Allow to load a flatfile database.
     *
     * @return True if the file is open. Else false.
     */
    private boolean loadFile() {
        boolean result = false;
        File dbFile = new File(Common.getInstance().getServerCaller().getDataFolder(), getDbConnectInfo().get("filename"));
        if (dbFile.exists()) {
            try {
                flatFileReader = new BufferedReader(new FileReader(dbFile));
                result = true;
            } catch (FileNotFoundException e) {
                Common.getInstance().getLogger().severe("iConomy database file not found!");
            }
        }
        return result;
    }

    /**
     * Allow to load a MySQL database.
     */
    private void loadMySQL() {
        try {
            MySQLConfiguration config = new MySQLConfiguration();
            config.setHost(getDbConnectInfo().get("address"));
            config.setUser(getDbConnectInfo().get("username"));
            config.setPassword(getDbConnectInfo().get("password"));
            config.setDatabase(getDbConnectInfo().get("database"));
            config.setPort(Integer.parseInt(getDbConnectInfo().get("port")));
            db = DatabaseFactory.createNewDatabase(config);
        } catch (NumberFormatException e) {
            Common.getInstance().getLogger().severe("Illegal Port!");
        }
    }

    /**
     * Allow to load a SQLite database.
     */
    private void loadSQLite() {
        SQLiteConfiguration config = new SQLiteConfiguration(Common.getInstance().getServerCaller().getDataFolder() + File.separator + getDbConnectInfo().get("filename"));
        db = DatabaseFactory.createNewDatabase(config);
    }

    @Override
    public boolean importData(String sender) {
        boolean result = false;
        if (flatFileReader != null) {
            result = importFlatFile(sender);
        } else if (db != null) {
            result = importDatabase(sender);
        }
        return result;
    }

    /**
     * Import accounts from a flatfile.
     *
     * @param sender The command sender so we can send back messages.
     * @return True if the convert is done. Else false.
     */
    private boolean importFlatFile(String sender) {
        boolean result = false;

        try {
            List<String> file = new ArrayList<String>();
            String str;
            while ((str = flatFileReader.readLine()) != null) {
                file.add(str);
            }
            flatFileReader.close();
            List<User> userList = new ArrayList<User>();
            for (String aFile : file) {
                String[] info = aFile.split(" ");
                try {
                    double balance = Double.parseDouble(info[1].split(":")[1]);
                    userList.add(new User(info[0], balance));
                } catch (NumberFormatException e) {
                    Common.getInstance().sendConsoleMessage(Level.SEVERE, "User " + info[0] + " have a invalid balance" + info[1]);
                } catch (ArrayIndexOutOfBoundsException e) {
                    Common.getInstance().sendConsoleMessage(Level.WARNING, "Line not formatted correctly. I read:" + Arrays.toString(info));
                }
            }
            addAccountToString(userList);
            addBalance(sender, userList);
            result = true;
        } catch (IOException e) {
            Common.getInstance().getLogger().severe("A error occured while reading the iConomy database file! Message: " + e.getMessage());
        }
        return result;
    }

    /**
     * Import accounts from the database.
     *
     * @param sender The command sender so we can send back messages.
     * @return True if the convert is done. Else false.
     */
    private boolean importDatabase(String sender) {
        List<IConomyTable> icoList = db.select(IConomyTable.class).execute().find();
        if (icoList != null && icoList.size() > 0) {
            Iterator<IConomyTable> icoListIterator = icoList.iterator();
            List<User> userList = new ArrayList<User>();
            while (icoListIterator.hasNext()) {
                IConomyTable entry = icoListIterator.next();
                userList.add(new User(entry.getUsername(), entry.getBalance()));
            }
            addAccountToString(userList);
            addBalance(sender, userList);
        }
        try {
            db.close();
        } catch (ConnectionException e) {
            Common.getInstance().getLogger().severe("Unable to disconnect from the iConomy database! Message: " + e.getMessage());
        }
        return true;
    }
}
