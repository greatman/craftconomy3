package com.greatmancode.craftconomy3.commands.setup;

import java.util.Iterator;
import java.util.Map.Entry;

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.SetupWizard;
import com.greatmancode.craftconomy3.commands.CraftconomyCommand;
import com.greatmancode.craftconomy3.converter.Converter;
import com.greatmancode.craftconomy3.converter.ConverterList;

public class SetupConvertCommand implements CraftconomyCommand {

	private static ConverterList importerList = new ConverterList();
	private boolean inProgress = false;
	
	private Converter selectedConverter = null;
	private String importlist = "";
	private int status = 0;
	@Override
	public void execute(String sender, String[] args) {
		if (SetupWizard.getState() == 5) {
			if (args.length == 0 && status == 0) {
				
				Iterator<Entry<String, Converter>> iterator = ConverterList.converterList.entrySet().iterator();
				while (iterator.hasNext()) {
					importlist += iterator.next().getKey();
					if (iterator.hasNext()) {
						importlist += ", ";
					}
				}
				Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}Welcome to the import wizard. I currently support those systems: " + importlist);
				Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}If you wish to use this system. Type {{WHITE}}/ccsetup convert yes{{DARK_GREEN}}. Else type {{WHITE}}/ccsetup convert complete{{DARK_GREEN}} to finish the setup wizard.");
			} else if (args.length == 1) {
				if (args[0].equals("complete") && !inProgress) {
					Common.getInstance().getConfigurationManager().getConfig().setValue("System.Setup", false);
					Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}Alright! Enjoy Craftconomy!");
				} else if (args[0].equals("yes")) {
					inProgress = true;
					Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}Okay! Please type /ccsetup convert <" + importlist + ">");
				} else if (inProgress && status == 0) {
					if (ConverterList.converterList.containsKey(args[0])) {
						status = 1;
						selectedConverter = ConverterList.converterList.get(args[0]);
						String list = "";
						Iterator<String> dbTypesIterator = selectedConverter.getDbTypes().iterator();
						while (dbTypesIterator.hasNext()) {
							list += dbTypesIterator.next();
							if (dbTypesIterator.hasNext());
							list += ", ";
						}
						Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}You selected {{WHITE}}" + args[0] + "{{DARK_GREEN}}.");
						Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}You now need to select the database type you are using. Please type /ccsetup convert <" + list +" >");
						
					} else {
						Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_RED}}Unknown converter. Be sure to have written the name correctly!");
					}
					
				} else if (inProgress && status == 1)  {
					if (selectedConverter.setDbType(args[0])) {
						status = 2;
						String list = "";
						Iterator<String> dbTypesIterator = selectedConverter.getDbInfo().iterator();
						while (dbTypesIterator.hasNext()) {
							list += dbTypesIterator.next();
							if (dbTypesIterator.hasNext());
							list += ", ";
						}
						Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}You selected {{WHITE}}" + args[0]);
						Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}Now, you need to insert the database information.");
						Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}If you used a flatfile file or a flatfile database, that file need to be in the Craftconomy3 folder.");
						Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}Please type /ccsetup convert <" + list + "> <value>");
					} else {
						Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_RED}}Unknown database type.");
					}
				}
			} else if (args.length == 2) {
				if (inProgress && status == 2) {
					if (selectedConverter.setDbInfo(args[0], args[1])) {
						if (!selectedConverter.allSet()) {
							Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}Value saved. Please continue");
						} else {
							if (selectedConverter.connect()) {
								Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}All values are ok! Let's start this conversion!");
								if (selectedConverter.importData(sender)) {
									Common.getInstance().getConfigurationManager().getConfig().setValue("System.Setup", false);
									Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}Conversion complete! Enjoy Craftconomy!");
								} else {
									Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_RED}}Better report that to the dev. Your totally not supposed to be here.");
								}
							} else {
								Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_RED}}Some settings are wrong. Be sure that every settings are ok! Check the console log for more information.");
							}
							
						}
						
					} else {
						Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_RED}}Unknown field.");
					}
				}
			}
		}
		
	}

	@Override
	public boolean permission(String sender) {
		return Common.getInstance().getServerCaller().checkPermission(sender, "craftconomy.setup");
	}

	@Override
	public String help() {
		return "/ccsetup basic - Basic configuration";
	}

	@Override
	public int maxArgs() {
		return 2;
	}

	@Override
	public int minArgs() {
		return 0;
	}

	@Override
	public boolean playerOnly() {
		return false;
	}

}
