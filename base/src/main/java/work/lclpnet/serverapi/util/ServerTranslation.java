/*
 * Copyright (c) 2021 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.serverapi.util;

import work.lclpnet.lclpnetwork.facade.MCPlayer;
import work.lclpnet.translations.Translations;

import javax.annotation.Nullable;

public class ServerTranslation {

    @Nullable
    public static String getPreferredLanguage(String playerUuid) {
        MCPlayer player = ServerCache.getPlayer(playerUuid);
        return player == null ? null : player.getLanguage();
    }

    /**
     * This method gets a server translation for a specific player.
     * The method respects the user set preferred network language.
     *
     * @param playerUuid The UUID of the player for whom the translation should be made.
     * @param language The language which should be used when the player did not set a preferred language.
     * @param key The translation key
     * @param substitutes Translation substitutes.
     * @return The translated string.
     */
    public static String getTranslation(String playerUuid, String language, String key, Object... substitutes) {
        String prefLang = getPreferredLanguage(playerUuid);
        if(prefLang != null) language = prefLang;

        return Translations.getTranslation(language, key, substitutes);
    }

}
