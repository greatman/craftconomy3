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
			if (args.length == 0) {
				
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
					} else {
						//Common.getInstance().getServerCaller().sendMessage(sender, "{{")
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
