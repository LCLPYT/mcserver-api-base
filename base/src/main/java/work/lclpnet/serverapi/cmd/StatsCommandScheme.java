/*
 * Copyright (c) 2021 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.serverapi.cmd;

import work.lclpnet.lclpnetwork.facade.MCPlayer;
import work.lclpnet.lclpnetwork.facade.MCStats;
import work.lclpnet.serverapi.util.IPlatformBridge;
import work.lclpnet.serverapi.util.ImplementationException;
import work.lclpnet.serverapi.util.MCMessage;

public interface StatsCommandScheme extends ICommandScheme.IPlatformCommandScheme {

    void openStats(MCPlayer player, MCPlayer target, MCMessage targetName, MCStats targetStats);

    @Override
    default String getName() {
        return "stats";
    }

    @Override
    default void execute(MCPlayer player, Object[] args) {
        IPlatformBridge bridge = getPlatformBridge();

        if(args.length > 1) throw new ImplementationException();

        if(args.length <= 0) { // fetch the sender's stats
            fetchStats(player, player);
            return;
        }

        // fetch someone else's stats
        if (args[0] instanceof MCPlayer) {
            fetchStats(player, (MCPlayer) args[0]);
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
                    bridge.sendMessageTo(player, MCMessage.error()
                            .thenTranslate("lclp.player.not_found_uuid", MCMessage.blank()
                                    .setColor(MCMessage.MessageColor.YELLOW)
                                    .text(argument)));
                } else { // target fetched successfully.
                    fetchStats(player, fetchedTarget);
                }
            });
        } else { // otherwise assume the argument is a username.
            bridge.getPlayerByName(argument,getAPI()).thenAccept(fetchedTarget -> {
                if(fetchedTarget == null) { // there was an error or no player was found
                    bridge.sendMessageTo(player, MCMessage.error()
                            .thenTranslate("lclp.player.not_found_name", MCMessage.blank()
                                    .setColor(MCMessage.MessageColor.YELLOW)
                                    .text(argument)));
                } else { // target fetched successfully.
                    fetchStats(player, fetchedTarget);
                }
            });
        }
    }

    default void fetchStats(MCPlayer invoker, MCPlayer target) {
        IPlatformBridge bridge = getPlatformBridge();

        if(!(invoker.equals(target))) {
            bridge.getPlayerName(target).thenAccept(name -> {
                MCMessage nameMsg = MCMessage.blank().text(name);
                bridge.sendMessageTo(invoker, MCMessage.prefixed().thenTranslate("stats.loading", nameMsg));
                fetchActual(invoker, target, nameMsg);
            });
            return;
        }

        bridge.sendMessageTo(invoker, MCMessage.prefixed().thenTranslate("stats.loading_yours"));
        fetchActual(invoker, target, MCMessage.blank().thenTranslate("stats.yours"));
    }

    default void fetchActual(MCPlayer invoker, MCPlayer target, MCMessage targetName) {
        IPlatformBridge bridge = getPlatformBridge();
        getAPI().getStats(target.getUuid(), null).thenAccept(stats -> {
            if(stats == null) {
                bridge.sendMessageTo(invoker, MCMessage.prefixed().thenTranslate("stats.error"));
            } else {
                openStats(invoker, target, targetName, stats);
            }
        });
    }

}
