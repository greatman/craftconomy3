/*
 * This file is part of Craftconomy3.
 *
 * Copyright (c) 2011-2013, Greatman <http://github.com/greatman/>
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
package com.greatmancode.craftconomy3.commands.setup;

import java.util.HashMap;
import java.util.Map;

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.NewSetupWizard;
import com.greatmancode.craftconomy3.commands.interfaces.CraftconomyCommand;
import com.greatmancode.craftconomy3.database.tables.ConfigTable;

public class NewSetupCurrencyCommand extends CraftconomyCommand {
	private enum INTERNALSTEP {
		NAME,
		NAMEPLURAL,
		MINOR,
		MINORPLURAL,
		SIGN,
		CC2;
	}

	private Map<String, String> map = new HashMap<String, String>();

	@Override
	public void execute(String sender, String[] args) {

		try {
			INTERNALSTEP step = INTERNALSTEP.valueOf(args[0].toUpperCase());

			if (step.equals(INTERNALSTEP.CC2)) {
				cc2(sender, args[1]);
			} else if (step.equals(INTERNALSTEP.NAME)) {
				name(sender, args[1]);
			} else if (step.equals(INTERNALSTEP.NAMEPLURAL)) {
				namePlural(sender, args[1]);
			} else if (step.equals(INTERNALSTEP.MINOR)) {
				minor(sender, args[1]);
			} else if (step.equals(INTERNALSTEP.MINORPLURAL)) {
				minorPlural(sender, args[1]);
			} else if (step.equals(INTERNALSTEP.SIGN)) {
				sign(sender, args[1]);
			}
		} catch (IllegalArgumentException e) {
			Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_RED}}Invalid sub-step! Please write a valid one.");
		}
	}

	@Override
	public String help() {
		return "/ccsetup currency - Configure the first currency";
	}

	@Override
	public int maxArgs() {
		return 2;
	}

	@Override
	public int minArgs() {
		return 2;
	}

	@Override
	public boolean playerOnly() {
		return false;
	}

	@Override
	public String getPermissionNode() {
		return "craftconomy.setup";
	}

	private void cc2(String sender, String response) {
		if (response.equals("yes")) {
			Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}Let's skip this step then! Please type {{WHITE}}/ccsetup basic");
			NewSetupWizard.setState(NewSetupWizard.BASIC_STEP);
		} else if (response.equals("no")) {
			Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}Alright! Welcome to Craftconomy! We use a Multi-Currency system. I need you to write the settings for the default currency.");
			Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}First, let's configure the {{WHITE}}main currency name {{DARK_GREEN}}(Ex: {{WHITE}}Dollar{{DARK_GREEN}}). Type {{WHITE}}/ccsetup currency name <Name>");
		} else {
			Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_RED}}Valid values are: {{WHITE}}/ccsetup currency cc2 yes {{DARK_RED}}or {{WHITE}}/ccsetup currency cc2 no");
		}
	}

	private void name(String sender, String name) {
		map.put("name", name);
		Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}Now, let's configure the currency name but in {{WHITE}}Plural {{DARK_GREEN}}(Ex: {{WHITE}}Dollars{{DARK_GREEN}}). Please type {{WHITE}}/ccsetup currency nameplural <Plural>");
		done(sender);
	}

	private void namePlural(String sender, String namePlural) {
		map.put("nameplural", namePlural);
		Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}Now, let's configure the currency name but for the {{WHITE}}minor {{DARK_GREEN}}(Ex: {{WHITE}}Coin{{DARK_GREEN}}). Please type {{WHITE}}/ccsetup currency minor <Minor>");
		done(sender);
	}

	private void minor(String sender, String minor) {
		map.put("minor", minor);
		Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}Now, let's configure the currency name but for the {{WHITE}}minor in plural {{DARK_GREEN}}(Ex: {{WHITE}}Coins{{DARK_GREEN}}). Please type {{WHITE}}/ccsetup currency minorplural <Minor plural>");
		done(sender);
	}

	private void minorPlural(String sender, String minorPlural) {
		map.put("minorplural", minorPlural);
		Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}Finally, let's put a {{WHITE}}sign {{DARK_GREEN}}on that currency (Ex: {{WHITE}}$ {{DARK_GREEN}}). Please type {{WHITE}}/ccsetup currency sign <Sign>");
		done(sender);
	}

	private void sign(String sender, String sign) {
		map.put("sign", sign);
		done(sender);
	}

	private void done(String sender) {
		if (map.size() == 5) {
			Common.getInstance().getCurrencyManager().addCurrency(map.get("name"), map.get("nameplural"), map.get("minor"), map.get("minorplural"), 0.0, map.get("sign"), true);
			int dbId = Common.getInstance().getCurrencyManager().getCurrency(map.get("name")).getDatabaseID();
			Common.getInstance().getCurrencyManager().setDefault(dbId);
			ConfigTable table = new ConfigTable();
			table.setName("bankcurrency");
			table.setValue(dbId + "");
			Common.getInstance().getDatabaseManager().getDatabase().save(table);
			Common.getInstance().getServerCaller().sendMessage(sender, "{{DARK_GREEN}}We are done for that step! Only 2 remaining! Please type {{WHITE}}/ccsetup basic");
			NewSetupWizard.setState(NewSetupWizard.BASIC_STEP);
		}
	}
}
