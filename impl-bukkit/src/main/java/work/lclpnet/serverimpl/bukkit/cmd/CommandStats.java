/*
 * Copyright (c) 2021 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.serverimpl.bukkit.cmd;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;
import work.lclpnet.lclpnetwork.facade.MCStats;
import work.lclpnet.serverapi.cmd.StatsCommandScheme;
import work.lclpnet.serverapi.translate.MCMessage;
import work.lclpnet.serverimpl.bukkit.MCServerBukkit;
import work.lclpnet.serverimpl.bukkit.cmd.util.PlatformCommandSchemeBase;
import work.lclpnet.serverimpl.bukkit.util.BukkitMCMessageImplementation;
import work.lclpnet.serverimpl.bukkit.util.StatsDisplay;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static work.lclpnet.serverimpl.bukkit.util.BukkitServerTranslation.getTranslation;

public class CommandStats extends PlatformCommandSchemeBase<Boolean> implements StatsCommandScheme {

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        String uuid = player.getUniqueId().toString();
        if(args.length == 0) execute(uuid, new Object[0]);
        else if(args.length == 1) execute(uuid, new Object[] { args[0] });
        else player.sendMessage(MCServerBukkit.pre + ChatColor.RED
                    + getTranslation(player, "stats.usage", getCommandName()));
    }

    @Override
    public void openStats(String invokerUuid, String targetUuid, MCMessage titleMsg, MCStats targetStats) {
        final Player invoker = Bukkit.getPlayer(UUID.fromString(invokerUuid));
        if(invoker == null) throw new NullPointerException("Invoker is null");

        String title = BukkitMCMessageImplementation.convertMCMessageToString(titleMsg, invoker);

        List<MCStats.Entry> entries = new ArrayList<>(targetStats.getStats());
        MCStats.Entry mainEntry = targetStats.getModule("general");
        if(mainEntry != null) entries.remove(mainEntry);

        Inventory inv = StatsDisplay.createStatsInv(title, mainEntry, entries, 0, invoker, null);

        // Open inventory must be called from the main thread.
        new BukkitRunnable() {
            @Override
            public void run() {
                invoker.openInventory(inv);
            }
        }.runTask(MCServerBukkit.getPlugin());
    }

}
