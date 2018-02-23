package com.rewedigital.examples.msintegration.composer.session;

import static java.util.stream.Collectors.toList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.spotify.apollo.Response;

// TODO evaluate idea: split into SessionRoot and SessionFragment?
public class Session {

    public interface Serializer {
        <T> Response<T> writeTo(final Response<T> response, final Map<String, String> sessionData, boolean dirty);
    }

    private static final String sessionPrefix = "x-rd-";
    private static final Session emptySession = new Session(new HashMap<>());

    private final Map<String, String> data;

    private Session(final Map<String, String> data) {
        this.data = data;
    }

    public static Session empty() {
        return emptySession;
    }

    public static <T> Session of(final Response<T> response) {
        final List<Map.Entry<String, String>> data = response.headerEntries()
            .stream()
            .filter(Session::isSessionEntry)
            .collect(toList());
        return new Session(toMap(data));
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

    public Session withValuesMergedFrom(final Session other) {
        final Map<String, String> mergedData = merge(this.data, other.data);
        return new Session(mergedData);
    }

    Map<String, String> rawData() {
        return new HashMap<>(data);
    }

    private static String prefixed(final String key) {
        return sessionPrefix + key;
    }

    private static boolean isSessionEntry(final Map.Entry<String, String> entry) {
        return entry.getKey().toLowerCase().startsWith(sessionPrefix);
    }

    private static Map<String, String> toMap(final List<Map.Entry<String, String>> entries) {
        final Map<String, String> result = new HashMap<String, String>();
        // not using Collectors.toMap here due to IllegalStateException if duplicate key
        for (final Map.Entry<String, String> entry : entries) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    private static Map<String, String> merge(final Map<String, String> first, final Map<String, String> second) {
        final Map<String, String> newData = new HashMap<String, String>(first);
        newData.putAll(second);
        // prune empty values
        return newData.entrySet().stream()
            .filter(Session::nonEmptyValue)
            .collect(entryCollector());
    }

    private static boolean nonEmptyValue(final Map.Entry<String, String> entry) {
        return entry.getValue() != null && !"".equals(entry.getValue().trim());
    }

    private static Collector<Entry<String, String>, ?, Map<String, String>> entryCollector() {
        return Collectors.toMap(e -> e.getKey(), e -> e.getValue());
    }
}
