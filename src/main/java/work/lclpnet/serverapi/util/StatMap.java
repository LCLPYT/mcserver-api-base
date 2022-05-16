/*
 * Copyright (c) 2022 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.serverapi.util;

import java.util.*;

public class StatMap<T> {

    public final Map<String, Map<String, T>> statMap;

    public StatMap(Set<String> statNames) {
        this.statMap = new HashMap<>();

        // fill stat map
        Objects.requireNonNull(statNames).forEach(stat -> statMap.put(stat, new HashMap<>()));
    }

    public Map<String, Map<String, T>> getAll() {
        return statMap;
    }

    public Optional<Map<String, T>> getStats(String stat) {
        return Optional.ofNullable(statMap.get(stat));
    }

    public Optional<T> get(String stat, String uuid) {
        return getStats(stat).map(stats -> stats.get(uuid));
    }

    public void set(String stat, String uuid, T value) {
        Objects.requireNonNull(uuid, "UUID must not be null");

        final Optional<Map<String, T>> stats = getStats(stat);
        if (!stats.isPresent())
            throw new NoSuchElementException(String.format("Stat %s is not present", stat));

        stats.get().put(uuid, value);
    }

    public void reset() {
        statMap.values().forEach(Map::clear);
    }
}
