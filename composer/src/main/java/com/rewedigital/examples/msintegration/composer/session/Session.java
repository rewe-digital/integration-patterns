package com.rewedigital.examples.msintegration.composer.session;

import static java.util.stream.Collectors.toList;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import com.spotify.apollo.Request;
import com.spotify.apollo.Response;

public class Session {

    public interface Serializer {
        <T> Response<T> writeTo(final Response<T> response, final Map<String, String> sessionData, boolean dirty);
    }


    private static final Session emptySession = new Session(new LinkedList<>(), false);
    private static final String sessionIdKey = "sessionId";
    private static final String sessionPrefix = "x-rd-";

    private final List<Map.Entry<String, String>> data;
    private final boolean dirty;


    private Session(final List<Map.Entry<String, String>> data, final boolean dirty) {
        this.data = data;
        this.dirty = dirty;
    }

    public static Session empty() {
        return emptySession;
    }

    public static Session of(final Map<String, String> data) {
        return new Session(new LinkedList<>(data.entrySet()), false);
    }

    public static <T> Session of(final Response<T> response) {
        final List<Map.Entry<String, String>> data = response.headerEntries()
            .stream()
            .filter(Session::isSessionEntry)
            .collect(toList());
        return new Session(data, false);
    }

    public Request enrich(final Request request) {
        return request.withHeaders(asHeaders());
    }

    public Optional<String> get(final String key) {
        final String lookupKey = prefixed(key);
        for (final Map.Entry<String, String> entry : data) {
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

    public Session mergeWith(final Session other) {
        final Map<String, String> dataAsMap = toMap(data);
        final Map<String, String> newData = new HashMap<String, String>(dataAsMap);
        newData.putAll(toMap(other.data));
        final boolean newDirty = !dataAsMap.equals(newData);
        return new Session(new LinkedList<>(newData.entrySet()), newDirty || dirty);
    }

    public Session withId(final String sessionId) {
        final Map<String, String> sessionData = toMap(data);
        sessionData.put(prefixed(sessionIdKey), sessionId);
        return new Session(new LinkedList<>(sessionData.entrySet()), true);
    }

    public boolean isDirty() {
        return dirty;
    }

    private Map<String, String> asHeaders() {
        final Map<String, String> result = toMap(data);
        return result;
    }

    private static String prefixed(final String key) {
        return sessionPrefix + key;
    }

    private static boolean isSessionEntry(final Entry<String, String> entry) {
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
}
