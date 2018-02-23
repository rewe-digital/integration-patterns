package com.rewedigital.examples.msintegration.composer.session;

import static java.util.stream.Collectors.toList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.spotify.apollo.Request;
import com.spotify.apollo.Response;

public class Session {

    public interface Serializer {
        <T> Response<T> writeTo(final Response<T> response, final Map<String, String> sessionData, boolean dirty);
    }

    private static final String sessionIdKey = "session-id";
    private static final String sessionPrefix = "x-rd-";

    private static final Session emptySession = new Session(new HashMap<>(), false);

    private final Map<String, String> data;
    private final boolean dirty;

    private Session(final Map<String, String> data, final boolean dirty) {
        this.data = data;
        this.dirty = dirty;
    }

    public static Session empty() {
        return emptySession;
    }

    public static Session of(final Map<String, String> data) {
        return of(data, false);
    }
    
    public static Session of(final Map<String, String> data, final boolean dirty) {
        return new Session(new HashMap<>(data), dirty);
    }

    public static <T> Session of(final Response<T> response) {
        final List<Map.Entry<String, String>> data = response.headerEntries()
            .stream()
            .filter(Session::isSessionEntry)
            .collect(toList());
        return new Session(toMap(data), false);
    }

    public Request enrich(final Request request) {
        return request.withHeaders(asHeaders());
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

    public Optional<String> getId() {
        return get(sessionIdKey);
    }

    public <T> Response<T> writeTo(final Response<T> response, final Serializer serializer) {
        return serializer.writeTo(response, asHeaders(), dirty);
    }

    public Session withValuesMergedFrom(final Session other) {
        final Map<String, String> mergedData = merge(this.data, other.data);
        // retain original session id if present
        getId().ifPresent(i -> mergedData.put(prefixed(sessionIdKey), i));
        final boolean newDirty = !data.equals(mergedData);
        return new Session(mergedData, newDirty || dirty);
    }

    public Session withId(final String sessionId) {
        final Map<String, String> sessionData = new HashMap<>(data);
        sessionData.put(prefixed(sessionIdKey), sessionId);
        return new Session(sessionData, true);
    }

    public boolean isDirty() {
        return dirty;
    }

    public Map<String, String> rawData() {
        return new HashMap<>(data);
    }

    private Map<String, String> asHeaders() {
        return data.entrySet().stream()
            .filter(Session::isSessionEntry)
            .collect(entryCollector());
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
