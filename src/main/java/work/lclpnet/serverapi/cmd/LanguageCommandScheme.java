/*
 * Copyright (c) 2023 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.serverapi.cmd;

import work.lclpnet.lclpnetwork.facade.MCPlayer;
import work.lclpnet.serverapi.msg.MCMessage;
import work.lclpnet.serverapi.util.ImplementationException;
import work.lclpnet.serverapi.util.ServerCache;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface LanguageCommandScheme extends ICommandScheme.IPlatformCommandScheme<Boolean>, IDebuggable {

    @Override
    default String getName() {
        return "language";
    }

    @Override
    default CompletableFuture<Boolean> execute(String playerUuid, Object[] args) {
        if (args.length > 1) throw new ImplementationException();

        if (args.length == 0) {
            // fetch the sender's current language
            return fetchCurrentLang(playerUuid);
        }

        // only string arguments are supported here
        if (!(args[0] instanceof String)) {
            throw new ImplementationException("Unimplemented argument type '" + args[0].getClass() + "'.");
        }

        String argument = (String) args[0];

        ServerCache cache = getContext().getCache();
        List<String> registeredLanguages = cache.getRegisteredLanguages();

        if (registeredLanguages == null) {
            getPlatformBridge().sendMessageTo(playerUuid, MCMessage.error()
                    .thenTranslate("netlang.error.not-editable"));

            return CompletableFuture.completedFuture(false);
        }

        if (!registeredLanguages.contains(argument)) {
            getPlatformBridge().sendMessageTo(playerUuid, MCMessage.error()
                    .thenTranslate("netlang.error.lang-not-registered", MCMessage.blank()
                            .text(argument)
                            .setColor(MCMessage.MessageColor.YELLOW)));

            return CompletableFuture.completedFuture(false);
        }

        return getAPI().setPreferredLanguage(playerUuid, argument).exceptionally(ex -> {
            if (shouldDebug()) logError(ex);

            return null;
        }).thenApply(success -> {
            if (success == null || !success) {
                getPlatformBridge().sendMessageTo(playerUuid, MCMessage.error().thenTranslate("netlang.error"));
                return false;
            }

            MCMessage langMsg = MCMessage.blank();

            if (argument.equals("auto")) {
                langMsg.thenTranslate("netlang.use-client");
            } else {
                langMsg.text(argument);
            }

            langMsg.setColor(MCMessage.MessageColor.YELLOW);

            getPlatformBridge().sendMessageTo(playerUuid, MCMessage.prefixed().thenTranslate("netlang.updated", langMsg));

            // load the change, can be async
            cache.refreshPlayer(getAPI(), playerUuid);

            return true;
        });
    }

    default CompletableFuture<Boolean> fetchCurrentLang(String playerUuid) {
        MCPlayer player = getContext().getCache().getPlayer(playerUuid);

        if (player != null) {
            sendCurrentLang(player);

            return CompletableFuture.completedFuture(true);
        }

        return getAPI().getMCPlayerByUUID(playerUuid).thenApply(pl -> {
            if (pl == null) {
                getPlatformBridge().sendMessageTo(playerUuid, MCMessage.error().thenTranslate("netlang.error"));
                return null;
            }

            sendCurrentLang(pl);
            return true;
        });
    }

    default void sendCurrentLang(MCPlayer player) {
        String lang = player.getLanguage();

        MCMessage langMsg = MCMessage.blank();

        if (lang != null) {
            langMsg.text(lang);
        } else {
            langMsg.thenTranslate("netlang.use-client");
        }

        langMsg.setColor(MCMessage.MessageColor.YELLOW);

        getPlatformBridge().sendMessageTo(player.getUuid(), MCMessage.prefixed()
                .thenTranslate("netlang.current", langMsg));
    }

}
