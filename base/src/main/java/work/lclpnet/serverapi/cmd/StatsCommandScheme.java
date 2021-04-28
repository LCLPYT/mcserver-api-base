/*
 * Copyright (c) 2021 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.serverapi.cmd;

import work.lclpnet.lclpnetwork.facade.MCStats;
import work.lclpnet.serverapi.util.IPlatformBridge;
import work.lclpnet.serverapi.util.ImplementationException;
import work.lclpnet.serverapi.util.MCMessage;

public interface StatsCommandScheme extends ICommandScheme.IPlatformCommandScheme {

    void openStats(String invokerUuid, String targetUuid, MCMessage targetName, MCStats targetStats);

    @Override
    default String getName() {
        return "stats";
    }

    @Override
    default void execute(String playerUuid, Object[] args) {
        IPlatformBridge bridge = getPlatformBridge();

        if(args.length > 1) throw new ImplementationException();

        if(args.length <= 0) { // fetch the sender's stats
            fetchStats(playerUuid, playerUuid);
            return;
        }

        // only string arguments are supported here
        if(!(args[0] instanceof String)) throw new ImplementationException("Unimplemented argument type '" + args[0].getClass() + "'.");

        String argument = (String) args[0];

        // Test if argument is an UUID.
        if(argument.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$")) {
            // First, fetch player by UUID.
            getAPI().getMCPlayerByUUID(argument).thenAccept(fetchedTarget -> {
                if(fetchedTarget == null) { // there was an error or no player was found
                    bridge.sendMessageTo(playerUuid, MCMessage.error()
                            .thenTranslate("lclp.player.not_found_uuid", MCMessage.blank()
                                    .setColor(MCMessage.MessageColor.YELLOW)
                                    .text(argument)));
                } else { // target fetched successfully.
                    fetchStats(playerUuid, fetchedTarget.getUuid());
                }
            });
        } else { // otherwise assume the argument is a username.
            bridge.getPlayerByName(argument, getAPI()).thenAccept(fetchedTarget -> {
                if(fetchedTarget == null) { // there was an error or no player was found
                    bridge.sendMessageTo(playerUuid, MCMessage.error()
                            .thenTranslate("lclp.player.not_found_name", MCMessage.blank()
                                    .setColor(MCMessage.MessageColor.YELLOW)
                                    .text(argument)));
                } else { // target fetched successfully.
                    fetchStats(playerUuid, fetchedTarget.getUuid());
                }
            });
        }
    }

    default void fetchStats(String invokerUuid, String targetUuid) {
        IPlatformBridge bridge = getPlatformBridge();

        if(!invokerUuid.equals(targetUuid)) {
            bridge.getPlayerNameByUUID(targetUuid).thenAccept(name -> {
                MCMessage nameMsg = MCMessage.blank().text(name);
                bridge.sendMessageTo(invokerUuid, MCMessage.prefixed().thenTranslate("stats.loading", nameMsg));
                fetchActual(invokerUuid, targetUuid, nameMsg);
            });
            return;
        }

        bridge.sendMessageTo(invokerUuid, MCMessage.prefixed().thenTranslate("stats.loading_yours"));
        fetchActual(invokerUuid, targetUuid, MCMessage.blank().thenTranslate("stats.yours"));
    }

    default void fetchActual(String invokerUuid, String targetUuid, MCMessage targetName) {
        IPlatformBridge bridge = getPlatformBridge();
        getAPI().getStats(targetUuid, null).thenAccept(stats -> {
            if(stats == null) {
                bridge.sendMessageTo(invokerUuid, MCMessage.prefixed().thenTranslate("stats.error"));
            } else {
                openStats(invokerUuid, targetUuid, targetName, stats);
            }
        });
    }

}
