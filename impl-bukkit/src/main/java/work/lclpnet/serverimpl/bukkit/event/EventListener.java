/*
 * Copyright (c) 2021 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.serverimpl.bukkit.event;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import work.lclpnet.lclpnetwork.facade.MCStats;
import work.lclpnet.serverapi.util.ServerCache;
import work.lclpnet.serverimpl.bukkit.MCServerBukkit;
import work.lclpnet.serverimpl.bukkit.cmd.CommandStats;
import work.lclpnet.serverimpl.bukkit.util.StatsManager;

import java.util.ArrayList;
import java.util.List;

public class EventListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        MCServerBukkit.getAPI().updateLastSeen(p.getUniqueId().toString()).thenAccept(player -> {
            if(player == null)
                MCServerBukkit.getPlugin().getLogger().warning(String.format("Could not update last seen for player '%s'.", p.getName()));
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        ServerCache.dropAllCachesFor(p.getUniqueId().toString());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onStatsInv(InventoryClickEvent e) {
        StatsManager.StatsInventory statsInv = StatsManager.getStatsInventory(e.getInventory());
        if(statsInv == null) return; // not a stats inventory

        e.setCancelled(true);

        ItemStack is = e.getCurrentItem();
        if(is == null) return;

        Player player = (Player) e.getWhoClicked();

        MCStats.Entry group = statsInv.getItemStackGroup(is);
        if(group != null) {
            if(group.getChildren() == null) return;

            List<MCStats.Entry> items = new ArrayList<>(group.getChildren());
            items.remove(group);

            Inventory inv = CommandStats.createStatsInv(
                    statsInv.getTitle(),
                    group,
                    items,
                    0,
                    player,
                    statsInv
            );
            player.openInventory(inv);
        } else if(is.equals(statsInv.getNextPageItem())) {
            Inventory inv = CommandStats.createStatsInv(
                    statsInv.getTitle(),
                    statsInv.getMainEntry(),
                    statsInv.getItems(),
                    statsInv.getPage() + 1,
                    player,
                    statsInv.getParent()
            );
            player.openInventory(inv);
        } else if(is.equals(statsInv.getPrevPageItem())) {
            Inventory inv = CommandStats.createStatsInv(
                    statsInv.getTitle(),
                    statsInv.getMainEntry(),
                    statsInv.getItems(),
                    statsInv.getPage() - 1,
                    player,
                    statsInv.getParent()
            );
            player.openInventory(inv);
        } else if(is.equals(statsInv.getBackItem())) {
            StatsManager.StatsInventory parent = statsInv.getParent();
            if(parent == null) return;

            Inventory inv = CommandStats.createStatsInv(
                    parent.getTitle(),
                    parent.getMainEntry(),
                    parent.getItems(),
                    parent.getPage(),
                    player,
                    parent.getParent()
            );
            player.openInventory(inv);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onStatsInvDrag(InventoryDragEvent e) {
        if(StatsManager.isStatsInventory(e.getInventory())) e.setCancelled(true);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        Inventory inv = e.getInventory();
        if(StatsManager.isStatsInventory(inv)) StatsManager.removeStatsMarker(inv);
    }

}
