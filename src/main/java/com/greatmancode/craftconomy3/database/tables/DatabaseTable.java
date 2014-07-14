package com.greatmancode.craftconomy3.database.tables;

/**
 * Created by greatman on 2014-07-13.
 */
public class DatabaseTable {

    private String prefix;

    public DatabaseTable(String prefix) {
        this.prefix = prefix;
    }

    protected String getPrefix() {
        return prefix;
    }
}
