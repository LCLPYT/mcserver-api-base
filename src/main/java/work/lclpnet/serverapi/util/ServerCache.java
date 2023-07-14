/*
 * Copyright (c) 2023 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.serverapi.util;

import work.lclpnet.lclpnetwork.ext.LCLPMinecraftAPI;
import work.lclpnet.lclpnetwork.facade.MCPlayer;
import work.lclpnet.serverapi.MCServerAPI;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class ServerCache {

    private final Map<String, MCPlayer> playersByUuid = new HashMap<>();
    private final List<String> registeredLanguages = new ArrayList<>();

    public void cachePlayer(MCPlayer player) {
        Objects.requireNonNull(player);
        playersByUuid.put(player.getUuid(), player);
    }

    public void removeCachedPlayer(String uuid) {
        Objects.requireNonNull(uuid);
        playersByUuid.remove(uuid);
    }

    @Nullable
    public MCPlayer getPlayer(String uuid) {
        Objects.requireNonNull(uuid);
        return playersByUuid.get(uuid);
    }

    public List<String> getRegisteredLanguages() {
        return registeredLanguages;
    }

    public CompletableFuture<Void> refreshRegisteredLanguages(MCServerAPI api) {
        return api.getRegisteredLanguages().thenAccept(languages -> {
            registeredLanguages.clear();

            if (languages != null) {
                registeredLanguages.addAll(languages);
            }
        });
    }

    public CompletableFuture<Void> refreshPlayer(LCLPMinecraftAPI api, String uuid) {
        return api.getMCPlayerByUUID(uuid).thenAccept(player -> {
            if (player != null) cachePlayer(player);
        });
    }

    /* Those methods should be called from somewhere in the implementation */

    /**
     * Initializes the {@link ServerCache}.
     * Implementations should call this method on their initialization.
     *
     * @param api    A {@link MCServerAPI} instance to use for fetching data.
     */
    public void init(MCServerAPI api) {
        refreshRegisteredLanguages(api);  // language refresh can run async
    }

    /**
     * Removes all cache items for the given player UUID.
     * Implementations should call this, if a player leaves the server.
     *
     * @param uuid The player UUID.
     */
    public void dropAllCachesFor(String uuid) {
        removeCachedPlayer(uuid);
    }
}
