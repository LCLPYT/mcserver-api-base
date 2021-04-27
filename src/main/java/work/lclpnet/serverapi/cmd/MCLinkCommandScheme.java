/*
 * Copyright (c) 2021 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.serverapi.cmd;

import work.lclpnet.lclpnetwork.facade.MCPlayer;
import work.lclpnet.serverapi.util.IPlatformBridge;
import work.lclpnet.serverapi.util.MCMessage;

public interface MCLinkCommandScheme extends ICommandScheme.IPlatformCommandScheme {

    @Override
    default String getName() {
        return "mclink";
    }

    @Override
    default void execute(MCPlayer player, Object[] args) {
        IPlatformBridge bridge = getPlatformBridge();

        bridge.sendMessageTo(player, MCMessage.prefixed()
                .thenTranslate("mc-link.requesting"));

        getAPI().requestMCLinkReverseToken(player.getUuid()).thenAccept(token -> {
            if(token == null) {
                bridge.sendMessageTo(player, MCMessage.prefixed()
                        .thenTranslate("mc-link.error"));
            } else {
                String link = String.format("https://lclpnet.work/me/mc-link/%s", token);
                bridge.sendMessageTo(player, MCMessage.prefixed()
                        .setColor(MCMessage.MessageColor.GREEN)
                        .thenTranslate("mc-link.open", MCMessage.blank()
                                .text(link)
                                .setColor(MCMessage.MessageColor.YELLOW))
                );
            }
        });
    }

}
