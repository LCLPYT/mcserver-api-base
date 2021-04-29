/*
 * Copyright (c) 2021 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.serverapi.api;

public class MCLinkResponse {

    private final boolean alreadyLinked;
    private final String token;

    public MCLinkResponse(boolean alreadyLinked, String token) {
        this.alreadyLinked = alreadyLinked;
        this.token = token;
    }

    public boolean isAlreadyLinked() {
        return alreadyLinked;
    }

    public String getToken() {
        return token;
    }
}
