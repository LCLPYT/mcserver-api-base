/*
 * Copyright (c) 2021 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.serverimpl.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import work.lclpnet.lclpnetwork.api.APIAccess;
import work.lclpnet.lclpnetwork.api.APIAuthAccess;
import work.lclpnet.lclpnetwork.util.Utils;
import work.lclpnet.serverapi.MCServerAPI;
import work.lclpnet.serverimpl.bukkit.event.EventListener;
import work.lclpnet.storage.LocalLCLPStorage;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MCServerBukkit extends JavaPlugin {

    public static final String PLUGIN_NAME = "MCServerAPI";
    public static final String pre = String.format("%s%s> %s", ChatColor.BLUE, PLUGIN_NAME, ChatColor.GRAY);
    private static MCServerBukkit plugin = null;
    private static MCServerAPI API = null;
    public static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(10);

    public static MCServerBukkit getPlugin() {
        return plugin;
    }

    public static MCServerAPI getAPI() {
        return API;
    }

    @Override
    public void onLoad() {
        loadConfig();

        String token;
        try {
            token = loadToken();
        } catch (IOException e) {
            e.printStackTrace();
            Bukkit.getServer().shutdown();
            return;
        }

        APIAuthAccess authAccess = new APIAuthAccess(token);
        authAccess.setHost(Config.live ? Config.liveHost : Config.stagingHost);
        authAccess.setCustomExecutor(EXECUTOR);

        try {
            authAccess = APIAccess.withAuthCheck(authAccess).join();
        } catch (CompletionException e) {
            Bukkit.getServer().shutdown();
            Bukkit.getConsoleSender().sendMessage(String.format("%s%sCould not login to LCLPNetwork... The server will shut down.", pre, ChatColor.RED));
            throw e;
        }

        API = new MCServerAPI(authAccess);
        Bukkit.getConsoleSender().sendMessage(String.format("%s%sLogged into LCLPNetwork successfully.", pre, ChatColor.GREEN));
    }

    @Override
    public void onEnable() {
        MCServerBukkit.plugin = this;

        registerListeners();

        Bukkit.getConsoleSender().sendMessage(String.format("%s%sPlugin enabled.", pre, ChatColor.GREEN));
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage("Trying to shutdown MCServer executor... (waiting up to 30 seconds)");

        EXECUTOR.shutdown();
        try {
            if(!EXECUTOR.awaitTermination(30, TimeUnit.SECONDS))
                throw new IllegalStateException("MCServer executor was terminated while some tasks were still active.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Bukkit.getConsoleSender().sendMessage(String.format("%s%sPlugin disabled.", pre, ChatColor.RED));
    }

    private void registerListeners() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new EventListener(), plugin);
    }

    private void loadConfig() {
        getConfig().options().copyDefaults(true);
        saveConfig();

        Config.load();
    }

    private String loadToken() throws IOException {
        String appName = Config.appName;
        if(appName == null) throw new IllegalStateException("Application name was not set inside of config.yml!");

        File dir = LocalLCLPStorage.getDirectory(
                appName,
                "access_tokens",
                Config.live ? "live" : "staging",
                "dedicated_server");

        File tokenFile = new File(dir, "lclpnetwork.token");
        if(!tokenFile.exists()) throw new FileNotFoundException(String.format("'%s' does not exist!", tokenFile.getAbsolutePath()));

        try (InputStream in = new FileInputStream(tokenFile)) {
            return Utils.toString(in, StandardCharsets.UTF_8);
        }
    }

}
