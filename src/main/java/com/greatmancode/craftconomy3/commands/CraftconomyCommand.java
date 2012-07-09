package com.greatmancode.craftconomy3.commands;

public interface CraftconomyCommand {

	public void execute(String sender, String[] args);
	
	public boolean permission(String sender);
	
	public String help();
	
	public int maxArgs();
	public int minArgs();
	
	public boolean playerOnly();
}
