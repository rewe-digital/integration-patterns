package com.rewedigital.examples.msintegration.composer.session;

import static java.util.stream.Collectors.toList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.spotify.apollo.Response;

// TODO evaluate idea: split into SessionRoot and SessionFragment?
public class Session {

    public interface Serializer {
        <T> Response<T> writeTo(final Response<T> response, final Map<String, String> sessionData, boolean dirty);
    }

    private static final Session emptySession = new Session(new HashMap<>());

    final SessionData data;

    private Session(final Map<String, String> data) {
        this(new SessionData(data));
    }

    private Session(final SessionData data) {
        this.data = data;
    }

    public static Session empty() {
        return emptySession;
    }

    public static <T> Session of(final Response<T> response) {
        final List<Map.Entry<String, String>> data = response.headerEntries()
            .stream()
            .filter(SessionData::isSessionEntry)
            .collect(toList());
        return new Session(toMap(data));
    }

    public Optional<String> get(final String key) {
        return data.get(key);
    }

    public Session withValuesMergedFrom(final Session other) {
        return new Session(data.mergedWith(other.data));
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
