/*
 * Copyright (c) 2021 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.serverimpl.bukkit.cmd.util;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import work.lclpnet.serverimpl.bukkit.MCServerBukkit;

import java.util.List;

public abstract class CommandBase implements CommandExecutor, TabCompleter {

    public abstract String getCommandName();

    public abstract boolean canExecute(CommandSender sender);

    public abstract void execute(CommandSender sender, String[] args);

    public List<String> getTabCompletions(CommandSender sender, String[] args) {
        return null;
    }

    public boolean ensurePlayer(CommandSender sender) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(MCServerBukkit.pre + ChatColor.RED + "You must be a player to do that.");
            return false;
        }
        return true;
    }

    public boolean ensureOp(CommandSender sender) {
        if(!sender.isOp()) {
            sender.sendMessage(MCServerBukkit.pre + ChatColor.RED + "You are not allowed to do that.");
            return false;
        }
        return true;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(command.getName().equalsIgnoreCase(getCommandName())) {
            if(canExecute(sender)) execute(sender, args);
            return true;
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        if(command.getName().equalsIgnoreCase(getCommandName())) {
            if(canExecute(sender)) return getTabCompletions(sender, args);
            else return null;
        }

        return null;
    }

}
