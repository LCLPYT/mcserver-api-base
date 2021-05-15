/*
 * Copyright (c) 2021 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.serverapi.api;

import java.util.HashMap;
import java.util.Map;

public class CurrencyMassIncrementTransaction extends MassIncrementTransaction {

    public CurrencyMassIncrementTransaction() {
        super(StatTypes.CURRENCY);
    }

    /**
     * Gives a player coins. A transaction with title will be saved on LCLPNetwork.
     *
     * @param uuid The UUID of the player to give the coins to.
     * @param amount The amount of coins to add.
     * @param transactionTitle The transaction name. If this should be a translation key, the 'translated' param has to be true.
     * @param translated If the 'transactionTitle' is a translation key.
     * @return The same instance.
     */
    public CurrencyMassIncrementTransaction addCoins(String uuid, int amount, String transactionTitle, boolean translated) {
        Map<String, Object> extra = new HashMap<>();
        extra.put("name", transactionTitle);
        extra.put("translated", translated);
        return (CurrencyMassIncrementTransaction) this.add(uuid, StatItems.COINS, amount, extra);
    }

    public CurrencyMassIncrementTransaction addPoints(String uuid, int amount) {
        return (CurrencyMassIncrementTransaction) this.add(uuid, StatItems.POINTS, amount);
    }

}
