/*
 * Copyright (c) 2021 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.serverapi.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class MassIncrementTransaction {

    protected final String statType;
    protected final List<IncrementTransaction> transactions = new ArrayList<>();

    public MassIncrementTransaction(String statType) {
        this.statType = statType;
    }

    protected Optional<IncrementTransaction> getTransactionFor(String uuid) {
        Objects.requireNonNull(uuid);
        return transactions.stream().filter(transaction -> transaction.getUuid().equals(uuid)).findFirst();
    }

    public MassIncrementTransaction add(String uuid, String type, int amount) {
        Objects.requireNonNull(uuid);
        Objects.requireNonNull(type);

        Optional<IncrementTransaction> existing = getTransactionFor(uuid);
        IncrementTransaction transaction;
        if(!existing.isPresent()) {
            transaction = new IncrementTransaction(uuid, new ArrayList<>());
            transactions.add(transaction);
        } else transaction = existing.get();

        Optional<IncrementTransaction.Item> existingItem = transaction.getItemFor(type);
        if(!existingItem.isPresent()) {
            IncrementTransaction.Item item = new IncrementTransaction.Item(type, amount);
            transaction.addItem(item);
        } else {
            IncrementTransaction.Item item = existingItem.get();
            item.setAmount(item.getAmount() + amount);
        }

        return this;
    }

    public String getStatType() {
        return statType;
    }

    public List<IncrementTransaction> getTransactions() {
        return transactions;
    }

}
