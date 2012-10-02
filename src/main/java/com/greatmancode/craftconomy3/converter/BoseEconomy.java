package com.greatmancode.craftconomy3.converter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.currency.CurrencyManager;

public class BoseEconomy extends Converter {

	private BufferedReader flatFileReader = null;

	public BoseEconomy() {
		dbTypes.add("flatfile");
	}

	@Override
	public List<String> getDbInfo() {
		dbInfo.add("filename");
		return dbInfo;
	}

	@Override
	public boolean connect() {
		boolean result = false;
		File dbFile = new File(Common.getInstance().getServerCaller().getDataFolder(), dbConnectInfo.get("filename"));
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

	@Override
	public boolean importData(String sender) {
		String line = "";
		boolean isInUser = false;
		String username = "";
		try {
			while (line != null) {

				if (!isInUser) {
					line = flatFileReader.readLine();
					if (line != null)
					{
						System.out.println("LINE FOUND:" + line);
						if (Pattern.compile("[a-zA-Z0-9\\s{]+").matcher(line).matches()) {
							username = line.split(" ")[0];
							System.out.println("WE GOT A MATCH!: " + username);
							isInUser = true;
						}
					}
				} else {
					flatFileReader.readLine();
					line = flatFileReader.readLine();
					if (line.contains("money")) {
						double amount = Double.parseDouble(line.split(" ")[1]);
						Common.getInstance().getAccountManager().getAccount(username).set(amount, Common.getInstance().getServerCaller().getDefaultWorld(), Common.getInstance().getCurrencyManager().getCurrency(CurrencyManager.defaultCurrencyID).getName());
						isInUser = false;
					}
				}
			}
		} catch (IOException e) {
			Common.getInstance().getLogger().severe("Error while reading bose file!" + e.getMessage());
		}
		return true;

	}

}
