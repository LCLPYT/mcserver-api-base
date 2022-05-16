/*
 * Copyright (c) 2022 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.serverapi.util;

import java.util.Set;

public class IntStatMap extends StatMap<Integer> {

    public IntStatMap(Set<String> statNames) {
        super(statNames);
    }

    public void increment(String stat, String uuid) {
        increase(stat, uuid, 1);
    }

    public void increase(String stat, String uuid, int amount) {
        set(stat, uuid, get(stat, uuid).orElse(0) + amount);
    }

    public void decrement(String stat, String uuid) {
        decrease(stat, uuid, 1);
    }

    public void decrease(String stat, String uuid, int amount) {
        increase(stat, uuid, -amount);
    }
}
