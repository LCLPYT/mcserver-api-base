/*
 * Copyright (c) 2022 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.serverapi.util;

import work.lclpnet.lclpnetwork.facade.MCPlayer;
import work.lclpnet.serverapi.MCServerAPI;
import work.lclpnet.serverapi.translate.MCMessage;

import java.util.concurrent.CompletableFuture;

public interface IPlatformBridge {

    void sendMessageTo(String playerUuid, MCMessage msg);

    /**
     * Gets the current name of the Minecraft account with the given UUID.
     * The server implementation should check for any online players with the {@link MCPlayer}'s UUID first, in order to save time.
     * If there is no player with that UUID online, it needs to be fetched from the Mojang API.
     *
     * @param playerUuid The player UUID to get the name from.
     * @return A completable future that will receive the fetched player name.
     */
    CompletableFuture<String> getPlayerNameByUUID(String playerUuid);

    /**
     * Fetches the UUID of a minecraft username.
     * The server implementation should check for any online players with that username first, in order to save time.
     * If there is no player with that username online, the UUID needs to be fetched from the Mojang API.
     *
     * @param name The username.
     * @return A completable future that will receive the UUID.
     */
    CompletableFuture<String> getPlayerUUIDByName(String name);

    /**
     * Fetches a {@link MCPlayer} by their current username.
     *
     * @param name The player's username.
     * @param api An API instance to use.
     * @return A completable future that will receive the fetched MCPlayer.
     */
    default CompletableFuture<MCPlayer> getPlayerByName(String name, MCServerAPI api) {
        return getPlayerUUIDByName(name).thenCompose(uuid -> {
            if (uuid == null) throw new NullPointerException("There is no minecraft account with that name.");
            else return api.getMCPlayerByUUID(uuid);
        });
    }

}
