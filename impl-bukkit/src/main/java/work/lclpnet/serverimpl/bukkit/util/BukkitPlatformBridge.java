/*
 * Copyright (c) 2021 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.serverimpl.bukkit.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import work.lclpnet.serverapi.util.IPlatformBridge;
import work.lclpnet.serverapi.util.MCMessage;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class BukkitPlatformBridge implements IPlatformBridge {

    private static final BukkitPlatformBridge instance = new BukkitPlatformBridge();

    public static BukkitPlatformBridge getInstance() {
        return instance;
    }

    @Override
    public void sendMessageTo(String playerUuid, MCMessage msg) {
        Player p = Bukkit.getPlayer(UUID.fromString(playerUuid));
        if(p == null) throw new IllegalStateException(String.format("There is no player with UUID '%s' online!", playerUuid));
        p.sendMessage(BukkitMCMessageImplementation.convertMCMessageToString(msg, p).replaceAll("http://localhost:8000", "https://lclpnet.work"));
    }

    @Override
    public CompletableFuture<String> getPlayerNameByUUID(String uuid) {
        Player online = Bukkit.getPlayer(UUID.fromString(uuid));
        if (online != null) return CompletableFuture.completedFuture(online.getName());
        else return MojangAPI.getUsernameByUUID(uuid);
    }

    @Override
    public CompletableFuture<String> getPlayerUUIDByName(String name) {
        Player online = Bukkit.getPlayer(name);
        if(online != null) return CompletableFuture.completedFuture(online.getUniqueId().toString());
        else return MojangAPI.getUUIDByUsername(name);
    }

}
