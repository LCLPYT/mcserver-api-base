/*
 * Copyright (c) 2021 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.serverimpl.bukkit.util;

import org.bukkit.inventory.Inventory;

import java.util.HashSet;
import java.util.Set;

public class StatsManager {

    private static final Set<Inventory> statsInventories = new HashSet<>();

    public static void markAsStats(Inventory inv) {
        statsInventories.add(inv);
        System.out.println(statsInventories);
    }

    public static void removeStatsMarker(Inventory inv) {
        statsInventories.remove(inv);
    }

    public static boolean isStatsInventory(Inventory inv) {
        return statsInventories.contains(inv);
    }

}
