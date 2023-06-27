/*
 * Copyright (c) 2023 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.serverapi.cmd;

import work.lclpnet.serverapi.translate.MCMessage;
import work.lclpnet.serverapi.util.IPlatformBridge;

import java.util.concurrent.CompletableFuture;

public interface MCLinkCommandScheme extends ICommandScheme.IPlatformCommandScheme<Boolean>, IDebuggable {

    @Override
    default String getName() {
        return "mclink";
    }

    @Override
    default CompletableFuture<Boolean> execute(String playerUuid, Object[] args) {
        IPlatformBridge bridge = getPlatformBridge();

        bridge.sendMessageTo(playerUuid, MCMessage.prefixed()
                .thenTranslate("mc-link.requesting"));

        return getAPI().requestMCLinkReverseToken(playerUuid).exceptionally(ex -> {
            if (shouldDebug()) logError(ex);

            return null;
        }).thenApply(linkResponse -> {
            if (linkResponse == null) {
                bridge.sendMessageTo(playerUuid, MCMessage.error()
                        .thenTranslate("mc-link.error"));
                return false;
            } else if (linkResponse.isAlreadyLinked()) {
                bridge.sendMessageTo(playerUuid, MCMessage.error()
                        .thenTranslate("mc-link.already-linked"));
                return false;
            }

            String link = String.format("%s/me/mc-link/%s", getAPI().getAPIAccess().getHost(), linkResponse.getToken());

            bridge.sendMessageTo(playerUuid, MCMessage.prefixed()
                    .setColor(MCMessage.MessageColor.GREEN)
                    .thenTranslate("mc-link.open", MCMessage.blank()
                            .text(link)
                            .setColor(MCMessage.MessageColor.YELLOW)));

            return true;
        });
    }
}
