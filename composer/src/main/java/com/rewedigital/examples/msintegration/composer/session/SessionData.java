package com.rewedigital.examples.msintegration.composer.session;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

class SessionData {

    private static final String sessionPrefix = "x-rd-";

    private final Map<String, String> data;

    SessionData(final Map<String, String> data) {
        this.data = new HashMap<>(data);
    }

    public Optional<String> get(final String key) {
        final String lookupKey = prefixed(key);
        // not using Map.get here due to equalsIgnoreCase
        for (final Map.Entry<String, String> entry : data.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(lookupKey)) {
                return Optional.ofNullable(entry.getValue());
            }
        }
        return Optional.empty();
    }

    public SessionData with(final String key, final String value) {
        final Map<String, String> newData = new HashMap<>(data);
        newData.put(prefixed(key), value);
        return new SessionData(newData);
    }

    private static String prefixed(final String key) {
        return sessionPrefix + key;
    }

    public SessionData mergedWith(final SessionData other) {
        return new SessionData(merge(this.data, other.data));
    }

    public Map<String, String> rawData() {
        return new HashMap<>(data);
    }

    public Map<String, String> asHeaders() {
        return data.entrySet().stream()
            .filter(SessionData::isSessionEntry)
            .collect(entryCollector());
    }

    @Override
    public int hashCode() {
        return data.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final SessionData other = (SessionData) obj;
        return this.data.equals(other.data);
    }

    public static boolean isSessionEntry(final Map.Entry<String, String> entry) {
        return entry.getKey().toLowerCase().startsWith(sessionPrefix);
    }

    private static Map<String, String> merge(final Map<String, String> first, final Map<String, String> second) {
        final Map<String, String> newData = new HashMap<String, String>(first);
        newData.putAll(second);
        // prune empty values
        return newData.entrySet().stream()
            .filter(SessionData::nonEmptyValue)
            .collect(entryCollector());
    }

    private static boolean nonEmptyValue(final Map.Entry<String, String> entry) {
        return entry.getValue() != null && !"".equals(entry.getValue().trim());
    }

    private static Collector<Entry<String, String>, ?, Map<String, String>> entryCollector() {
        return Collectors.toMap(e -> e.getKey(), e -> e.getValue());
    }

}
