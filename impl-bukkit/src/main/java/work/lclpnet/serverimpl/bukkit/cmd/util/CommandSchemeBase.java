/*
 * Copyright (c) 2021 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.serverimpl.bukkit.cmd.util;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import work.lclpnet.serverapi.cmd.ICommandScheme;

public abstract class CommandSchemeBase<T> extends CommandBase implements ICommandScheme<T> {

    @Override
    public boolean canExecute(CommandSender sender) {
        return ensurePlayer(sender);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        execute(player.getUniqueId().toString(), null);
    }

    @Override
    public String getCommandName() {
        return getName();
    }
}
