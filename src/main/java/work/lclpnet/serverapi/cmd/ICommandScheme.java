/*
 * Copyright (c) 2022 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.serverapi.cmd;

import work.lclpnet.serverapi.MCServerAPI;
import work.lclpnet.serverapi.util.IPlatformBridge;

import java.util.concurrent.CompletableFuture;

public interface ICommandScheme<T> {

    String getName();

    CompletableFuture<T> execute(String playerUuid, Object[] args);

    interface IPlatformCommandScheme<T> extends ICommandScheme<T> {

        MCServerAPI getAPI();

        IPlatformBridge getPlatformBridge();

    }

}
