/*
 * Copyright (c) 2023 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.serverapi.util;

import work.lclpnet.serverapi.translate.MCMessage;

import java.util.Objects;

public class TransactionMessenger {

    public static TransactionMessenger getInstance() {
        return Holder.instance;
    }

    /**
     * Sends a transaction message to a player.
     * E.g. '+5 Coins'
     *
     * @param bridge     A platform bridge instance.
     * @param playerUuid The player UUID to whom the message should be sent.
     * @param statKey    The stat translation key.
     * @param amount     The amount changed. Can be positive or negative.
     */
    public void sendStatChangeMessage(IPlatformBridge bridge, String playerUuid, String statKey, int amount) {
        Objects.requireNonNull(bridge);
        Objects.requireNonNull(playerUuid);
        Objects.requireNonNull(statKey);
        if (amount == 0) return;

        boolean positive = amount > 0;

        bridge.sendMessageTo(playerUuid, MCMessage.prefixed()
                .then(MCMessage.blank()
                        .then(MCMessage.blank().text(String.format("%s%s ", positive ? "+" : "", amount)))
                        .thenTranslate(statKey)
                        .setColor(positive ? MCMessage.MessageColor.GRAY : MCMessage.MessageColor.RED)
                )
        );
    }

    public void sendCoinsChange(IPlatformBridge bridge, String playerUuid, int amount) {
        sendStatChangeMessage(bridge, playerUuid, "stat.general.coins", amount);
    }

    public void sendPointsChange(IPlatformBridge bridge, String playerUuid, int amount) {
        sendStatChangeMessage(bridge, playerUuid, "stat.general.points", amount);
    }

    // lazy loaded singleton
    private static class Holder {
        private static final TransactionMessenger instance = new TransactionMessenger();
    }
}
