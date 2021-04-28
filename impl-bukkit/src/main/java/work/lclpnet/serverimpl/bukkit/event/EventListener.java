/*
 * Copyright (c) 2021 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.serverimpl.bukkit.event;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import work.lclpnet.serverimpl.bukkit.MCServerBukkit;

public class EventListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        MCServerBukkit.getAPI().updateLastSeen(p.getUniqueId().toString()).thenAccept(success -> {
            if(!success)
                MCServerBukkit.getPlugin().getLogger().warning(String.format("Could not update last seen for player '%s'.", p.getName()));
        });
    }

}
