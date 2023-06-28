/*
 * Copyright (c) 2023 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.serverapi.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import work.lclpnet.lclpnetwork.api.APIException;
import work.lclpnet.lclpnetwork.api.APIResponse;

import java.io.IOException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

public class MojangAPI {

    /**
     * Retrieves the current UUID linked to a username asynchronously.
     *
     * @param username The username
     * @return A completable future which will receive the fetched UUID.
     */
    public static CompletableFuture<String> getUUIDByUsername(String username) {
        return CompletableFuture.supplyAsync(() -> sendHttpGetSync(String.format("https://api.mojang.com/users/profiles/minecraft/%s", username)
        )).thenApply(resp -> {
            if (resp.getResponseCode() != 200) return null;

            JsonObject obj = resp.getResponseAs(JsonObject.class);
            JsonElement elem = obj.get("id");
            if (elem == null) return null;
            else {
                String id = elem.getAsString();
                StringBuilder builder = new StringBuilder(id.trim());
                builder.insert(20, "-");
                builder.insert(16, "-");
                builder.insert(12, "-");
                builder.insert(8, "-");
                return builder.toString();
            }
        });
    }

    /**
     * Retrieves the current username of a player UUID.
     *
     * @param uuid The UUID.
     * @return A completable future which will receive the fetched username.
     */
    public static CompletableFuture<String> getUsernameByUUID(String uuid) {
        return CompletableFuture.supplyAsync(() -> sendHttpGetSync(String.format("https://sessionserver.mojang.com/session/minecraft/profile/%s",
                uuid.replaceAll("-", "")))
        ).thenApply(resp -> {
            if (resp.getResponseCode() != 200) return null;

            JsonObject obj = resp.getResponseAs(JsonObject.class);
            if (!obj.has("name")) return null;

            return obj.get("name").getAsString();
        });
    }

    private static APIResponse sendHttpGetSync(String url) throws APIException {
        try {
            URL urlInstance = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) urlInstance.openConnection();
            conn.setRequestMethod("GET");

            APIResponse response = APIResponse.fromRequest(conn);

            conn.disconnect();

            return response;
        } catch (ConnectException e) {
            throw APIException.NO_CONNECTION;
        } catch (IOException e) {
            throw new APIException(e);
        }
    }

}
