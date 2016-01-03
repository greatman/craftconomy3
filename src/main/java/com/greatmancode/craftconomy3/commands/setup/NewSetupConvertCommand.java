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
package com.greatmancode.craftconomy3.commands.setup;

import com.greatmancode.craftconomy3.Common;
import com.greatmancode.craftconomy3.NewSetupWizard;
import com.greatmancode.craftconomy3.converter.Converter;
import com.greatmancode.craftconomy3.converter.ConverterList;
import com.greatmancode.tools.commands.interfaces.CommandExecutor;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public class NewSetupConvertCommand extends CommandExecutor {
    private enum INTERNALSTEP {
        START,
        SELECT_CONVERT,
        SELECT_DB,
        INSERT_VALUES,
        CONVERT;
    }

    private static final ConverterList IMPORTER_LIST = new ConverterList();
    private static Converter selectedConverter = null;
    private INTERNALSTEP step = INTERNALSTEP.START;

    @Override
    public void execute(String sender, String[] args) {
        if (NewSetupWizard.getState().equals(NewSetupWizard.CONVERT_STEP)) {
            if (step.equals(INTERNALSTEP.START)) {
                start(sender, args);
            } else if (step.equals(INTERNALSTEP.SELECT_CONVERT)) {
                selectConvert(sender, args);
            } else if (step.equals(INTERNALSTEP.SELECT_DB)) {
                selectDb(sender, args);
            } else if (step.equals(INTERNALSTEP.INSERT_VALUES)) {
                selectValues(sender, args);
            }
        }
    }

    @Override
    public String help() {
        return "/ccsetup convert - Convert wizard.";
    }

    @Override
    public int maxArgs() {
        return 3;
    }

    @Override
    public int minArgs() {
        return 1;
    }

    @Override
    public boolean playerOnly() {
        return false;
    }

    @Override
    public String getPermissionNode() {
        return "craftconomy.setup";
    }

    private void selectValues(final String sender, String[] args) {
        if (args.length <= 2) {
            if (selectedConverter != null) {
                if (selectedConverter.setDbInfo(args[0], args[1])) {
                    if (selectedConverter.allSet()) {
                        //We start the convert!
                        if (selectedConverter.connect()) {
                            Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, "{{DARK_GREEN}}All values are ok! Let's start this conversion!");
                            Common.getInstance().getServerCaller().getSchedulerCaller().schedule(new Runnable() {
                                @Override
                                public void run() {
                                    Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, "{{DARK_RED}}NOTICE: {{WHITE}}The conversion is made in another thread so it doesn't hang the server. Craftconomy will be unlocked when the conversion is complete.");
                                    selectedConverter.importData(sender);
                                    Common.getInstance().getMainConfig().setValue("System.Setup", false);
                                    Common.getInstance().reloadPlugin();

                                    Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, "{{DARK_GREEN}}Conversion complete! Enjoy Craftconomy!");
                                }
                            }, 0, 0, true);
                        } else {
                            Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, "{{DARK_RED}}Some settings are wrong. Be sure that every settings are ok! Check the console log for more information.");
                        }
                    } else {
                        Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, "{{DARK_GREEN}}Value for {{WHITE}}" + args[0] + "{{DARK_GREEN}} set. Please continue.");
                    }
                }
            } else {
                Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, "{{DARK_RED}}Something is wrong. There isn't a converter selected!");
            }
        }
    }

    private void selectDb(String sender, String[] args) {
        if (selectedConverter.getDbTypes().contains(args[0])) {
            selectedConverter.setDbType(args[0]);
            step = INTERNALSTEP.INSERT_VALUES;
            if (selectedConverter.getDbInfo().size() == 0) {
                selectedConverter.importData(sender);
                Common.getInstance().getMainConfig().setValue("System.Setup", false);
                Common.getInstance().reloadPlugin();
                Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, "{{DARK_GREEN}}Conversion complete! Enjoy Craftconomy!");
            } else {
                Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, args[0] + " {{DARK_GREEN}}selected. Now, Please enter the correct values for the database format chosen. Syntax is: {{WHITE}}/ccsetup convert <" + formatListString(selectedConverter.getDbInfo()) + "> <value>");
                Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, "{{DARK_GREEN}}Example: {{WHITE}}/ccsetup convert " + selectedConverter.getDbInfo().get(0) + " test");
            }
        } else {
            Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, "{{DARK_RED}}This db type doesn't exist! Please type {{WHITE}}/ccsetup convert <" + formatListString(selectedConverter.getDbTypes()) + ">");
        }
    }

    private void selectConvert(String sender, String[] args) {
        if (IMPORTER_LIST.getConverterList().containsKey(args[0])) {
            selectedConverter = IMPORTER_LIST.getConverterList().get(args[0]);
            Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, "{{WHITE}}" + args[0] + " {{DARK_GREEN}}importer selected.");
            if (selectedConverter.getWarning() != null) {
                Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, "{{DARK_RED}}Warning{{WHITE}}: " + selectedConverter.getWarning());
            }
            if (selectedConverter.getDbTypes().size() == 1) {
                step = INTERNALSTEP.SELECT_DB;
                selectDb(sender, new String[]{selectedConverter.getDbTypes().get(0)});
            } else {
                Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, "{{DARK_GREEN}}This converter support those database types. Please select one");
                Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, "{{WHITE}}/ccsetup convert <" + formatListString(selectedConverter.getDbTypes()) + ">");
                step = INTERNALSTEP.SELECT_DB;
            }
        }
    }

    private void start(String sender, String[] args) {
        if ("yes".equalsIgnoreCase(args[0])) {
            step = INTERNALSTEP.SELECT_CONVERT;
            Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, "{{DARK_GREEN}}I currently support those systems: {{WHITE}}" + getConverterListFormatted());
            Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, "{{DARK_GREEN}}Please type {{WHITE}}/ccsetup convert <" + getConverterListFormatted() + ">");
        } else if ("no".equalsIgnoreCase(args[0])) {
            Common.getInstance().getMainConfig().setValue("System.Setup", false);
            Common.getInstance().reloadPlugin();
            Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, "{{DARK_GREEN}}The setup is done! Enjoy Craftconomy!");
        } else {
            Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, "{{DARK_RED}}Correct values are yes or no! Please type {{WHITE}}/ccsetup convert <yes/no>");
        }
    }

    private String getConverterListFormatted() {
        String result = "";
        Iterator<Entry<String, Converter>> iterator = IMPORTER_LIST.getConverterList().entrySet().iterator();
        while (iterator.hasNext()) {
            result += iterator.next().getKey();
            if (iterator.hasNext()) {
                result += ", ";
            }
        }
        return result;
    }

    private String formatListString(List<String> list) {
        String result = "";
        Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()) {
            result += iterator.next();
            if (iterator.hasNext()) {
                result += ", ";
            }
        }
        return result;
    }
}
