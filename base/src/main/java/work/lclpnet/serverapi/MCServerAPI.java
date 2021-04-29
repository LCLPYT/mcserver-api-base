/*
 * Copyright (c) 2021 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.serverapi;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import work.lclpnet.lclpnetwork.api.APIAccess;
import work.lclpnet.lclpnetwork.api.APIError;
import work.lclpnet.lclpnetwork.api.annotation.AuthRequired;
import work.lclpnet.lclpnetwork.api.annotation.Scopes;
import work.lclpnet.lclpnetwork.ext.LCLPMinecraftAPI;
import work.lclpnet.lclpnetwork.facade.MCPlayer;
import work.lclpnet.lclpnetwork.util.JsonBuilder;
import work.lclpnet.serverapi.api.MCLinkResponse;

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
        return api.post("api/mc/admin/is-network-operator", JsonBuilder.object().set("uuid", playerUuid).createObject()).thenApply(resp -> {
            if(resp.getResponseCode() != 200) return null;

            JsonObject obj = resp.getResponseAs(JsonObject.class);
            JsonElement elem = obj.get("op");
            if(elem == null) return null;

            return elem.getAsBoolean();
        });
    }

    /**
     * Updates the last seen property of a {@link MCPlayer}.
     * If there is no MCPlayer with that UUID, it will be created.
     *
     * @param playerUuid The UUID of the {@link MCPlayer}.
     * @return A completable future that will contain the result of the update.
     */
    public CompletableFuture<Boolean> updateLastSeen(String playerUuid) {
        return api.post("api/mc/admin/update-last-seen", JsonBuilder.object().set("uuid", playerUuid).createObject())
                .thenApply(resp -> resp.getResponseCode() == 200);
    }

    /**
     * Processes a MCLink token (which was previously requested by the client).
     * This should only be called from modded servers, which can associate a {@link work.lclpnet.lclpnetwork.facade.User}
     * with a Minecraft account (UUID).<br>
     * <br>
     * If this call is successful, a new {@link work.lclpnet.lclpnetwork.facade.MCUser} will be created on LCLPNetwork,
     * associating a {@link work.lclpnet.lclpnetwork.facade.User} with a Minecraft UUID.
     * After that, the {@link MCPlayer} can also be associated with a {@link work.lclpnet.lclpnetwork.facade.User}.
     *
     * @param playerUuid The UUID of the minecraft player, with dashes, the integrity of those should be ensured by Minecraft's YggdrasilSessionService.
     * @param token The MCLinkToken (in form of an UUID, not to confuse with the playerUuid) that a player sent to the minecraft server.
     * @return A completable future that will contain the processing result.
     */
    public CompletableFuture<Boolean> processMCLinkToken(String playerUuid, String token) {
        return api.post("api/mc/admin/process-mclink-token", JsonBuilder.object()
                .set("mcUuid", playerUuid)
                .set("token", token)
                .createObject())
                .thenApply(resp -> resp.getResponseCode() == 201);
    }

    public CompletableFuture<MCLinkResponse> requestMCLinkReverseToken(String uuid) {
        return api.post("api/mc/admin/request-mclink-reverse-token", JsonBuilder.object()
                .set("uuid", uuid)
                .createObject()).thenApply(resp -> {
            if(resp.getResponseCode() == 422 && resp.hasValidationViolations()) {
                APIError error = resp.getValidationViolations();
                if(error.has("uuid", "The uuid has already been taken."))
                    return new MCLinkResponse(true, null);
            }
            if(resp.getResponseCode() != 201) return null;

            JsonObject obj = resp.getResponseAs(JsonObject.class);
            JsonElement elem = obj.get("token");
            if (elem == null) return null;

            return new MCLinkResponse(false, elem.getAsString());
        });
    }

}
