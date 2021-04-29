/*
 * Copyright (c) 2021 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.serverimpl.bukkit.cmd;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import work.lclpnet.lclpnetwork.facade.MCStats;
import work.lclpnet.serverapi.cmd.StatsCommandScheme;
import work.lclpnet.serverapi.util.MCMessage;
import work.lclpnet.serverimpl.bukkit.MCServerBukkit;
import work.lclpnet.serverimpl.bukkit.util.BukkitMCMessageImplementation;
import work.lclpnet.serverimpl.bukkit.util.ServerTranslations;
import work.lclpnet.serverimpl.bukkit.util.StatsManager;

import java.util.UUID;

public class CommandStats extends PlatformCommandSchemeBase implements StatsCommandScheme {

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        String uuid = player.getUniqueId().toString();
        if(args.length == 0) execute(uuid, new Object[0]);
        else if(args.length == 1) execute(uuid, new Object[] { args[0] });
        else player.sendMessage(MCServerBukkit.pre + ChatColor.RED
                    + ServerTranslations.getTranslation(player, "stats.usage", getCommandName()));
    }

    @Override
    public void openStats(String invokerUuid, String targetUuid, MCMessage titleMsg, MCStats targetStats) {
        final Player invoker = Bukkit.getPlayer(UUID.fromString(invokerUuid));
        if(invoker == null) throw new NullPointerException("Invoker is null");

        String title = BukkitMCMessageImplementation.convertMCMessageToString(titleMsg, invoker);

        final Inventory inv = Bukkit.createInventory(null,54, ChatColor.stripColor(title));
        StatsManager.markAsStats(inv);

        inv.setItem(4, new ItemStack(Material.DIRT));

        // Open inventory must be called from the main thread.
        new BukkitRunnable() {
            @Override
            public void run() {
                invoker.openInventory(inv);
            }
        }.runTask(MCServerBukkit.getPlugin());
    }

}
