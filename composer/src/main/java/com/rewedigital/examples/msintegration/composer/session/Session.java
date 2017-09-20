package com.rewedigital.examples.msintegration.composer.session;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.spotify.apollo.Request;
import com.spotify.apollo.Response;

public class Session {
    private final List<Map.Entry<String, String>> data = new LinkedList<>();

    private static final Session EMPTY = new Session();

    private Session() {

    }

    public static Session empty() {
        return EMPTY;
    }

    public static <T> Session of(final Response<T> response) {
        return new Session();
    }

    public Request enrich(final Request request) {
        return request.withHeaders(asHeaders());
    }

    private Map<String, String> asHeaders() {
        return Collections.emptyMap();
    }

    public Optional<String> get(final String key) {
        for (final Map.Entry<String, String> entry : data) {
            if (entry.getKey().equalsIgnoreCase(key)) {
                return Optional.ofNullable(entry.getValue());
            }
        }
        return Optional.empty();
    }

    public <T> Response<T> writeTo(Response<T> response) {
        return response;
    }

    public Session mergeWith(Session other) {
        return this;
    }
}
