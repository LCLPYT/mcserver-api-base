/*
 * Copyright (c) 2021 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.serverapi;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import work.lclpnet.lclpnetwork.api.APIAccess;
import work.lclpnet.lclpnetwork.api.annotation.AuthRequired;
import work.lclpnet.lclpnetwork.api.annotation.Scopes;
import work.lclpnet.lclpnetwork.ext.LCLPMinecraftAPI;
import work.lclpnet.lclpnetwork.facade.MCPlayer;
import work.lclpnet.lclpnetwork.util.JsonBuilder;

import java.util.concurrent.CompletableFuture;

public class MCServerAPI extends LCLPMinecraftAPI {

    /**
     * Construct a new MCServerAPI object.
     *
     * @param access The API accessor to use.
     */
    public MCServerAPI(APIAccess access) {
        super(access);
    }

    /**
     * Fetches, whether a {@link MCPlayer} is a network operator.
     * Returns null, if the there is no MCPlayer with that uuid who is currently tracked by LCLPNetwork.
     *
     * @param player The MCPlayer to check.
     * @return A completable future containing whether the player with the UUID is a network operator.
     */
    @AuthRequired
    @Scopes("minecraft[admin]")
    public CompletableFuture<Boolean> isNetworkOperator(MCPlayer player) {
        return isNetworkOperator(player.getUuid());
    }

    /**
     * Fetches, whether a {@link MCPlayer} is a network operator.
     * Returns null, if the there is no MCPlayer with that uuid who is currently tracked by LCLPNetwork.
     *
     * @param playerUuid The UUID of the MCPlayer, with dashes.
     * @return A completable future containing whether the player with the UUID is a network operator.
     */
    @AuthRequired
    @Scopes("minecraft[admin]")
    public CompletableFuture<Boolean> isNetworkOperator(String playerUuid) {
        return api.post("api/mc/is-network-operator", JsonBuilder.object().set("uuid", playerUuid).createObject()).thenApply(resp -> {
            if(resp.getResponseCode() != 200) return null;

            JsonObject obj = resp.getResponseAs(JsonObject.class);
            JsonElement elem = obj.get("op");
            if(elem == null) return null;

            return elem.getAsBoolean();
        });
    }

}
