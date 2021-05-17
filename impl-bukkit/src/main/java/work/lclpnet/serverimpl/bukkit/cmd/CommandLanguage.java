/*
 * Copyright (c) 2021 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.serverimpl.bukkit.cmd;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import work.lclpnet.serverapi.cmd.LanguageCommandScheme;
import work.lclpnet.serverapi.util.ServerCache;
import work.lclpnet.serverimpl.bukkit.MCServerBukkit;
import work.lclpnet.serverimpl.bukkit.cmd.util.PlatformCommandSchemeBase;

import java.util.Collections;
import java.util.List;

import static work.lclpnet.serverimpl.bukkit.util.BukkitServerTranslation.getTranslation;

public class CommandLanguage extends PlatformCommandSchemeBase<Boolean> implements LanguageCommandScheme {

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        String uuid = player.getUniqueId().toString();
        if(args.length == 0) execute(uuid, new Object[0]);
        else if(args.length == 1) execute(uuid, new Object[] { args[0] });
        else player.sendMessage(MCServerBukkit.pre + ChatColor.RED
                    + getTranslation(player, "netlang.usage", getCommandName()));
    }

    @Override
    public List<String> getTabCompletions(CommandSender sender, String[] args) {
        if(args.length == 1) return ServerCache.getRegisteredLanguages();
        else return Collections.emptyList();
    }
}
