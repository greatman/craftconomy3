/*
 * This file is part of Craftconomy3.
 *
 * Copyright (c) 2011-2012, Greatman <http://github.com/greatman/>
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
package com.greatmancode.craftconomy3;


/**
 * Global class to keep a global variable for the Setup Wizard.
 * @author greatman
 *
 */
public enum SetupWizard {

	NOT_STARTED,
	DATABASE_SETUP,
	MULTIWORLD_SETUP,
	CURRENCY_SETUP,
	BASIC_SETUP,
	CONVERT_SETUP;
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
	private static SetupWizard state = NOT_STARTED;

	/**
	 * Set the wizard state.
	 * @param newState The new state to set to.
	 */
	public static void setState(SetupWizard newState) {
		state = newState;
	}

	/**
	 * Retrieve the Wizard state.
	 */
	public static SetupWizard getState() {
		return state;
	}
}
