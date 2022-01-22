/*
 * Copyright (c) 2022 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.serverapi.util;

import work.lclpnet.lclpnetwork.facade.MCPlayer;
import work.lclpnet.serverapi.MCServerAPI;
import work.lclpnet.translations.Translations;
import work.lclpnet.translations.network.LCLPNetworkTranslationLoader;
import work.lclpnet.translations.network.LCLPTranslationAPI;
import work.lclpnet.translations.util.ILogger;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class ServerCache {

    private static final Map<String, MCPlayer> playersByUuid = new HashMap<>();
    private static final List<String> registeredLanguages = new ArrayList<>();

    public static void cachePlayer(MCPlayer player) {
        Objects.requireNonNull(player);
        playersByUuid.put(player.getUuid(), player);
    }

    public static void removeCachedPlayer(String uuid) {
        Objects.requireNonNull(uuid);
        playersByUuid.remove(uuid);
    }

    @Nullable
    public static MCPlayer getPlayer(String uuid) {
        Objects.requireNonNull(uuid);
        return playersByUuid.get(uuid);
    }

    public static List<String> getRegisteredLanguages() {
        return registeredLanguages;
    }

    public static CompletableFuture<Void> refreshRegisteredLanguages(MCServerAPI api) {
        return api.getRegisteredLanguages().thenAccept(languages -> {
            registeredLanguages.clear();
            if(languages != null) registeredLanguages.addAll(languages);
        });
    }

    public static CompletableFuture<Void> reloadTranslations(LCLPTranslationAPI api, @Nullable ILogger logger) throws IOException {
        LCLPNetworkTranslationLoader loader = new LCLPNetworkTranslationLoader(Collections.singletonList("mc_server"), null, api, logger);
        return Translations.loadAsyncFrom(loader);
    }

    public static CompletableFuture<Void> refreshPlayer(MCServerAPI api, String uuid) {
        return api.getMCPlayerByUUID(uuid).thenAccept(player -> {
            if(player != null) cachePlayer(player);
        });
    }

    /* Those methods should be called from somewhere in the implementation */

    /**
     * Initializes the {@link ServerCache}.
     * Implementations should call this method on their initialization.
     *
     * @param api A {@link MCServerAPI} instance to use for fetching data.
     * @param logger An optional logger to receive information.
     * @throws IOException If there was an I/O-error in the initialization process.
     */
    public static void init(MCServerAPI api, @Nullable ILogger logger) throws IOException {
        refreshRegisteredLanguages(api).thenAccept(ignored -> {}); // language refresh can run async
        reloadTranslations(new LCLPTranslationAPI(api.getAPIAccess()), logger).join(); // should run synchronous
    }

    /**
     * Removes all cache items for the given player UUID.
     * Implementations should call this, if a player leaves the server.
     * @param uuid The player UUID.
     */
    public static void dropAllCachesFor(String uuid) {
        removeCachedPlayer(uuid);
    }

}
