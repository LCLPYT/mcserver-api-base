/*
 * Copyright (c) 2021 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.serverimpl.bukkit.util;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import work.lclpnet.serverapi.translate.DefaultTranslationLoader;
import work.lclpnet.serverapi.translate.DefaultTranslationLocator;
import work.lclpnet.serverapi.translate.ServerTranslation;
import work.lclpnet.serverapi.util.ILogger;

import java.io.IOException;
import java.util.Arrays;

public class BukkitServerTranslation {

    public static void init(JavaPlugin plugin) throws IOException {
        Class<?> clazz = BukkitServerTranslation.class;
        ILogger logger = new BukkitLogger(plugin.getLogger());

        DefaultTranslationLocator locator = new DefaultTranslationLocator(clazz, logger, Arrays.asList("resource/mcsapi/lang/", "resource/bukkit/lang/"));
        DefaultTranslationLoader loader = new DefaultTranslationLoader(locator, plugin::getResource, clazz, logger);

        ServerTranslation.loadFrom(loader);
    }

    public static String getTranslation(Player player, String key, Object... substitutes) {
        return ServerTranslation.getTranslation(player.getLocale(), key, substitutes);
    }

}
