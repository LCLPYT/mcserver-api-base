/*
 * Copyright (c) 2021 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.serverapi.cmd;

import work.lclpnet.serverapi.MCServerAPI;
import work.lclpnet.serverapi.util.IPlatformBridge;

public interface ICommandScheme {

    String getName();

    void execute(String playerUuid, Object[] args);

    public interface IPlatformCommandScheme extends ICommandScheme {

        MCServerAPI getAPI();

        IPlatformBridge getPlatformBridge();

    }

}
