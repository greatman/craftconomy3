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
package com.greatmancode.craftconomy3.commands.pay;

import com.greatmancode.tools.commands.interfaces.Command;
import com.greatmancode.tools.commands.CommandHandler;
import com.greatmancode.tools.commands.SubCommand;
import com.greatmancode.tools.commands.interfaces.CommandExecutor;
import lombok.Getter;

import java.util.*;

public class PayShortCommand extends SubCommand {
    private Map<String, Command> commandList = new HashMap<String, Command>();
    private CommandHandler commandHandler;
    private SubCommand parent;
    @Getter
    private String name;
    private int level;
    private String subCommandList;

    public PayShortCommand(String name, CommandHandler commandHandler, SubCommand parent, int level){
      super(name, commandHandler, parent, level);
      this.name = name;
      this.commandHandler = commandHandler;
      this.parent = parent;
      this.level = level;
    }

    @Override
    public void addCommand(String name, Command command) {
      commandList.put(name, command);
      if (command instanceof CommandExecutor) {
        commandHandler.getServerCaller().registerPermission(((CommandExecutor) command).getPermissionNode());
      }
      super.addCommand(name, command);
    }

    @Override
    public boolean commandExist(String name) {
        return true;
    }

    /*
    * Command contains the Player name
    */
    @Override
    public void execute(String command, String sender, String[] args) {
        if (level <= commandHandler.getCurrentLevel()) {
            if (commandExist(command)) {
                Command entry = commandList.get("");
                if (entry instanceof CommandExecutor) {
                    CommandExecutor cmd = ((CommandExecutor) entry);
                    if (commandHandler.getServerCaller().getPlayerCaller().checkPermission(sender, cmd.getPermissionNode())) {
                        if (cmd.playerOnly() && sender.equalsIgnoreCase("console")) {
                            commandHandler.getServerCaller().getPlayerCaller().sendMessage(sender, "{{DARK_RED}}Only players can do this command!");
                            return;
                        }
                        if (args.length >= cmd.minArgs() - 1 && args.length <= cmd.maxArgs() - 1) {
                            String[] newArgs = new String[args.length + 1];
                            newArgs[0] = command;
                            System.arraycopy(args, 0, newArgs, 1, args.length);
                            cmd.execute(sender, newArgs);
                        } else {
                            commandHandler.getServerCaller().getPlayerCaller().sendMessage(sender, cmd.help());
                        }
                    } else {
                        commandHandler.getServerCaller().getPlayerCaller().sendMessage(sender, "{{DARK_RED}}Permission denied!");
                    }
                } else if (entry instanceof SubCommand) {
                    SubCommand subCommand = (SubCommand) entry;

                    String subSubCommand = "";
                    if (args.length != 0) {
                        subSubCommand = args[0];
                    }

                    if (subCommand.commandExist(subSubCommand)) {
                        String[] newArgs;
                        if (args.length == 0) {
                            newArgs = args;
                        } else {
                            newArgs = new String[args.length - 1];
                            System.arraycopy(args, 1, newArgs, 0, args.length - 1);
                        }
                        ((SubCommand) entry).execute(subSubCommand, sender, newArgs);
                    }
                }
            }
        } else {
            commandHandler.getServerCaller().getPlayerCaller().sendMessage(sender, commandHandler.getWrongLevelMsg());
        }
    }
}
