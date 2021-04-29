/*
 * Copyright (c) 2021 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.serverimpl.bukkit.cmd;

import work.lclpnet.serverapi.MCServerAPI;
import work.lclpnet.serverapi.cmd.ICommandScheme;
import work.lclpnet.serverapi.util.IPlatformBridge;
import work.lclpnet.serverimpl.bukkit.MCServerBukkit;
import work.lclpnet.serverimpl.bukkit.util.BukkitPlatformBridge;

public abstract class PlatformCommandSchemeBase extends CommandSchemeBase implements ICommandScheme.IPlatformCommandScheme {

    @Override
    public MCServerAPI getAPI() {
        return MCServerBukkit.getAPI();
    }

    @Override
    public IPlatformBridge getPlatformBridge() {
        return BukkitPlatformBridge.getInstance();
    }

}
