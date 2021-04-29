/*
 * Copyright (c) 2021 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.serverimpl.bukkit.cmd;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import work.lclpnet.serverapi.MCServerAPI;
import work.lclpnet.serverapi.cmd.MCLinkCommandScheme;
import work.lclpnet.serverapi.util.IPlatformBridge;
import work.lclpnet.serverimpl.bukkit.MCServerBukkit;
import work.lclpnet.serverimpl.bukkit.util.BukkitPlatformBridge;

public class CommandMCLink extends CommandBase implements MCLinkCommandScheme {

    public CommandMCLink(String name) {
        super(name);
    }

    @Override
    public boolean canExecute(CommandSender sender) {
        return ensurePlayer(sender);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        execute(player.getUniqueId().toString(), null);
    }

    @Override
    public MCServerAPI getAPI() {
        return MCServerBukkit.getAPI();
    }

    @Override
    public IPlatformBridge getPlatformBridge() {
        return BukkitPlatformBridge.getInstance();
    }
}