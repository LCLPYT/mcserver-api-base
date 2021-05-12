/*
 * Copyright (c) 2021 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.serverapi.cmd;

import work.lclpnet.lclpnetwork.facade.MCPlayer;
import work.lclpnet.serverapi.translate.MCMessage;
import work.lclpnet.serverapi.util.ImplementationException;
import work.lclpnet.serverapi.util.ServerCache;

import java.util.List;

public interface LanguageCommandScheme extends ICommandScheme.IPlatformCommandScheme {

    @Override
    default String getName() {
        return "language";
    }

    @Override
    default void execute(String playerUuid, Object[] args) {
        if(args.length > 1) throw new ImplementationException();

        if(args.length <= 0) { // fetch the sender's current language
            fetchCurrentLang(playerUuid);
            return;
        }

        // only string arguments are supported here
        if(!(args[0] instanceof String)) throw new ImplementationException("Unimplemented argument type '" + args[0].getClass() + "'.");

        String argument = (String) args[0];

        List<String> registeredLanguages = ServerCache.getRegisteredLanguages();
        if(registeredLanguages == null) {
            getPlatformBridge().sendMessageTo(playerUuid, MCMessage.error().thenTranslate("netlang.error.not-editable"));
            return;
        }

        if(!registeredLanguages.contains(argument)) {
            getPlatformBridge().sendMessageTo(playerUuid, MCMessage.error().thenTranslate("netlang.error.lang-not-registered"));
            return;
        }

        getAPI().setPreferredLanguage(playerUuid, argument).thenAccept(success -> {
            if(success == null || !success) getPlatformBridge().sendMessageTo(playerUuid, MCMessage.error().thenTranslate("netlang.error"));
            else {
                MCMessage langMsg = MCMessage.blank();
                if(argument.equals("auto")) langMsg.text(argument);
                else langMsg.thenTranslate("netlang.use-client");
                langMsg.setColor(MCMessage.MessageColor.YELLOW);

                getPlatformBridge().sendMessageTo(playerUuid, MCMessage.prefixed().thenTranslate("netlang.updated", langMsg));
            }
        });
    }

    default void fetchCurrentLang(String playerUuid) {
        MCPlayer player = ServerCache.getPlayer(playerUuid);
        if(player != null) {
            sendCurrentLang(player);
            return;
        }

        getAPI().getMCPlayerByUUID(playerUuid).thenAccept(pl -> {
            if(pl == null) getPlatformBridge().sendMessageTo(playerUuid, MCMessage.error().thenTranslate("netlang.error"));
            else sendCurrentLang(pl);
        });
    }

    default void sendCurrentLang(MCPlayer player) {
        String lang = player.getLanguage();

        MCMessage langMsg = MCMessage.blank();
        if(lang != null) langMsg.text(lang);
        else langMsg.thenTranslate("netlang.use-client");
        langMsg.setColor(MCMessage.MessageColor.YELLOW);

        getPlatformBridge().sendMessageTo(player.getUuid(), MCMessage.prefixed().thenTranslate("netlang.current", langMsg));
    }

}
