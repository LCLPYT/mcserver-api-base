/*
 * Copyright (c) 2023 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.serverapi.api;

import com.google.gson.annotations.Expose;
import work.lclpnet.lclpnetwork.facade.JsonSerializable;

import java.util.*;

public class IncrementTransaction extends JsonSerializable {

    @Expose
    private final String uuid;
    @Expose
    private final List<Item> items;

    public IncrementTransaction(String uuid, List<Item> items) {
        this.uuid = Objects.requireNonNull(uuid);
        this.items = Objects.requireNonNull(items);
    }

    public String getUuid() {
        return uuid;
    }

    public List<Item> getItems() {
        return items;
    }

    public void addItem(Item item) {
        items.add(Objects.requireNonNull(item));
    }

    public Optional<Item> getItemFor(String type) {
        Objects.requireNonNull(type);
        return items.stream().filter(item -> type.equals(item.type)).findFirst();
    }

    public static class Item extends JsonSerializable {

        @Expose
        private final String type;
        @Expose
        private int amount;
        @Expose
        private Map<String, Object> extra;

        public Item(String type, int amount) {
            this.type = Objects.requireNonNull(type);
            if (amount <= 0) throw new IllegalArgumentException("The amount must be greater than 0!");
            this.amount = amount;
        }

        public String getType() {
            return type;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }

        public Map<String, Object> getExtra() {
            return extra;
        }

        public void setExtra(Map<String, Object> extra) {
            this.extra = extra;
        }

        public void setExtraProperty(String key, Object value) {
            Objects.requireNonNull(key);
            Objects.requireNonNull(value);

            if (getExtra() == null) {
                setExtra(new HashMap<>());
            }

            this.extra.put(key, value);
        }
    }
}
