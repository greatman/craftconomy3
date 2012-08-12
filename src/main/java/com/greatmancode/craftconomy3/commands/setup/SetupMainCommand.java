package com.greatmancode.craftconomy3.commands.setup;

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.SetupWizard;
import com.greatmancode.craftconomy3.commands.CraftconomyCommand;

public class SetupMainCommand implements CraftconomyCommand {

	@Override
	public void execute(String sender, String[] args) {
		// TODO Auto-generated method stub
		Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}Welcome to the Craftconomy setup wizard.");
		Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}This setup will help you configure Craftconomy like you want.");
		Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}To continue, type {{WHITE}} /ccsetup database");
		SetupWizard.setState(1);
	}

	@Override
	public boolean permission(String sender) {
		return Common.getInstance().getServerCaller().checkPermission(sender, "craftconomy.setup");
	}

	@Override
	public String help() {
		return "/ccsetup - Start the setup";
	}

	@Override
	public int maxArgs() {
		return 0;
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
