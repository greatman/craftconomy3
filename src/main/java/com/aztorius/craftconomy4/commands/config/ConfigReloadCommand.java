/**
 * This file is part of Craftconomy4.
 *
 * Copyright (c) 2011-2016, Greatman <http://github.com/greatman/>
 * Copyright (c) 2017, Aztorius <http://github.com/Aztorius/>
 *
 * Craftconomy4 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Craftconomy4 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Craftconomy4.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aztorius.craftconomy4.commands.config;

import com.aztorius.craftconomy4.Common;
import com.greatmancode.tools.commands.interfaces.CommandExecutor;

public class ConfigReloadCommand extends CommandExecutor {
    @Override
    public void execute(String sender, String[] args) {
        Common.getInstance().onDisable();
        Common.getInstance().reloadPlugin();
        Common.getInstance().getServerCaller().getPlayerCaller().sendMessage(sender, Common.getInstance().getLanguageManager().getString("craftconomy_reloaded"));
    }

    @Override
    public String help() {
        return Common.getInstance().getLanguageManager().getString("config_reload_help_cmd");
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

    @Override
    public String getPermissionNode() {
        return "craftconomy.config.reload";
    }
}
