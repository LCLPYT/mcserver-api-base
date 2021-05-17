/*
 * Copyright (c) 2021 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.serverimpl.bukkit;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Config {

    public static String liveHost = "https://lclpnet.work", stagingHost = "http://localhost:8000";
    public static boolean live = false;
    public static String appName = null;
    public static boolean debug = false;

    private static final File file = new File("plugins/" + MCServerBukkit.PLUGIN_NAME + "/config.yml");
    private static FileConfiguration conf = null;
    private static boolean modified = false;

    private static boolean initConfig() {
        if(!Config.file.exists()) {
            //noinspection ResultOfMethodCallIgnored
            Config.file.getParentFile().mkdirs();
            try {
                Config.file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        conf = YamlConfiguration.loadConfiguration(Config.file);
        return true;
    }

    public static void load() {
        if(conf == null && !initConfig()) return;

        liveHost = loadValue("network.host-live", liveHost);
        stagingHost = loadValue("network.host-staging", stagingHost);
        live = loadValue("network.use-live", live);
        appName = loadValue("app-name", appName);
        debug = loadValue("debug", debug);

        if(modified) {
            modified = false;
            save();
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T loadValue(String path, T def) {
        T known = null;
        if(!conf.contains(path)) {
            conf.set(path, def);
            known = def;
            modified = true;
        }
        return known == null ? (T) conf.get(path) : known;
    }

    public static boolean save() {
        if(conf == null && !initConfig()) return false;

        try {
            conf.save(file);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}
