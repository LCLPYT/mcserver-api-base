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
import org.bukkit.inventory.Inventory;
import work.lclpnet.serverimpl.bukkit.MCServerBukkit;
import work.lclpnet.serverimpl.bukkit.util.StatsManager;

public class EventListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        MCServerBukkit.getAPI().updateLastSeen(p.getUniqueId().toString()).thenAccept(success -> {
            if(!success)
                MCServerBukkit.getPlugin().getLogger().warning(String.format("Could not update last seen for player '%s'.", p.getName()));
        });
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onStatsInv(InventoryClickEvent e) {
        if(StatsManager.isStatsInventory(e.getInventory())) e.setCancelled(true);
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
