/**
 * This file is part of Craftconomy3.
 *
 * Copyright (c) 2011-2016, Greatman <http://github.com/greatman/>
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
 * Different setup steps for the wizard
 */
public enum NewSetupWizard {
    DATABASE_STEP,
    BASIC_STEP,
    CURRENCY_STEP,
    CONVERT_STEP;
    private static NewSetupWizard state = BASIC_STEP;

    /**
     * Set the wizard state.
     *
     * @param newState The new state to set to.
     */
    public static void setState(NewSetupWizard newState) {
        state = newState;
    }

    /**
     * Retrieve the Wizard state.
     *
     * @return The state of the setup wizard
     */
    public static NewSetupWizard getState() {
        return state;
    }
}
