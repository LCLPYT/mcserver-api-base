/*
 * Copyright (c) 2021 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.serverapi.cmd;

import work.lclpnet.serverapi.util.IPlatformBridge;
import work.lclpnet.serverapi.util.MCMessage;

public interface MCLinkCommandScheme extends ICommandScheme.IPlatformCommandScheme {

    @Override
    default String getName() {
        return "mclink";
    }

    @Override
    default void execute(String playerUuid, Object[] args) {
        IPlatformBridge bridge = getPlatformBridge();

        bridge.sendMessageTo(playerUuid, MCMessage.prefixed()
                .thenTranslate("mc-link.requesting"));

        getAPI().requestMCLinkReverseToken(playerUuid).thenAccept(linkResponse -> {
            if(linkResponse == null) {
                bridge.sendMessageTo(playerUuid, MCMessage.error()
                        .thenTranslate("mc-link.error"));
            } else if(linkResponse.isAlreadyLinked()) {
                bridge.sendMessageTo(playerUuid, MCMessage.error()
                        .thenTranslate("mc-link.already-linked"));
            } else {
                String link = String.format("%s/me/mc-link/%s", getAPI().getAPIAccess().getHost(), linkResponse.getToken());
                bridge.sendMessageTo(playerUuid, MCMessage.prefixed()
                        .setColor(MCMessage.MessageColor.GREEN)
                        .thenTranslate("mc-link.open", MCMessage.blank()
                                .text(link)
                                .setColor(MCMessage.MessageColor.YELLOW))
                );
            }
        });
    }

}
