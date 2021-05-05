/*
 * Copyright (c) 2021 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.serverimpl.bukkit.util;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import work.lclpnet.lclpnetwork.api.APIAccess;
import work.lclpnet.serverimpl.bukkit.MCServerBukkit;
import work.lclpnet.translations.Translations;
import work.lclpnet.translations.io.JarTranslationLocator;
import work.lclpnet.translations.io.ResourceTranslationLoader;
import work.lclpnet.translations.network.LCLPNetworkTranslations;
import work.lclpnet.translations.util.ILogger;

import java.io.IOException;
import java.util.Collections;

public class BukkitServerTranslation {

    public static void init(JavaPlugin plugin) throws IOException {
        Class<?> clazz = BukkitServerTranslation.class;
        ILogger logger = new BukkitLogger(plugin.getLogger());

        JarTranslationLocator locator = new JarTranslationLocator(clazz, logger, Collections.singletonList("resource/bukkit/lang/"));
        ResourceTranslationLoader loader = new ResourceTranslationLoader(locator, plugin::getResource, logger);

        Translations.loadFrom(loader);

        APIAccess.PUBLIC.setHost(MCServerBukkit.getAPI().getAPIAccess().getHost());
        LCLPNetworkTranslations.loadApplications(logger, "mc_server").join();
    }

    public static String getTranslation(Player player, String key, Object... substitutes) {
        return Translations.getTranslation(player.getLocale(), key, substitutes);
    }

}
