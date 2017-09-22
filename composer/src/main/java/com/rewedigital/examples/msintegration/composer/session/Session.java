package com.rewedigital.examples.msintegration.composer.session;

import static java.util.stream.Collectors.toList;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import com.spotify.apollo.Request;
import com.spotify.apollo.Response;

public class Session {

    public interface Serializer {
        Map<String, String> readFrom(final Request request);

        <T> Response<T> writeTo(final Response<T> response, final Map<String, String> sessionData);
    }

    private static String sessionPrefix = "x-rd-";

    private final List<Map.Entry<String, String>> data;

    private static final Session EMPTY = new Session();

    private Session() {
        this(new LinkedList<>());
    }

    private Session(final List<Map.Entry<String, String>> data) {
        this.data = data;
    }

    public static Session empty() {
        return EMPTY;
    }

    public static Session of(final Map<String, String> data) {
        return new Session(new LinkedList<>(data.entrySet()));
    }

    public static <T> Session of(final Response<T> response) {
        final List<Map.Entry<String, String>> data = response.headerEntries()
            .stream()
            .filter(Session::isSessionEntry)
            .collect(toList());
        return new Session(data);
    }

    private static boolean isSessionEntry(Entry<String, String> entry) {
        return entry.getKey().toLowerCase().startsWith(sessionPrefix);
    }

    public static Session of(final Request request, final Serializer serializer) {
        return Session.of(serializer.readFrom(request));
    }

    public Request enrich(final Request request) {
        return request.withHeaders(asHeaders());
    }

    private Map<String, String> asHeaders() {
        return data.stream().collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
    }

    public Optional<String> get(final String key) {
        final String lookupKey = sessionPrefix + key;
        for (final Map.Entry<String, String> entry : data) {
            if (entry.getKey().equalsIgnoreCase(lookupKey)) {
                return Optional.ofNullable(entry.getValue());
            }
        }
        return Optional.empty();
    }

    public <T> Response<T> writeTo(final Response<T> response, final Serializer serializer) {
        return serializer.writeTo(response, asHeaders());
    }

    public Session mergeWith(final Session other) {
        final List<Map.Entry<String, String>> newData = new LinkedList<>(data);
        newData.addAll(other.data);
        return new Session(newData);
    }
}
