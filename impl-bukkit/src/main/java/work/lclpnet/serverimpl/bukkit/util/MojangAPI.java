/*
 * Copyright (c) 2021 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.serverimpl.bukkit.util;

import com.google.gson.JsonArray;
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
            if(resp.getResponseCode() != 200) return null;

            JsonObject obj = resp.getResponseAs(JsonObject.class);
            JsonElement elem = obj.get("id");
            return elem == null ? null : elem.getAsString();
        });
    }

    /**
     * Retrieves the current username of a player UUID.
     *
     * @param uuid The UUID.
     * @return A completable future which will receive the fetched username.
     */
    public static CompletableFuture<String> getUsernameByUUID(String uuid) {
        return CompletableFuture.supplyAsync(
                () -> sendHttpGetSync(
                        String.format("https://api.mojang.com/user/profiles/%s/names", uuid.replaceAll("-", ""))
                )
        ).thenApply(resp -> {
            if(resp.getResponseCode() != 200) return null;

            JsonArray arr = resp.getResponseAs(JsonArray.class);

            String latestName = null;
            long latest = 0;

            for (JsonElement elem : arr) {
                if(!elem.isJsonObject()) continue;

                JsonObject obj = elem.getAsJsonObject();
                JsonElement changedToAt = obj.get("changedToAt");
                long time = changedToAt == null ? 0 : changedToAt.getAsLong();

                if(time < latest) continue;

                latest = time;
                JsonElement name = obj.get("name");
                if(name != null) latestName = name.getAsString();
            }

            return latestName;
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
