/*
 * Copyright (c) 2021 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.serverimpl.bukkit.util;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import work.lclpnet.serverapi.util.ServerTranslation;

import java.io.IOException;

public class ServerTranslations {

    private static ServerTranslation serverTranslation = null;

    public static void init(JavaPlugin plugin) throws IOException {
        if(serverTranslation != null) throw new IllegalStateException("Already initialized.");

        serverTranslation = new ServerTranslation(
                plugin::getResource,
                ServerTranslations.class,
                new BukkitLogger(plugin.getLogger()),
                "en_us");

        serverTranslation.load();
    }

    public static String getTranslation(Player p, String key, Object... substitutes) {
        return getTranslation(p.getLocale(), key, substitutes);
    }

    public static String getTranslation(String locale, String key, Object... substitutes) {
        return serverTranslation.getTranslation(locale, key, substitutes);
    }

}
