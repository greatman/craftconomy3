package com.greatmancode.craftconomy3.converter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.account.Account;
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
		try {
			int i = 0;
			while (line != null) {
				if (i % ALERT_EACH_X_ACCOUNT == 0) {
					Common.getInstance().getServerCaller().sendMessage(sender, i + " {{DARK_GREEN}}accounts imported.");
				}
				line = flatFileReader.readLine();
				if (line != null && Pattern.compile("[?a-zA-Z0-9\\s{_-]+").matcher(line).matches()) {
					String username = line.split(" ")[0];
					line = flatFileReader.readLine();
					// Line for account type
					String type = line.split(" ")[1];
					System.out.println("Adding " + username);
					if (type.equalsIgnoreCase("player")) {
						System.out.println("It's a player");
						line = flatFileReader.readLine();
						double amount = Double.parseDouble(line.split(" ")[1]);
						Common.getInstance().getAccountManager().getAccount(username).set(amount, Common.getInstance().getServerCaller().getDefaultWorld(), Common.getInstance().getCurrencyManager().getCurrency(CurrencyManager.defaultCurrencyID).getName());
						i++;
					} else if (type.equalsIgnoreCase("bank")) {
						System.out.println("It's a bank");
						line = flatFileReader.readLine();
						double amount = Double.parseDouble(line.split(" ")[1]);
						Common.getInstance().getAccountManager().getAccount(Account.BANK_PREFIX + username).set(amount, Common.getInstance().getServerCaller().getDefaultWorld(), Common.getInstance().getCurrencyManager().getCurrency(CurrencyManager.defaultCurrencyID).getName());
						line = flatFileReader.readLine();
						if (line.contains("members")) {
							line = flatFileReader.readLine();
							line = line.replaceAll("\\t+", "");
							while (!line.equals("}")) {
								System.out.println("MEMBER: " + line);
								Common.getInstance().getAccountManager().getAccount(Account.BANK_PREFIX + username).getAccountACL().set(line, true, true, false, true, false);
								line = flatFileReader.readLine();
								line = line.replaceAll("\\t+", "");
							}
						}
						line = flatFileReader.readLine();
						if (line.contains("owners")) {
							line = flatFileReader.readLine();
							line = line.replaceAll("\\t+", "");
							while (!line.equals("}")) {
								Common.getInstance().getAccountManager().getAccount(Account.BANK_PREFIX + username).getAccountACL().set(line, true, true, true, true, true);
								line = flatFileReader.readLine();
								line = line.replaceAll("\\t+", "");
							}
						}
					}
				}

			}
			flatFileReader.close();
		} catch (IOException e) {
			Common.getInstance().getLogger().severe("Error while reading bose file!" + e.getMessage());
		}
		return true;

	}

}
