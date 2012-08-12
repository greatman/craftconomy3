package com.greatmancode.craftconomy3;

public class SetupWizard {

	/**
	 * Possible states:
	 * 0 = Setup not started
	 * 1 = Basic state, waiting for database information (After the welcome msg)
	 * 2 = Database set. Waiting for multiworld information
	 * 3 = Multiworld set. Waiting for currency information
	 * 4 = Currency set. Waiting for basic settings (Bank price, default money, etc)
	 * 5 = Basic settings done. Asking for convert
	 * 6 = done
	 */
	private static int state = 0;

	public static void setState(int newState) {
		state = newState;
	}
	
	public static int getState() {
		return state;
	}
}
