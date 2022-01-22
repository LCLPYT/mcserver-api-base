/*
 * Copyright (c) 2022 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.serverapi.cmd;

import work.lclpnet.lclpnetwork.facade.MCStats;
import work.lclpnet.serverapi.translate.MCMessage;
import work.lclpnet.serverapi.util.IPlatformBridge;
import work.lclpnet.serverapi.util.ImplementationException;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public interface StatsCommandScheme extends ICommandScheme.IPlatformCommandScheme<Boolean>, IDebuggable {

    void openStats(String invokerUuid, String targetUuid, MCMessage title, MCStats targetStats);

    @Override
    default String getName() {
        return "stats";
    }

    @Override
    default CompletableFuture<Boolean> execute(String playerUuid, Object[] args) {
        IPlatformBridge bridge = getPlatformBridge();

        if(args.length > 1) throw new ImplementationException();

        if(args.length <= 0) { // fetch the sender's stats
            return fetchStats(playerUuid, playerUuid);
        }

        // only string arguments are supported here
        if(!(args[0] instanceof String)) throw new ImplementationException("Unimplemented argument type '" + args[0].getClass() + "'.");

        String argument = (String) args[0];

        // Test if argument is an UUID.
        if(argument.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$")) {
            // First, fetch player by UUID.
            return getAPI().getMCPlayerByUUID(argument).thenCompose(fetchedTarget -> {
                if(fetchedTarget == null) { // there was an error or no player was found
                    bridge.sendMessageTo(playerUuid, MCMessage.error()
                            .thenTranslate("lclp.player.not_found_uuid", MCMessage.blank()
                                    .setColor(MCMessage.MessageColor.YELLOW)
                                    .text(argument)));
                    return CompletableFuture.completedFuture(null);
                } else { // target fetched successfully.
                    return fetchStats(playerUuid, fetchedTarget.getUuid());
                }
            });
        } else { // otherwise assume the argument is a username.
            bridge.sendMessageTo(playerUuid, MCMessage.prefixed()
                    .thenTranslate("mc.search_player", MCMessage.blank()
                            .setColor(MCMessage.MessageColor.YELLOW)
                            .text(argument)));

            return bridge.getPlayerByName(argument, getAPI())
                    .exceptionally(throwable -> {
                        if(throwable instanceof CompletionException) {
                            Throwable cause = throwable.getCause();
                            if(cause instanceof NullPointerException
                                    && "There is no minecraft account with that name.".equals(cause.getMessage())) {
                                bridge.sendMessageTo(playerUuid, MCMessage.error()
                                        .thenTranslate("mc.player.not_found_name", MCMessage.blank()
                                                .setColor(MCMessage.MessageColor.YELLOW)
                                                .text(argument)));
                            }
                        }
                        return null;
                    })
                    .thenCompose(fetchedTarget -> {
                        if(fetchedTarget == null) { // there was an error or no player was found
                            bridge.sendMessageTo(playerUuid, MCMessage.error()
                                    .thenTranslate("lclp.player.not_found_name", MCMessage.blank()
                                            .setColor(MCMessage.MessageColor.YELLOW)
                                            .text(argument)));
                            return CompletableFuture.completedFuture(null);
                        } else { // target fetched successfully.
                            return fetchStats(playerUuid, fetchedTarget.getUuid());
                        }
                    });
        }
    }

    default CompletableFuture<Boolean> fetchStats(String invokerUuid, String targetUuid) {
        IPlatformBridge bridge = getPlatformBridge();

        if(!invokerUuid.equals(targetUuid)) {
            return bridge.getPlayerNameByUUID(targetUuid).thenCompose(name -> {
                bridge.sendMessageTo(invokerUuid, MCMessage.prefixed().thenTranslate("stats.loading", MCMessage.blank()
                        .setColor(MCMessage.MessageColor.YELLOW)
                        .text(name)));
                return fetchActual(invokerUuid, targetUuid, MCMessage.blank().thenTranslate("stats.title.player", MCMessage.blank()
                        .text(name)));
            });
        }

        bridge.sendMessageTo(invokerUuid, MCMessage.prefixed().thenTranslate("stats.loading_yours"));
        return fetchActual(invokerUuid, targetUuid, MCMessage.blank().thenTranslate("stats.title.yours"));
    }

    default CompletableFuture<Boolean> fetchActual(String invokerUuid, String targetUuid, MCMessage title) {
        IPlatformBridge bridge = getPlatformBridge();
        return getAPI().getStats(targetUuid, null)
                .exceptionally(ex -> {
                    if(shouldDebug()) logError(ex);
                    return null;
                })
                .thenApply(stats -> {
                    if(stats == null) {
                        bridge.sendMessageTo(invokerUuid, MCMessage.prefixed().thenTranslate("stats.error"));
                        return false;
                    } else {
                        openStats(invokerUuid, targetUuid, title, stats);
                        return true;
                    }
                });
    }

}
