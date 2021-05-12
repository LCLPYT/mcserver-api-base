/*
 * Copyright (c) 2021 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.serverapi.api;

public class CurrencyMassIncrementTransaction extends MassIncrementTransaction {

    public CurrencyMassIncrementTransaction() {
        super(StatTypes.CURRENCY);
    }

    public CurrencyMassIncrementTransaction addCoins(String uuid, int amount) {
        return (CurrencyMassIncrementTransaction) this.add(uuid, StatItems.COINS, amount);
    }

    public CurrencyMassIncrementTransaction addPoints(String uuid, int amount) {
        return (CurrencyMassIncrementTransaction) this.add(uuid, StatItems.POINTS, amount);
    }

}
