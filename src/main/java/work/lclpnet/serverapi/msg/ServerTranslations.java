/*
 * Copyright (c) 2023 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.serverapi.msg;

import work.lclpnet.lclpnetwork.facade.MCPlayer;
import work.lclpnet.serverapi.util.ServerCache;
import work.lclpnet.translations.DefaultLanguageTranslator;
import work.lclpnet.translations.Translator;
import work.lclpnet.translations.loader.translation.TranslationLoader;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class ServerTranslations {

    private final ServerCache cache;
    private final DefaultLanguageTranslator translator;

    public ServerTranslations(ServerCache cache, TranslationLoader translationLoader) {
        this.cache = cache;
        this.translator = new DefaultLanguageTranslator(translationLoader);
    }

    @Nullable
    public String getPreferredLanguage(String playerUuid) {
        MCPlayer player = cache.getPlayer(playerUuid);
        if (player == null) return null;

        return player.getLanguage();
    }

    /**
     * This method gets a server translation for a specific player.
     * The method respects the user set preferred network language.
     *
     * @param playerUuid  The UUID of the player for whom the translation should be made.
     * @param language    The language which should be used when the player did not set a preferred language.
     * @param key         The translation key
     * @param substitutes Translation substitutes.
     * @return The translated string.
     */
    public String getTranslation(String playerUuid, String language, String key, Object... substitutes) {
        String prefLang = getPreferredLanguage(playerUuid);
        if (prefLang != null) language = prefLang;

        return translator.translate(language, key, substitutes);
    }

    /**
     * This method gets a server translation for a specific player.
     * The method respects the user set preferred network language.
     *
     * @param playerUuid  The UUID of the player for whom the translation should be made.
     * @param language    The language which should be used when the player did not set a preferred language.
     * @param key         The translation key
     * @return The translated string.
     */
    public String getTranslation(String playerUuid, String language, String key) {
        String prefLang = getPreferredLanguage(playerUuid);
        if (prefLang != null) language = prefLang;

        return translator.translate(language, key);
    }

    public CompletableFuture<Void> reloadTranslations() {
        return translator.reload();
    }

    public Translator getTranslator() {
        return translator;
    }
}
