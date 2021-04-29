/*
 * Copyright (c) 2021 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.serverimpl.bukkit.cmd;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitCommands {

    public static void register(JavaPlugin plugin) {
        registerCommand(plugin, "mclink", new CommandMCLink());
        registerCommand(plugin, "stats", new CommandStats());
    }

    private static void registerCommand(JavaPlugin plugin, String name, CommandExecutor executor) {
        if(name == null) throw new NullPointerException("Command name is null.");

        PluginCommand cmd = plugin.getCommand(name);
        if(cmd == null) throw new NullPointerException("There is no command with name '" + name + "'registered.");

        cmd.setExecutor(executor);
    }

}
